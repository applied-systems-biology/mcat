package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.ClusterCentersData;
import org.hkijena.mcat.extension.datatypes.HyperstackData;

import java.util.Arrays;
import java.util.List;

/**
 * Organizes clustered data
 */
public class MCATClusteredDataInterface implements MCATDataInterface {
    private MCATDataSlot clusterCenters = new MCATDataSlot("cluster-centers", ClusterCentersData.class);
    private MCATDataSlot clusterImages = new MCATDataSlot("cluster-image", HyperstackData.class);
    private MCATDataSlot singleClusterImage = new MCATDataSlot("single-cluster-image", HyperstackData.class);

    public MCATClusteredDataInterface() {

    }

    public MCATClusteredDataInterface(MCATClusteredDataInterface other) {
        this.clusterCenters = new MCATDataSlot(other.getClusterCenters());
        this.clusterImages = new MCATDataSlot(other.getClusterImages());
        this.singleClusterImage = new MCATDataSlot(other.getSingleClusterImage());
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
        return Arrays.asList(clusterCenters, clusterImages, singleClusterImage);
    }
}
