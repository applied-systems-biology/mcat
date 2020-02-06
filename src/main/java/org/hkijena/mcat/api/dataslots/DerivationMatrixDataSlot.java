package org.hkijena.mcat.api.dataslots;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataproviders.DerivationMatrixFromFileProvider;
import org.hkijena.mcat.api.datatypes.DerivationMatrixData;

/**
 * Slot for {@link DerivationMatrixData}
 */
@JsonSerialize(using = MCATDataSlot.Serializer.class)
public class DerivationMatrixDataSlot extends MCATDataSlot<DerivationMatrixData> {
    public DerivationMatrixDataSlot(String name) {
        super(name, DerivationMatrixData.class, new DerivationMatrixFromFileProvider());
    }

    public DerivationMatrixDataSlot(DerivationMatrixDataSlot other) {
        super(other);
    }
}
