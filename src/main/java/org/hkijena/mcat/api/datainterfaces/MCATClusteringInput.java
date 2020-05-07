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

    private final String groupSubject;
    private final String groupTreatment;
    private Map<String, MCATClusteringInputDataSetEntry> dataSetEntries = new HashMap<>();

    public MCATClusteringInput(String groupSubject, String groupTreatment) {
        this.groupSubject = groupSubject;
        this.groupTreatment = groupTreatment;
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

    /**
     * Discriminator used for grouping the data set entries together. Can be null.
     * This discriminator is derived from the data set name
     * @return Discriminator used for grouping the data set entries together. Can be null.
     */
    public String getGroupSubject() {
        return groupSubject;
    }

    /**
     * Discriminator used for grouping the data set entries together. Can be null.
     * This discriminator is derived from the data set's treatment parameter
     * @return Discriminator used for grouping the data set entries together. Can be null.
     */
    public String getGroupTreatment() {
        return groupTreatment;
    }
}
