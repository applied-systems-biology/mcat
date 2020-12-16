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

import java.util.HashMap;
import java.util.Map;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.ClusterAbundanceData;
import org.hkijena.mcat.extension.datatypes.HyperstackData;

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
