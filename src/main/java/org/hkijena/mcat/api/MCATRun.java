package org.hkijena.mcat.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.hkijena.mcat.api.algorithms.MCATClusteringAlgorithm;
import org.hkijena.mcat.api.algorithms.MCATPostprocessingAlgorithm;
import org.hkijena.mcat.api.algorithms.MCATPreprocessingAlgorithm;
import org.hkijena.mcat.api.datainterfaces.*;
import org.hkijena.mcat.api.parameters.*;
import org.hkijena.mcat.utils.api.ACAQValidatable;
import org.hkijena.mcat.utils.api.ACAQValidityReport;

public class MCATRun implements ACAQValidatable {
    private MCATProject project;
    private MCATAlgorithmGraph graph;

    private MCATParametersTable parametersTable;
    private boolean isReady = false;
    private Path outputPath;

    public MCATRun(MCATProject project) {
        this.project = project;
        this.parametersTable = new MCATParametersTable(project.getParametersTable());
        this.graph = new MCATAlgorithmGraph();

        // Iterate through unique preprocessing parameters
        Set<MCATPreprocessingParameters> uniquePreprocessingParameters =
                parametersTable.getRows().stream().map(MCATParametersTableRow::getPreprocessingParameters).collect(Collectors.toSet());
        for (MCATPreprocessingParameters preprocessingParameters : uniquePreprocessingParameters) {
            initializePreprocessing(preprocessingParameters);
        }

    }

    /**
     * Creates the algorithm graph and data interfaces for preprocessing
     * @param preprocessingParameters the parameters
     */
    private void initializePreprocessing(MCATPreprocessingParameters preprocessingParameters) {
        List<MCATProjectDataSet> dataSetList = new ArrayList<>();
        List<MCATRawDataInterface> rawDataInterfaceList = new ArrayList<>();
        List<MCATPreprocessedDataInterface> preprocessedDataInterfaceList = new ArrayList<>();

        for (Map.Entry<String, MCATProjectDataSet> entry : project.getSamples().entrySet()) {
            MCATRawDataInterface rawDataInterface = new MCATRawDataInterface(entry.getValue().getRawDataInterface());
            MCATPreprocessedDataInterface preprocessedDataInterface = new MCATPreprocessedDataInterface();
            dataSetList.add(entry.getValue());
            rawDataInterfaceList.add(rawDataInterface);
            preprocessedDataInterfaceList.add(preprocessedDataInterface);
        }

        // Find all unique clustering parameters with prepending preprocessing
        Set<MCATClusteringParameters> uniqueClusteringParameters = new HashSet<>();
        for (MCATParametersTableRow row : parametersTable.getRows()) {
            if(row.getPreprocessingParameters().equals(preprocessingParameters)) {
                uniqueClusteringParameters.add(row.getClusteringParameters());
            }
        }

        // Go through unique clustering parameters
        for (MCATClusteringParameters clusteringParameters : uniqueClusteringParameters) {
            initializeClustering(dataSetList, rawDataInterfaceList, preprocessedDataInterfaceList, preprocessingParameters, clusteringParameters);
        }
    }

