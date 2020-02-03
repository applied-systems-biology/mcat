package org.hkijena.mcat.api.dataslots;

import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataproviders.DerivationMatrixFromFileProvider;
import org.hkijena.mcat.api.datatypes.DerivationMatrixData;

/**
 * Slot for {@link DerivationMatrixData}
 */
public class DerivationMatrixDataSlot extends MCATDataSlot<DerivationMatrixData> {
    public DerivationMatrixDataSlot() {
        super(DerivationMatrixData.class, new DerivationMatrixFromFileProvider());
    }
}
