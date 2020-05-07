package org.hkijena.mcat.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.hkijena.mcat.api.algorithms.MCATPreprocessingAlgorithm;
import org.hkijena.mcat.api.datainterfaces.MCATPreprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATRawDataInterface;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATParametersTable;
import org.hkijena.mcat.api.parameters.MCATParametersTableRow;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
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
        List<MCATRawDataInterface> rawDataInterfaceList = new ArrayList<>();
        List<MCATPreprocessedDataInterface> preprocessedDataInterfaceList = new ArrayList<>();

        for (Map.Entry<String, MCATProjectDataSet> entry : project.getSamples().entrySet()) {
            MCATRawDataInterface rawDataInterface = new MCATRawDataInterface(entry.getValue().getRawDataInterface());
            MCATPreprocessedDataInterface preprocessedDataInterface = new MCATPreprocessedDataInterface();
            rawDataInterfaceList.add(rawDataInterface);
            preprocessedDataInterfaceList.add(preprocessedDataInterface);
        }

        // Find all unique clustering parameters with prepending preprocessing
//            Set<MCATClusteringParameters> uniqueClusteringParameters = new HashSet<>();
//            for (MCATParametersTableRow row : parametersTable.getRows()) {
//                if(row.getPreprocessingParameters().equals(preprocessingParameters)) {
//                    uniqueClusteringParameters.add(row.getClusteringParameters());
//                }
//            }
//
//            // Go through unique clustering parameters
//            for (MCATClusteringParameters clusteringParameters : uniqueClusteringParameters) {
//                initializeClustering(rawDataInterface, preprocessedDataInterface, preprocessingParameters, clusteringParameters);
//            }
    }

    private void initializeClustering(MCATRawDataInterface rawDataInterface,
                                      MCATPreprocessedDataInterface preprocessedDataInterface,
                                      MCATPreprocessingParameters preprocessingParameters,
                                      MCATClusteringParameters clusteringParameters) {
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

//        // Apply output path to the data slots
//        for (Map.Entry<String, MCATRunSample> kv : samples.entrySet()) {
//
//            // Apply output path to the data slots
//            for (MCATDataSlot slot : kv.getValue().getSlots()) {
//                slot.setStorageFilePath(outputPath.resolve(kv.getKey()).resolve(slot.getName()));
//                if (!Files.exists(slot.getStorageFilePath())) {
//                    try {
//                        Files.createDirectories(slot.getStorageFilePath());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//
//            // Do the same for the subjects
//            for (Map.Entry<String, MCATRunSampleSubject> kv2 : kv.getValue().getSubjects().entrySet()) {
//                for (MCATDataSlot slot : kv2.getValue().getSlots()) {
//                    slot.setStorageFilePath(outputPath.resolve(kv.getKey()).resolve(kv2.getKey()).resolve(slot.getName()));
//                    if (!Files.exists(slot.getStorageFilePath())) {
//                        try {
//                            Files.createDirectories(slot.getStorageFilePath());
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//            }
//        }
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
