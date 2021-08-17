/*******************************************************************************
 * Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge
 *
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 ******************************************************************************/
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
