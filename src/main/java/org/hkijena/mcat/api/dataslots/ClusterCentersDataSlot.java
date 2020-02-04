package org.hkijena.mcat.api.dataslots;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataproviders.ClusterCentersFromFileProvider;
import org.hkijena.mcat.api.datatypes.ClusterCentersData;

/**
 * Slot for {@link ClusterCentersData}
 */
@JsonSerialize(using = MCATDataSlot.Serializer.class)
public class ClusterCentersDataSlot extends MCATDataSlot<ClusterCentersData> {
    public ClusterCentersDataSlot() {
        super(ClusterCentersData.class, new ClusterCentersFromFileProvider());
    }
}
