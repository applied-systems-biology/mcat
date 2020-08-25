/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/
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
