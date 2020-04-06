package org.hkijena.mcat.api.dataslots;

import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataproviders.ClusterAbundanceFromFileProvider;
import org.hkijena.mcat.api.datatypes.ClusterAbundanceData;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Slot for {@link ClusterAbundanceData}
 */
@JsonSerialize(using = MCATDataSlot.Serializer.class)
public class ClusterAbundanceDataSlot extends MCATDataSlot<ClusterAbundanceData> {

    public ClusterAbundanceDataSlot(String name) {
        super(name, ClusterAbundanceData.class, new ClusterAbundanceFromFileProvider());
    }

    public ClusterAbundanceDataSlot(ClusterAbundanceDataSlot other) {
        super(other);
    }
}
