package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.ClusterAbundanceData;

import java.util.Collections;
import java.util.List;

public class MCATClusteringOutputDataSetEntry implements MCATDataInterface {
    private String dataSetName;
    private MCATDataSlot clusterAbundance = new MCATDataSlot("cluster-abundance", ClusterAbundanceData.class);

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
    public List<MCATDataSlot> getSlots() {
        return Collections.singletonList(clusterAbundance);
    }
}
