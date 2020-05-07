package org.hkijena.mcat.api;

import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
import org.hkijena.mcat.utils.api.ACAQValidatable;

import java.util.List;
import java.util.Set;

/**
 * Base class for an algorithm node
 * Please use the provided properties to access data and parameters to later allow easy extension to hyperparameters
 */
public abstract class MCATAlgorithm implements ACAQValidatable, Runnable {

    private MCATRun run;
    private MCATPreprocessingParameters preprocessingParameters;
    private MCATPostprocessingParameters postprocessingParameters;
    private MCATClusteringParameters clusteringParameters;

    public MCATAlgorithm(MCATRun run, MCATPreprocessingParameters preprocessingParameters, MCATPostprocessingParameters postprocessingParameters, MCATClusteringParameters clusteringParameters) {
        this.run = run;
        this.preprocessingParameters = preprocessingParameters;
        this.postprocessingParameters = postprocessingParameters;
        this.clusteringParameters = clusteringParameters;
    }

    public abstract void run();

    public abstract String getName();

    public MCATRun getRun() {
        return run;
    }

    public MCATPreprocessingParameters getPreprocessingParameters() {
        return preprocessingParameters;
    }

    public MCATPostprocessingParameters getPostprocessingParameters() {
        return postprocessingParameters;
    }

    public MCATClusteringParameters getClusteringParameters() {
        return clusteringParameters;
    }

    public abstract List<MCATDataInterface> getInputDataInterfaces();

    public abstract List<MCATDataInterface> getOutputDataInterfaces();
}
