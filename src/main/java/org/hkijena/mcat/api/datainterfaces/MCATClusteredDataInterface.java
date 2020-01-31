package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.datatypes.ClusterCentersData;
import org.hkijena.mcat.api.datatypes.HyperstackData;

public class MCATClusteredDataInterface {
    private MCATDataSlot<ClusterCentersData> clusterCenters = new MCATDataSlot<>(ClusterCentersData.class);
    private MCATDataSlot<HyperstackData> clusterImages = new MCATDataSlot<>(HyperstackData.class);

    public MCATDataSlot<ClusterCentersData> getClusterCenters() {
        return clusterCenters;
    }

    public MCATDataSlot<HyperstackData> getClusterImages() {
        return clusterImages;
    }
}
