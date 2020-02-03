package org.hkijena.mcat.api;

import org.hkijena.mcat.api.datainterfaces.MCATClusteredDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATPreprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATRawDataInterface;

public abstract class MCATAlgorithm {

    private MCATSample sample;
    private MCATRawDataInterface rawData;
    private MCATPreprocessedDataInterface preprocessedData;
    private MCATClusteredDataInterface postprocessedData;

    protected MCATAlgorithm(MCATSample sample, MCATRawDataInterface rawData, MCATPreprocessedDataInterface preprocessedData, MCATClusteredDataInterface postprocessedData) {
        this.sample = sample;
        this.rawData = rawData;
        this.preprocessedData = preprocessedData;
        this.postprocessedData = postprocessedData;
    }

    public abstract void run();

    public MCATProject getProject() {
        return sample.getProject();
    }

    public MCATSample getSample() {
        return sample;
    }

    public MCATRawDataInterface getRawData() {
        return rawData;
    }

    public MCATPreprocessedDataInterface getPreprocessedData() {
        return preprocessedData;
    }

    public MCATClusteredDataInterface getPostprocessedData() {
        return postprocessedData;
    }
}