    private void initializeClustering(List<MCATProjectDataSet> dataSetList,
                                      List<MCATRawDataInterface> rawDataInterfaceList,
                                      List<MCATPreprocessedDataInterface> preprocessedDataInterfaceList,
                                      MCATPreprocessingParameters preprocessingParameters,
                                      MCATClusteringParameters clusteringParameters) {
        boolean noTreatment = clusteringParameters.getClusteringHierarchy() != MCATClusteringHierarchy.PerTreatment;
        boolean noSubject = clusteringParameters.getClusteringHierarchy() != MCATClusteringHierarchy.PerSubject;

        // Map from dataset (subject) -> treatment -> input
        Map<String, Map<String, MCATClusteringInput>> inputGroups = new HashMap<>();
        Map<String, Map<String, MCATClusteringOutput>> outputGroups = new HashMap<>();

        for (int i = 0; i < dataSetList.size(); i++) {
            MCATProjectDataSet projectDataSet = dataSetList.get(i);
            MCATRawDataInterface rawDataInterface = rawDataInterfaceList.get(i);
            MCATPreprocessedDataInterface preprocessedDataInterface = preprocessedDataInterfaceList.get(i);

            String groupSubject = projectDataSet.getName();
            String groupTreatment = projectDataSet.getParameters().getTreatment();

            if(noSubject)
                groupSubject ="";
            if(noTreatment)
                groupTreatment = "";

            // Add new entry into clustering input
            {
                Map<String, MCATClusteringInput> subjectMap = inputGroups.getOrDefault(groupSubject, null);
                if(subjectMap == null) {
                    subjectMap = new HashMap<>();
                    inputGroups.put(groupSubject, subjectMap);
                }

                MCATClusteringInput clusteringInput = subjectMap.getOrDefault(groupTreatment, null);
                if(clusteringInput == null) {
                    clusteringInput = new MCATClusteringInput(groupSubject, groupTreatment);
                    subjectMap.put(groupTreatment, clusteringInput);
                }

                clusteringInput.getDataSetEntries().put(projectDataSet.getName(),
                        new MCATClusteringInputDataSetEntry(projectDataSet.getName(),
                                rawDataInterface,
                                preprocessedDataInterface));
            }
            // Add new entry into clustering output
            {
                Map<String, MCATClusteringOutput> subjectMap = outputGroups.getOrDefault(groupSubject, null);
                if(subjectMap == null) {
                    subjectMap = new HashMap<>();
                    outputGroups.put(groupSubject, subjectMap);
                }

                MCATClusteringOutput clusteringOutput = subjectMap.getOrDefault(groupTreatment, null);
                if(clusteringOutput == null) {
                    clusteringOutput = new MCATClusteringOutput(groupSubject, groupTreatment);
                    subjectMap.put(groupTreatment, clusteringOutput);
                }

                clusteringOutput.getDataSetEntries().put(projectDataSet.getName(),
                        new MCATClusteringOutputDataSetEntry(projectDataSet.getName()));
            }
        }

        // Find all unique postprocessing parameters with prepending preprocessing
        Set<MCATPostprocessingParameters> uniquePostProcessingParameters = new HashSet<>();
        for (MCATParametersTableRow row : parametersTable.getRows()) {
            if(row.getPreprocessingParameters().equals(preprocessingParameters) &&
            row.getClusteringParameters().equals(clusteringParameters)) {
                uniquePostProcessingParameters.add(row.getPostprocessingParameters());
            }
        }

        for (MCATPostprocessingParameters postprocessingParameters : uniquePostProcessingParameters) {
            initializePostprocessing(dataSetList,
                    rawDataInterfaceList,
                    preprocessedDataInterfaceList,
                    inputGroups,
                    outputGroups,
                    preprocessingParameters,
                    clusteringParameters,
                    postprocessingParameters);
        }
    }

