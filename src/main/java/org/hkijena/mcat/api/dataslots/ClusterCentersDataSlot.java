package org.hkijena.mcat.api.dataslots;

import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataproviders.ClusterCentersFromFileProvider;
import org.hkijena.mcat.api.datatypes.ClusterCentersData;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Slot for {@link ClusterCentersData}
 */
@JsonSerialize(using = MCATDataSlot.Serializer.class)
public class ClusterCentersDataSlot extends MCATDataSlot<ClusterCentersData> {

    public ClusterCentersDataSlot(String name) {
        super(name, ClusterCentersData.class, new ClusterCentersFromFileProvider());
    }

    public ClusterCentersDataSlot(ClusterCentersDataSlot other) {
        super(other);
    }
}
