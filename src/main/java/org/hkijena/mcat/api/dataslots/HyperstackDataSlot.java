package org.hkijena.mcat.api.dataslots;

import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataproviders.HyperstackFromTifDataProvider;
import org.hkijena.mcat.api.datatypes.HyperstackData;

public class HyperstackDataSlot extends MCATDataSlot<HyperstackData> {
    public HyperstackDataSlot() {
        super(HyperstackData.class, new HyperstackFromTifDataProvider());
    }
}