    private void initializePostprocessing(List<MCATProjectDataSet> dataSetList,
                                          List<MCATRawDataInterface> rawDataInterfaceList,
                                          List<MCATPreprocessedDataInterface> preprocessedDataInterfaceList,
                                          Map<String, Map<String, MCATClusteringInput>> clusteringInputGroups,
                                          Map<String, Map<String, MCATClusteringOutput>> clusteringOutputGroups,
                                          MCATPreprocessingParameters preprocessingParameters,
                                          MCATClusteringParameters clusteringParameters,
                                          MCATPostprocessingParameters postprocessingParameters) {

        List<MCATPreprocessingAlgorithm> preprocessingAlgorithmList = new ArrayList<>();
        for (int i = 0; i < dataSetList.size(); i++) {
            // Preprocessing
            MCATPreprocessingAlgorithm preprocessingAlgorithm = new MCATPreprocessingAlgorithm(this,
                    preprocessingParameters,
                    postprocessingParameters,
                    clusteringParameters,
                    rawDataInterfaceList.get(i),
                    preprocessedDataInterfaceList.get(i));
            graph.insertNode(preprocessingAlgorithm);
            preprocessingAlgorithmList.add(preprocessingAlgorithm);
        }

        for (String subject : clusteringInputGroups.keySet()) {
            for (String treatment : clusteringInputGroups.get(subject).keySet()) {
                // Clustering
                MCATClusteringInput clusteringInput = clusteringInputGroups.get(subject).get(treatment);
                MCATClusteringOutput clusteringOutput = clusteringOutputGroups.get(subject).get(treatment);

                MCATClusteringAlgorithm clusteringAlgorithm = new MCATClusteringAlgorithm(this,
                        preprocessingParameters,
                        postprocessingParameters,
                        clusteringParameters,
                        clusteringInput,
                        clusteringOutput);

                // Insert into the graph and connect
                graph.insertNode(clusteringAlgorithm);
                for (MCATPreprocessingAlgorithm preprocessingAlgorithm : preprocessingAlgorithmList) {
                    graph.connect(preprocessingAlgorithm, clusteringAlgorithm);
                }

                // Postprocessing
                MCATPostprocessingDataInterface postprocessingDataInterface = new MCATPostprocessingDataInterface();
                MCATPostprocessingAlgorithm postprocessingAlgorithm = new MCATPostprocessingAlgorithm(this,
                        preprocessingParameters,
                        postprocessingParameters,
                        clusteringParameters,
                        clusteringOutput,
                        postprocessingDataInterface);
                graph.insertNode(postprocessingAlgorithm);
                graph.connect(clusteringAlgorithm, postprocessingAlgorithm);
            }
        }
    }


    public boolean isReady() {
        return isReady;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(Path outputPath) {
        if (isReady)
            throw new RuntimeException("This run's parameters are already locked!");
        this.outputPath = outputPath;
    }


    /**
     * This function must be called before running the graph
     */
    private void prepare() {
        isReady = true;

        if (!Files.exists(outputPath)) {
            try {
                Files.createDirectories(outputPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (MCATAlgorithm node : graph.getNodes()) {
            for (MCATDataInterface outputDataInterface : node.getOutputDataInterfaces()) {
                for (Map.Entry<String, MCATDataSlot> entry : outputDataInterface.getSlots().entrySet()) {
                    Path storagePath = outputPath.resolve(node.getName()).resolve(entry.getKey()); //TODO: Identifier for parameters
                }
            }
        }

        // TODO: Write parameters + association into storage roots

    }

    public void run(Consumer<Status> onProgress, Supplier<Boolean> isCancelled) {
        prepare();
        int counter = 0;
        for (MCATAlgorithm algorithm : graph.traverse()) {
            if (isCancelled.get())
                throw new RuntimeException("Execution was cancelled");
            onProgress.accept(new Status(counter, graph.size(), algorithm.getName()));
            algorithm.run();
            ++counter;
            onProgress.accept(new Status(counter, graph.size(), algorithm.getName() + " done"));
        }
    }

    public MCATProject getProject() {
        return project;
    }

    public MCATAlgorithmGraph getGraph() {
        return graph;
    }

    @Override
    public void reportValidity(ACAQValidityReport report) {
        report.forCategory("Algorithm graph").report(graph);
    }

    public static class Status {
        private int progress;
        private int maxProgress;
        private String currentTask;

        public Status(int progress, int maxProgress, String currentTask) {
            this.progress = progress;
            this.maxProgress = maxProgress;
            this.currentTask = currentTask;
        }

        public int getProgress() {
            return progress;
        }

        public int getMaxProgress() {
            return maxProgress;
        }

        public String getCurrentTask() {
            return currentTask;
        }
    }
}
