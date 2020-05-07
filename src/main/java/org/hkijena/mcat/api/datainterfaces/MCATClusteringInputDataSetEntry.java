package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;

import java.util.Map;

/**
 * A data interface that contains the input of an {@link org.hkijena.mcat.api.algorithms.MCATClusteringAlgorithm}
 */
public class MCATClusteringInputDataSetEntry implements MCATDataInterface {

    private String dataSetName;
    private MCATRawDataInterface rawDataInterface;
    private MCATPreprocessedDataInterface preprocessedDataInterface;

    public MCATClusteringInputDataSetEntry(String dataSetName, MCATRawDataInterface rawDataInterface, MCATPreprocessedDataInterface preprocessedDataInterface) {
        this.dataSetName = dataSetName;
        this.rawDataInterface = rawDataInterface;
        this.preprocessedDataInterface = preprocessedDataInterface;
    }

    @Override
    public Map<String, MCATDataSlot> getSlots() {
        return preprocessedDataInterface.getSlots();
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public MCATPreprocessedDataInterface getPreprocessedDataInterface() {
        return preprocessedDataInterface;
    }

    public MCATRawDataInterface getRawDataInterface() {
        return rawDataInterface;
    }
}
