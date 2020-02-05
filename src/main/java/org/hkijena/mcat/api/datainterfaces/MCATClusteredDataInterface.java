package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.dataslots.ClusterCentersDataSlot;
import org.hkijena.mcat.api.dataslots.HyperstackDataSlot;

/**
 * Organizes clustered data
 */
public class MCATClusteredDataInterface {
    private ClusterCentersDataSlot clusterCenters = new ClusterCentersDataSlot();
    private HyperstackDataSlot clusterImages = new HyperstackDataSlot();

    public MCATClusteredDataInterface() {

    }

    public MCATClusteredDataInterface(MCATClusteredDataInterface other) {
        this.clusterCenters = new ClusterCentersDataSlot(other.getClusterCenters());
        this.clusterImages = new HyperstackDataSlot(other.getClusterImages());
    }

    public ClusterCentersDataSlot getClusterCenters() {
        return clusterCenters;
    }

    public HyperstackDataSlot getClusterImages() {
        return clusterImages;
    }
}
