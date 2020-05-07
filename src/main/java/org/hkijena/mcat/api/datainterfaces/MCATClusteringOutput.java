package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.ClusterCentersData;
import org.hkijena.mcat.extension.datatypes.HyperstackData;

import java.util.*;

/**
 * A data interface that contains the input of an {@link org.hkijena.mcat.api.algorithms.MCATClusteringAlgorithm}
 */
public class MCATClusteringOutput implements MCATDataInterface {

    private Set<String> sourceDataSetNames;
    private Map<String, MCATClusteringOutputDataSetEntry> dataSetEntries = new HashMap<>();
    private MCATDataSlot clusterCenters = new MCATDataSlot("cluster-centers", ClusterCentersData.class);
    private MCATDataSlot clusterImages = new MCATDataSlot("cluster-image", HyperstackData.class);
    private MCATDataSlot singleClusterImage = new MCATDataSlot("single-cluster-image", HyperstackData.class);

    public MCATClusteringOutput(Set<String> sourceDataSetNames) {
        this.sourceDataSetNames = sourceDataSetNames;
    }

    public MCATDataSlot getClusterCenters() {
        return clusterCenters;
    }

    public MCATDataSlot getClusterImages() {
        return clusterImages;
    }

    public MCATDataSlot getSingleClusterImage() {
        return singleClusterImage;
    }

    @Override
    public List<MCATDataSlot> getSlots() {
        List<MCATDataSlot> result = Arrays.asList(clusterCenters, clusterImages, singleClusterImage);
        for (MCATClusteringOutputDataSetEntry value : dataSetEntries.values()) {
            result.addAll(value.getSlots());
        }
        return result;
    }

    public Set<String> getSourceDataSetNames() {
        return sourceDataSetNames;
    }

    public Map<String, MCATClusteringOutputDataSetEntry> getDataSetEntries() {
        return dataSetEntries;
    }
}
