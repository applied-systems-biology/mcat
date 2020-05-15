package org.hkijena.mcat.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.api.algorithms.MCATClusteringAlgorithm;
import org.hkijena.mcat.api.algorithms.MCATPlotGenerationAlgorithm;
import org.hkijena.mcat.api.algorithms.MCATPostprocessingAlgorithm;
import org.hkijena.mcat.api.algorithms.MCATPreprocessingAlgorithm;
import org.hkijena.mcat.api.datainterfaces.*;
import org.hkijena.mcat.api.parameters.*;
import org.hkijena.mcat.utils.JsonUtils;
import org.hkijena.mcat.api.events.ParameterChangedEvent;
import org.hkijena.mcat.api.parameters.MCATCustomParameterCollection;
import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.hkijena.mcat.api.parameters.MCATTraversedParameterCollection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MCATRun implements MCATValidatable {
    private MCATProject project;
    private MCATAlgorithmGraph graph;

    private MCATParametersTable parametersTable;
    private BiMap<MCATDataInterfaceKey, MCATDataInterface> uniqueDataInterfaces = HashBiMap.create();
    private Set<MCATDataInterfaceKey> savedDataInterfaces = new HashSet<>();
    private boolean isReady = false;
    private Path outputPath;

    public MCATRun(MCATProject project) {
        this.project = project;
        this.parametersTable = new MCATParametersTable(project.getParametersTable());
        this.graph = new MCATAlgorithmGraph();

        // Create input data keys
        for (Map.Entry<String, MCATProjectDataSet> entry : project.getDataSets().entrySet()) {
            // Create a new raw data interface
            // It is only identified by its data set name
            MCATPreprocessingInput rawDataInterface = new MCATPreprocessingInput(entry.getValue().getRawDataInterface());
            MCATDataInterfaceKey rawDataInterfaceKey = new MCATDataInterfaceKey("preprocessing-input");
            rawDataInterfaceKey.addDataSet(entry.getKey());
            registerUniqueDataInterface(rawDataInterfaceKey, rawDataInterface);
        }

        // Iterate through unique preprocessing parameters
        Set<MCATPreprocessingParameters> uniquePreprocessingParameters =
                parametersTable.getRows().stream().map(MCATParametersTableRow::getPreprocessingParameters).collect(Collectors.toSet());
        for (MCATPreprocessingParameters preprocessingParameters : uniquePreprocessingParameters) {
            initializePreprocessing(preprocessingParameters);
        }

        // Install some functionality to lock the parameters (which completely ruins some assumptions)
        for (MCATParametersTableRow row : parametersTable.getRows()) {
            row.getEventBus().register(new Object() {
                @Subscribe
                public void onParameterChanged(ParameterChangedEvent event) {
                    throw new RuntimeException("Parameter " + event.getKey() + " in " + event.getSource() + " was changed after run generation! This is not allowed. " +
                            "If you want to pass data between algorithms, use a data interface.");
                }
            });
        }
    }

    private void registerUniqueDataInterface(MCATDataInterfaceKey key, MCATDataInterface dataInterface) {
        if(uniqueDataInterfaces.containsKey(key))
            throw new RuntimeException("Found data interface key " + key + " in set! This should not be possible.");
        uniqueDataInterfaces.put(key, dataInterface);
    }

    /**
     * Creates the algorithm graph and data interfaces for preprocessing
     *
     * @param preprocessingParameters the parameters
     */
    private void initializePreprocessing(MCATPreprocessingParameters preprocessingParameters) {

        for (Map.Entry<String, MCATProjectDataSet> entry : project.getDataSets().entrySet()) {
            // Create a new preprocessed data interface
            // It is identified by its data set name and the parameters that generated it
            MCATPreprocessingOutput preprocessedDataInterface = new MCATPreprocessingOutput();
            MCATDataInterfaceKey preprocessedDataInterfaceKey = new MCATDataInterfaceKey("preprocessing-output");
            preprocessedDataInterfaceKey.addDataSet(entry.getKey());
            preprocessedDataInterfaceKey.addParameter(preprocessingParameters);
            registerUniqueDataInterface(preprocessedDataInterfaceKey, preprocessedDataInterface);
        }

        // Find all unique clustering parameters with prepending preprocessing
        Set<MCATClusteringParameters> uniqueClusteringParameters = new HashSet<>();
        for (MCATParametersTableRow row : parametersTable.getRows()) {
            if (row.getPreprocessingParameters().equals(preprocessingParameters)) {
                uniqueClusteringParameters.add(row.getClusteringParameters());
            }
        }

        // Go through unique clustering parameters
        for (MCATClusteringParameters clusteringParameters : uniqueClusteringParameters) {
            initializeClustering(preprocessingParameters, clusteringParameters);
        }
    }

    private void initializeClustering(MCATPreprocessingParameters preprocessingParameters,
                                      MCATClusteringParameters clusteringParameters) {
        boolean noTreatment = clusteringParameters.getClusteringHierarchy() != MCATClusteringHierarchy.PerTreatment;
        boolean noSubject = clusteringParameters.getClusteringHierarchy() != MCATClusteringHierarchy.PerSubject;

        Set<MCATDataInterfaceKey> matchingPreprocessedInterfaceKeys = uniqueDataInterfaces.keySet().stream()
                .filter(k -> k.getParameters().contains(preprocessingParameters) &&
                        "preprocessing-output".equals(k.getDataInterfaceName())).collect(Collectors.toSet());

        // Map from dataset (subject) -> treatment -> input
        Map<String, Map<String, MCATClusteringInput>> inputGroups = new HashMap<>();
        Map<String, Map<String, MCATClusteringOutput>> outputGroups = new HashMap<>();

        for (MCATDataInterfaceKey preprocessedInterfaceKey : matchingPreprocessedInterfaceKeys) {
            if(preprocessedInterfaceKey.getDataSetNames().size() != 1)
                throw new RuntimeException("Must have exactly one data set reference!");
            String dataSetName = preprocessedInterfaceKey.getDataSetNames().iterator().next();
            MCATDataInterfaceKey rawInterfaceKey = new MCATDataInterfaceKey("preprocessing-input");
            rawInterfaceKey.addDataSets(preprocessedInterfaceKey.getDataSetNames());
            savedDataInterfaces.add(rawInterfaceKey);

            MCATProjectDataSet projectDataSet = project.getDataSets().get(dataSetName);
            MCATPreprocessingInput rawDataInterface = (MCATPreprocessingInput) uniqueDataInterfaces.get(rawInterfaceKey);
            MCATPreprocessingOutput preprocessedDataInterface = (MCATPreprocessingOutput)uniqueDataInterfaces.get(preprocessedInterfaceKey);

            String groupSubject = projectDataSet.getName();
            String groupTreatment = projectDataSet.getParameters().getTreatment();

            if (noSubject)
                groupSubject = "";
            if (noTreatment)
                groupTreatment = "";

            // Add new entry into clustering input
            {
                Map<String, MCATClusteringInput> subjectMap = inputGroups.getOrDefault(groupSubject, null);
                if (subjectMap == null) {
                    subjectMap = new HashMap<>();
                    inputGroups.put(groupSubject, subjectMap);
                }

                MCATClusteringInput clusteringInput = subjectMap.getOrDefault(groupTreatment, null);
                if (clusteringInput == null) {
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
                if (subjectMap == null) {
                    subjectMap = new HashMap<>();
                    outputGroups.put(groupSubject, subjectMap);
                }

                MCATClusteringOutput clusteringOutput = subjectMap.getOrDefault(groupTreatment, null);
                if (clusteringOutput == null) {
                    clusteringOutput = new MCATClusteringOutput(groupSubject, groupTreatment);
                    subjectMap.put(groupTreatment, clusteringOutput);
                }

                clusteringOutput.getDataSetEntries().put(projectDataSet.getName(),
                        new MCATClusteringOutputDataSetEntry(projectDataSet.getName()));
            }
        }

        // Add data interfaces to the unique data set map
        for (String subject : inputGroups.keySet()) {
            for (String treatment : inputGroups.get(subject).keySet()) {
                MCATClusteringInput clusteringInput = inputGroups.get(subject).get(treatment);
                MCATDataInterfaceKey clusteringInputKey = new MCATDataInterfaceKey("clustering-input");
                clusteringInputKey.addParameter(preprocessingParameters);
                clusteringInputKey.addDataSets(clusteringInput.getDataSetEntries().keySet());
                registerUniqueDataInterface(clusteringInputKey, clusteringInput);
                
                MCATClusteringOutput clusteringOutput = outputGroups.get(subject).get(treatment);
                MCATDataInterfaceKey clusteringOutputKey = new MCATDataInterfaceKey("clustering-output");
                clusteringOutputKey.addParameter(preprocessingParameters);
                clusteringOutputKey.addParameter(clusteringParameters);
                clusteringOutputKey.addDataSets(clusteringInput.getDataSetEntries().keySet());
                registerUniqueDataInterface(clusteringOutputKey, clusteringOutput);
            }
        }


        // Find all unique postprocessing parameters with prepending preprocessing
        Set<MCATPostprocessingParameters> uniquePostProcessingParameters = new HashSet<>();
        for (MCATParametersTableRow row : parametersTable.getRows()) {
            if (row.getPreprocessingParameters().equals(preprocessingParameters) &&
                    row.getClusteringParameters().equals(clusteringParameters)) {
                uniquePostProcessingParameters.add(row.getPostprocessingParameters());
            }
        }

        for (MCATPostprocessingParameters postprocessingParameters : uniquePostProcessingParameters) {
            initializePostprocessing(preprocessingParameters,
                    clusteringParameters,
                    postprocessingParameters);
        }
    }

    private void initializePostprocessing(MCATPreprocessingParameters preprocessingParameters,
                                          MCATClusteringParameters clusteringParameters,
                                          MCATPostprocessingParameters postprocessingParameters) {

        List<MCATPreprocessingAlgorithm> preprocessingAlgorithmList = new ArrayList<>();
        for (MCATDataInterfaceKey preprocessingInputInterfaceKey : uniqueDataInterfaces.keySet().stream()
                .filter(k -> "preprocessing-input".equals(k.getDataInterfaceName())).collect(Collectors.toSet())) {

            MCATDataInterfaceKey preprocessingOutputInterfaceKey = new MCATDataInterfaceKey("preprocessing-output");
            preprocessingOutputInterfaceKey.addDataSets(preprocessingInputInterfaceKey.getDataSetNames());
            preprocessingOutputInterfaceKey.addParameter(preprocessingParameters);

            MCATPreprocessingInput rawDataInterface = (MCATPreprocessingInput) uniqueDataInterfaces.get(preprocessingInputInterfaceKey);
            MCATPreprocessingOutput preprocessedDataInterface = (MCATPreprocessingOutput) uniqueDataInterfaces.get(preprocessingOutputInterfaceKey);
            savedDataInterfaces.add(preprocessingOutputInterfaceKey);

            // Preprocessing
            MCATPreprocessingAlgorithm preprocessingAlgorithm = new MCATPreprocessingAlgorithm(this,
                    preprocessingParameters,
                    rawDataInterface,
                    preprocessedDataInterface);
            graph.insertNode(preprocessingAlgorithm);
            preprocessingAlgorithmList.add(preprocessingAlgorithm);
        }

        for (MCATDataInterfaceKey clusteringOutputInterfaceKey : uniqueDataInterfaces.keySet().stream().filter(k ->
                "clustering-output".equals(k.getDataInterfaceName()) && k.getParameters().contains(clusteringParameters))
                .collect(Collectors.toSet())) {
            MCATDataInterfaceKey clusteringInputInterfaceKey = new MCATDataInterfaceKey("clustering-input");
            clusteringInputInterfaceKey.addDataSets(clusteringOutputInterfaceKey.getDataSetNames());
            clusteringInputInterfaceKey.addParameter(preprocessingParameters);

            MCATClusteringInput clusteringInputInterface = (MCATClusteringInput) uniqueDataInterfaces.get(clusteringInputInterfaceKey);
            MCATClusteringOutput clusteringOutputInterface = (MCATClusteringOutput) uniqueDataInterfaces.get(clusteringOutputInterfaceKey);
            savedDataInterfaces.add(clusteringOutputInterfaceKey);

            // Create clustering algorithm node, insert it, and let it depend on preprocessing
            MCATClusteringAlgorithm clusteringAlgorithm = new MCATClusteringAlgorithm(this,
                    preprocessingParameters,
                    clusteringParameters,
                    clusteringInputInterface,
                    clusteringOutputInterface);

            graph.insertNode(clusteringAlgorithm);
            for (MCATPreprocessingAlgorithm preprocessingAlgorithm : preprocessingAlgorithmList) {
                graph.connect(preprocessingAlgorithm, clusteringAlgorithm);
            }

            // Postprocessing
            MCATPostprocessingOutput postprocessingDataInterface = new MCATPostprocessingOutput();
            MCATDataInterfaceKey postprocessingDataInterfaceKey = new MCATDataInterfaceKey("postprocessing-output");
            postprocessingDataInterfaceKey.addDataSets(clusteringOutputInterfaceKey.getDataSetNames());
            postprocessingDataInterfaceKey.addParameters(clusteringOutputInterfaceKey.getParameters());
            postprocessingDataInterfaceKey.addParameter(postprocessingParameters);
            registerUniqueDataInterface(postprocessingDataInterfaceKey, postprocessingDataInterface);
            savedDataInterfaces.add(postprocessingDataInterfaceKey);

            MCATPostprocessingAlgorithm postprocessingAlgorithm = new MCATPostprocessingAlgorithm(this,
                    preprocessingParameters,
                    postprocessingParameters,
                    clusteringParameters,
                    clusteringOutputInterface,
                    postprocessingDataInterface);
            graph.insertNode(postprocessingAlgorithm);
            graph.connect(clusteringAlgorithm, postprocessingAlgorithm);

            if(postprocessingParameters.isAnalyzeNetIncrease()) {
                initializePlotGeneration(MCATPostprocessingMethod.NetIncrease, postprocessingAlgorithm, postprocessingDataInterfaceKey);
            }
            if(postprocessingParameters.isAnalyzeNetDecrease()) {
                initializePlotGeneration(MCATPostprocessingMethod.NetDecrease, postprocessingAlgorithm, postprocessingDataInterfaceKey);
            }
            if(postprocessingParameters.isAnalyzeMaxIncrease()) {
                initializePlotGeneration(MCATPostprocessingMethod.MaxIncrease, postprocessingAlgorithm, postprocessingDataInterfaceKey);
            }
            if(postprocessingParameters.isAnalyzeMaxDecrease()) {
                initializePlotGeneration(MCATPostprocessingMethod.MaxDecrease, postprocessingAlgorithm, postprocessingDataInterfaceKey);
            }
        }
    }

    private void initializePlotGeneration(MCATPostprocessingMethod method,
                                          MCATPostprocessingAlgorithm postprocessingAlgorithm,
                                          MCATDataInterfaceKey postprocessingDataInterfaceKey) {
        MCATAUCDataConditions conditions = new MCATAUCDataConditions(method);
        // Create output data
        MCATDataInterfaceKey plotDataInterfaceKey = new MCATDataInterfaceKey("plots");
        plotDataInterfaceKey.addDataSets(postprocessingDataInterfaceKey.getDataSetNames());
        plotDataInterfaceKey.addParameters(postprocessingDataInterfaceKey.getParameters());
        plotDataInterfaceKey.addParameter(conditions);

        MCATPlotGenerationOutput plotGenerationOutput = new MCATPlotGenerationOutput();
        registerUniqueDataInterface(plotDataInterfaceKey, plotGenerationOutput);
        savedDataInterfaces.add(plotDataInterfaceKey);

        // Create the algorithm instance
        MCATPlotGenerationAlgorithm plotGenerationAlgorithm = new MCATPlotGenerationAlgorithm(this,
                postprocessingAlgorithm.getPreprocessingParameters(),
                postprocessingAlgorithm.getPostprocessingParameters(),
                postprocessingAlgorithm.getClusteringParameters(),
                conditions,
                postprocessingAlgorithm.getClusteringOutput(),
                postprocessingAlgorithm.getPostprocessingOutput(),
                plotGenerationOutput);
        graph.insertNode(plotGenerationAlgorithm);
        graph.connect(postprocessingAlgorithm, plotGenerationAlgorithm);
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
     * Finds the parameter keys in the parameter table that contain information
     * @return parameter keys
     */
    private Set<String> getRelevantParameterKeys() {
        Set<String> result = new HashSet<>();
        for (int column = 0; column < parametersTable.getColumnCount(); column++) {
            String key = parametersTable.getColumnKey(column);
            Set<Object> values = new HashSet<>();
            for (int row = 0; row < parametersTable.getRowCount(); row++) {
                values.add(parametersTable.getValueAt(row, column));
            }
            if(values.size() > 1) {
                result.add(key);
            }
        }
        return result;
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

        try {
            JsonUtils.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(outputPath.resolve("project.json").toFile(), project);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MCATResultDataInterfaces exportedSavedInterfaces = new MCATResultDataInterfaces();
        for (MCATDataInterfaceKey key : savedDataInterfaces) {
            MCATResultDataInterfaces.DataInterfaceEntry dataInterfaceEntry = setDataInterfaceStoragePath(key, uniqueDataInterfaces.get(key));
            exportedSavedInterfaces.getEntries().add(dataInterfaceEntry);
        }
        try {
            JsonUtils.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(outputPath.resolve("data.json").toFile(), exportedSavedInterfaces);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MCATResultDataInterfaces.DataInterfaceEntry setDataInterfaceStoragePath(MCATDataInterfaceKey key, MCATDataInterface dataInterface) {
        MCATResultDataInterfaces.DataInterfaceEntry result = new MCATResultDataInterfaces.DataInterfaceEntry(key);

        String interfaceType = key.getDataInterfaceName();
        String dataSetString = key.getDataSetNames().stream().sorted().collect(Collectors.joining(","));
        Set<MCATParameterAccess> parameterAccesses = new HashSet<>();
        for (MCATParameterCollection parameterCollection : key.getParameters()) {
            parameterAccesses.addAll((new MCATTraversedParameterCollection(parameterCollection)).getParameters().values());
        }
        String parameterString = MCATCustomParameterCollection.parametersToString(parameterAccesses, ",", "=");
        result.setParameterString(parameterString);

        for (Map.Entry<String, MCATDataSlot> slotEntry : dataInterface.getSlots().entrySet()) {
            Path slotPath = outputPath.resolve(interfaceType).resolve(parameterString).resolve(dataSetString).resolve(slotEntry.getValue().getName());
            if (!Files.exists(slotPath)) {
                try {
                    Files.createDirectories(slotPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            slotEntry.getValue().setStorageFilePath(slotPath);

            MCATResultDataInterfaces.SlotEntry exportedSlotEntry = new MCATResultDataInterfaces.SlotEntry(slotEntry.getKey(), outputPath.relativize(slotPath));
            result.getSlots().add(exportedSlotEntry);
        }

        return result;
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
    public void reportValidity(MCATValidityReport report) {
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
