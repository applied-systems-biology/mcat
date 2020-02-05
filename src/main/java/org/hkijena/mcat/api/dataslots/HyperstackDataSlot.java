package org.hkijena.mcat.api.dataslots;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataproviders.HyperstackFromTifDataProvider;
import org.hkijena.mcat.api.datatypes.HyperstackData;

/**
 * Slot for {@link HyperstackData}
 */
@JsonSerialize(using = MCATDataSlot.Serializer.class)
public class HyperstackDataSlot extends MCATDataSlot<HyperstackData> {
    public HyperstackDataSlot() {
        super(HyperstackData.class, new HyperstackFromTifDataProvider());
    }
    public HyperstackDataSlot(HyperstackDataSlot other) {
        super(HyperstackData.class, new HyperstackFromTifDataProvider(other.getProvider(HyperstackFromTifDataProvider.class)));
    }
}
