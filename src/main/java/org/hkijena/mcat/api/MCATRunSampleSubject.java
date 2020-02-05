package org.hkijena.mcat.api;

import org.hkijena.mcat.api.datainterfaces.MCATPreprocessedDataInterface;
import org.hkijena.mcat.api.datainterfaces.MCATRawDataInterface;
import org.hkijena.mcat.api.parameters.MCATSampleParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MCATRunSampleSubject implements MCATDataInterface, Comparable<MCATRunSampleSubject> {

    private MCATRunSample sample;
    private MCATSampleParameters parameters;
    private MCATRawDataInterface rawDataInterface;
    private MCATPreprocessedDataInterface preprocessedDataInterface;

    public MCATRunSampleSubject(MCATRunSample sample, MCATProjectSample source) {
        this.sample = sample;
        this.parameters = new MCATSampleParameters(source.getParameters());
        this.rawDataInterface = new MCATRawDataInterface(source.getRawDataInterface());
        this.preprocessedDataInterface = new MCATPreprocessedDataInterface(source.getPreprocessedDataInterface());
    }

    @Override
    public List<MCATDataSlot<?>> getSlots() {
        List<MCATDataSlot<?>> result = new ArrayList<>();
        result.addAll(rawDataInterface.getSlots());
        result.addAll(preprocessedDataInterface.getSlots());
        return result;
    }

    public MCATRunSample getSample() {
        return sample;
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

    public String getName() {
        return sample.getSubjects().inverse().get(this);
    }

    @Override
    public int compareTo(MCATRunSampleSubject o) {
        return getName().compareTo(o.getName());
    }
}
