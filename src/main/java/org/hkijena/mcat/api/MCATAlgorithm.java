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
public abstract class MCATAlgorithm implements MCATValidatable, Runnable {

    private MCATRunSample sample;

    public MCATAlgorithm(MCATRunSample sample) {
        this.sample = sample;
    }

    public abstract void run();

    public abstract String getName();

    public MCATRun getRun() {
        return sample.getRun();
    }

    public MCATRunSample getSample() {
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
        return sample.getRun().getClusteringParameters();
    }

    public MCATPreprocessingParameters getPreprocessingParameters() {
        return sample.getRun().getPreprocessingParameters();
    }

    public MCATPostprocessingParameters getPostprocessingParameters() {
        return sample.getRun().getPostprocessingParameters();
    }
}
