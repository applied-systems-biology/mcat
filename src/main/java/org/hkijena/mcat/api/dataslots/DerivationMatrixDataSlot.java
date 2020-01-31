package org.hkijena.mcat.api.dataslots;

import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.datatypes.DerivationMatrixData;

public class DerivationMatrixDataSlot extends MCATDataSlot<DerivationMatrixData> {
    public DerivationMatrixDataSlot() {
        super(DerivationMatrixData.class);
    }
}
