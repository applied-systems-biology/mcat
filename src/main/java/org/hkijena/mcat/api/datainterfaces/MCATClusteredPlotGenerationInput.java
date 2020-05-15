package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains multiple {@link MCATClusteringOutput}
 */
public class MCATClusteredPlotGenerationInput implements MCATDataInterface {

    private Map<String, MCATClusteringOutput> clusteringOutputMap = new HashMap<>();

    public MCATClusteredPlotGenerationInput() {
    }

    public Map<String, MCATClusteringOutput> getClusteringOutputMap() {
        return clusteringOutputMap;
    }

    public void setClusteringOutputMap(Map<String, MCATClusteringOutput> clusteringOutputMap) {
        this.clusteringOutputMap = clusteringOutputMap;
    }

    @Override
    public Map<String, MCATDataSlot> getSlots() {
        Map<String, MCATDataSlot> result = new HashMap<>();
        for (Map.Entry<String, MCATClusteringOutput> entry : clusteringOutputMap.entrySet()) {
            for (Map.Entry<String, MCATDataSlot> clusterEntry : entry.getValue().getSlots().entrySet()) {
                result.put(entry.getKey() + "/" + clusterEntry.getKey(), clusterEntry.getValue());
            }
        }
        return result;
    }
}
