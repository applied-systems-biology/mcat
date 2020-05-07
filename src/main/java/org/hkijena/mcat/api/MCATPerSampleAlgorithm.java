package org.hkijena.mcat.api;

import org.hkijena.mcat.api.datainterfaces.MCATClusteredDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATPostprocessedDataInterface;

public abstract class MCATPerSampleAlgorithm extends MCATAlgorithm {
    private MCATRunSample sample;

    public MCATPerSampleAlgorithm(MCATRunSample sample) {
        super(sample.getRun(), preprocessingParameters, postprocessingParameters, clusteringParameters);
        this.sample = sample;
    }

    public MCATRunSample getSample() {
        return sample;
    }

    public MCATClusteredDataInterface getClusteredDataInterface() {
        return sample.getClusteredDataInterface();
    }

    public MCATPostprocessedDataInterface getPostprocessedDataInterface() {
        return sample.getPostprocessedDataInterface();
    }
}
