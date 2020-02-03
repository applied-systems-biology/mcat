package org.hkijena.mcat.api;

import org.hkijena.mcat.api.datainterfaces.MCATClusteredDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATPostprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATPreprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATRawDataInterface;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
import org.hkijena.mcat.api.parameters.MCATSampleParameters;

/**
 * Base class for an algorithm node
 * Please use the provided properties to access data and parameters to later allow easy extension to hyperparameters
 */
public abstract class MCATAlgorithm implements MCATValidatable {

    private MCATSample sample;

    public MCATAlgorithm(MCATSample sample) {
        this.sample = sample;
    }

    public abstract void run();

    public MCATProject getProject() {
        return sample.getProject();
    }

    public MCATSample getSample() {
        return sample;
    }

    public MCATRawDataInterface getRawData() {
        return sample.getRawDataInterface();
    }

    public MCATPreprocessedDataInterface getPreprocessedData() {
        return sample.getPreprocessedDataInterface();
    }

    public MCATClusteredDataInterface getClusteredDataInterface() {
        return sample.getClusteredDataInterface();
    }

    public MCATPostprocessedDataInterface getPostprocessedData() {
        return sample.getPostprocessedDataInterface();
    }

    public MCATSampleParameters getSampleParameters() {
        return sample.getParameters();
    }

    public MCATClusteringParameters getClusteringParameters() {
        return sample.getProject().getClusteringParameters();
    }

    public MCATPreprocessingParameters getPreprocessingParameters() {
        return sample.getProject().getPreprocessingParameters();
    }

    public MCATPostprocessingParameters getPostprocessingParameters() {
        return sample.getProject().getPostprocessingParameters();
    }
}
