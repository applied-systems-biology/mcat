package org.hkijena.mcat.api.dataslots;

import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataproviders.ClusterCentersFromFileProvider;
import org.hkijena.mcat.api.datatypes.ClusterCentersData;

/**
 * Slot for {@link ClusterCentersData}
 */
public class ClusterCentersDataSlot extends MCATDataSlot<ClusterCentersData> {
    public ClusterCentersDataSlot() {
        super(ClusterCentersData.class, new ClusterCentersFromFileProvider());
    }
}
