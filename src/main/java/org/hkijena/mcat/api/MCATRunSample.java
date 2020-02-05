package org.hkijena.mcat.api;

import org.hkijena.mcat.api.datainterfaces.MCATClusteredDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATPostprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATPreprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATRawDataInterface;
import org.hkijena.mcat.api.parameters.MCATSampleParameters;

import java.util.ArrayList;
import java.util.List;

public class MCATRunSample implements MCATDataInterface {
    private MCATProjectSample sourceSample;
    private MCATRun run;

    private MCATSampleParameters parameters;
    private MCATRawDataInterface rawDataInterface;
    private MCATPreprocessedDataInterface preprocessedDataInterface;
    private MCATClusteredDataInterface clusteredDataInterface;
    private MCATPostprocessedDataInterface postprocessedDataInterface;

    public MCATRunSample(MCATRun run, MCATProjectSample source) {
        this.sourceSample = source;
        this.run = run;

        // Initialize from source sample
        this.parameters = new MCATSampleParameters(source.getParameters());
        this.rawDataInterface = new MCATRawDataInterface(source.getRawDataInterface());
        this.preprocessedDataInterface = new MCATPreprocessedDataInterface(source.getPreprocessedDataInterface());
        this.clusteredDataInterface = new MCATClusteredDataInterface(source.getClusteredDataInterface());
        this.postprocessedDataInterface = new MCATPostprocessedDataInterface(source.getPostprocessedDataInterface());
    }

    public MCATProjectSample getSourceSample() {
        return sourceSample;
    }

    public MCATRun getRun() {
        return run;
    }

    public MCATSampleParameters getParameters() {
        return parameters;
    }

    public MCATRawDataInterface getRawDataInterface() {
        return rawDataInterface;
    }

    public MCATPreprocessedDataInterface getPreprocessedDataInterface() {
        return preprocessedDataInterface;
    }

    public MCATClusteredDataInterface getClusteredDataInterface() {
        return clusteredDataInterface;
    }

    public MCATPostprocessedDataInterface getPostprocessedDataInterface() {
        return postprocessedDataInterface;
    }

    public String getName() {
        return  getRun().getSamples().inverse().get(this);
    }

    @Override
    public List<MCATDataSlot<?>> getSlots() {
        List<MCATDataSlot<?>> result = new ArrayList<>();
        result.addAll(rawDataInterface.getSlots());
        result.addAll(preprocessedDataInterface.getSlots());
        result.addAll(clusteredDataInterface.getSlots());
        result.addAll(postprocessedDataInterface.getSlots());
        return result;
    }
}
