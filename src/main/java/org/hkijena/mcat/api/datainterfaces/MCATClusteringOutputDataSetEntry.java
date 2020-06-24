package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.ClusterAbundanceData;
import org.hkijena.mcat.extension.datatypes.HyperstackData;

import java.util.HashMap;
import java.util.Map;

public class MCATClusteringOutputDataSetEntry implements MCATDataInterface {
    private String dataSetName;
    private MCATDataSlot clusterAbundance = new MCATDataSlot("cluster-abundance", ClusterAbundanceData.class);
    private MCATDataSlot clusterImages = new MCATDataSlot("cluster-image", HyperstackData.class);

    public MCATClusteringOutputDataSetEntry(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public MCATDataSlot getClusterAbundance() {
        return clusterAbundance;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    @Override
    public Map<String, MCATDataSlot> getSlots() {
        Map<String, MCATDataSlot> result = new HashMap<>();
        result.put(clusterAbundance.getName(), clusterAbundance);
        result.put(clusterImages.getName(), clusterImages);
        return result;
    }

    public MCATDataSlot getClusterImages() {
        return clusterImages;
    }
}
