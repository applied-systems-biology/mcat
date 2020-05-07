package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A data interface that contains the input of an {@link org.hkijena.mcat.api.algorithms.MCATClusteringAlgorithm}
 */
public class MCATClusteringInput implements MCATDataInterface {

    private Map<String, MCATClusteringInputDataSetEntry> dataSetEntries = new HashMap<>();

    public MCATClusteringInput() {

    }

    @Override
    public List<MCATDataSlot> getSlots() {
        List<MCATDataSlot> result = new ArrayList<>();
        for (MCATClusteringInputDataSetEntry value : dataSetEntries.values()) {
            result.addAll(value.getSlots());
        }
        return result;
    }

    public Map<String, MCATClusteringInputDataSetEntry> getDataSetEntries() {
        return dataSetEntries;
    }
}
