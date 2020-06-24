package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.ClusterCentersData;
import org.hkijena.mcat.extension.datatypes.HyperstackData;

import java.util.HashMap;
import java.util.Map;

/**
 * A data interface that contains the input of an {@link org.hkijena.mcat.api.algorithms.MCATClusteringAlgorithm}
 */
public class MCATClusteringOutput implements MCATDataInterface {

    private final String groupSubject;
    private final String groupTreatment;
    private Map<String, MCATClusteringOutputDataSetEntry> dataSetEntries = new HashMap<>();
    private MCATDataSlot clusterCenters = new MCATDataSlot("cluster-centers", ClusterCentersData.class);
//    private MCATDataSlot singleClusterImage = new MCATDataSlot("single-cluster-image", HyperstackData.class);

    private int minLength = -1;

    public MCATClusteringOutput(String groupSubject, String groupTreatment) {
        this.groupSubject = groupSubject;
        this.groupTreatment = groupTreatment;
    }

    public MCATDataSlot getClusterCenters() {
        return clusterCenters;
    }


//    public MCATDataSlot getSingleClusterImage() {
//        return singleClusterImage;
//    }

    @Override
    public Map<String, MCATDataSlot> getSlots() {
        Map<String, MCATDataSlot> result = new HashMap<>();
        result.put(clusterCenters.getName(), clusterCenters);
//        result.put(singleClusterImage.getName(), singleClusterImage);
        for (Map.Entry<String, MCATClusteringOutputDataSetEntry> entry : dataSetEntries.entrySet()) {
            Map<String, MCATDataSlot> slots = entry.getValue().getSlots();
            for (Map.Entry<String, MCATDataSlot> slotEntry : slots.entrySet()) {
                result.put("entries/" + entry.getKey() + "/" + slotEntry.getKey(), slotEntry.getValue());
            }
        }
        return result;
    }

    public Map<String, MCATClusteringOutputDataSetEntry> getDataSetEntries() {
        return dataSetEntries;
    }

    /**
     * Discriminator used for grouping the data set entries together. Can be null.
     * This discriminator is derived from the data set name
     *
     * @return Discriminator used for grouping the data set entries together. Can be null.
     */
    public String getGroupSubject() {
        return groupSubject;
    }

    /**
     * Discriminator used for grouping the data set entries together. Can be null.
     * This discriminator is derived from the data set's treatment parameter
     *
     * @return Discriminator used for grouping the data set entries together. Can be null.
     */
    public String getGroupTreatment() {
        return groupTreatment;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }
}
