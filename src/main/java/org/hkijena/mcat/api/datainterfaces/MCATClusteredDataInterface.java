package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.dataslots.ClusterCentersDataSlot;
import org.hkijena.mcat.api.dataslots.HyperstackDataSlot;

public class MCATClusteredDataInterface {
    private ClusterCentersDataSlot clusterCenters = new ClusterCentersDataSlot();
    private HyperstackDataSlot clusterImages = new HyperstackDataSlot();

    public ClusterCentersDataSlot getClusterCenters() {
        return clusterCenters;
    }

    public HyperstackDataSlot getClusterImages() {
        return clusterImages;
    }
}
