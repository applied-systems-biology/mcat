package org.hkijena.mcat.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MCATRun implements MCATValidatable {
    private MCATProject project;
    private MCATAlgorithmGraph graph;
    private BiMap<String, MCATRunSample> samples = HashBiMap.create();

    private MCATPreprocessingParameters preprocessingParameters;
    private MCATClusteringParameters clusteringParameters;
    private MCATPostprocessingParameters postprocessingParameters;
    private boolean isReady = false;
    private Path outputPath;

    public MCATRun(MCATProject project) {
        this.project = project;
        this.preprocessingParameters = new MCATPreprocessingParameters(project.getPreprocessingParameters());
        this.clusteringParameters = new MCATClusteringParameters(project.getClusteringParameters());
        this.postprocessingParameters = new MCATPostprocessingParameters(project.getPostprocessingParameters());
        for(Map.Entry<String, MCATProjectSample> sampleEntry : project.getSamples().entrySet()) {
            samples.put(sampleEntry.getKey(), new MCATRunSample(this, sampleEntry.getValue()));
        }

        this.graph = new MCATAlgorithmGraph(this);
    }

    public BiMap<String, MCATRunSample> getSamples() {
        return ImmutableBiMap.copyOf(samples);
    }

    public MCATPreprocessingParameters getPreprocessingParameters() {
        return preprocessingParameters;
    }

    public MCATClusteringParameters getClusteringParameters() {
        return clusteringParameters;
    }

    public MCATPostprocessingParameters getPostprocessingParameters() {
        return postprocessingParameters;
    }

    @Override
    public MCATValidityReport getValidityReport() {
        return graph.getValidityReport();
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

        if(!Files.exists(outputPath)) {
            try {
                Files.createDirectories(outputPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Apply output path to the data slots
        for (Map.Entry<String, MCATRunSample> kv : samples.entrySet()) {
           for(MCATDataSlot<?> slot : kv.getValue().getSlots()) {
               slot.setStorageFilePath(outputPath.resolve(kv.getKey()).resolve(slot.getName()));
               if(!Files.exists(slot.getStorageFilePath())) {
                   try {
                       Files.createDirectories(slot.getStorageFilePath());
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }
               }
           }
        }
    }

    public void run(Consumer<Status> onProgress, Supplier<Boolean> isCancelled) {
        prepare();
        int counter = 0;
        for(MCATAlgorithm algorithm : graph.traverse()) {
            if(isCancelled.get())
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
