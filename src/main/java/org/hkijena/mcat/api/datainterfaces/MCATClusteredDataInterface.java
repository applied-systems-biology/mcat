package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataslots.ClusterCentersDataSlot;
import org.hkijena.mcat.api.dataslots.HyperstackDataSlot;

import java.util.Arrays;
import java.util.List;

/**
 * Organizes clustered data
 */
public class MCATClusteredDataInterface implements MCATDataInterface {
    private ClusterCentersDataSlot clusterCenters = new ClusterCentersDataSlot("cluster-centers");
    private HyperstackDataSlot clusterImages = new HyperstackDataSlot("cluster-image");

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

    @Override
    public List<MCATDataSlot<?>> getSlots() {
        return Arrays.asList(clusterCenters, clusterImages);
    }
}
