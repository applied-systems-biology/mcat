package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;

import java.util.Map;

/**
 * A data interface that contains the input of an {@link org.hkijena.mcat.api.algorithms.MCATClusteringAlgorithm}
 */
public class MCATClusteringInputDataSetEntry implements MCATDataInterface {

    private String dataSetName;
    private MCATPreprocessingInput rawDataInterface;
    private MCATPreprocessingOutput preprocessedDataInterface;

    public MCATClusteringInputDataSetEntry(String dataSetName, MCATPreprocessingInput rawDataInterface, MCATPreprocessingOutput preprocessedDataInterface) {
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

    public MCATPreprocessingOutput getPreprocessedDataInterface() {
        return preprocessedDataInterface;
    }

    public MCATPreprocessingInput getRawDataInterface() {
        return rawDataInterface;
    }
}
