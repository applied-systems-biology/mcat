package org.hkijena.mcat.api.dataslots;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataproviders.DerivationMatrixFromFileProvider;
import org.hkijena.mcat.api.datatypes.DerivativeMatrixData;

/**
 * Slot for {@link DerivativeMatrixData}
 */
@JsonSerialize(using = MCATDataSlot.Serializer.class)
public class DerivativeMatrixDataSlot extends MCATDataSlot<DerivativeMatrixData> {
    public DerivativeMatrixDataSlot(String name) {
        super(name, DerivativeMatrixData.class, new DerivationMatrixFromFileProvider());
    }

    public DerivativeMatrixDataSlot(DerivativeMatrixDataSlot other) {
        super(other);
    }
}
