package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.datatypes.DerivationMatrixData;
import org.hkijena.mcat.api.datatypes.HyperstackData;

public class MCATPreprocessedDataInterface {
    private MCATDataSlot<HyperstackData> preprocessedImage = new MCATDataSlot<>(HyperstackData.class);
    private MCATDataSlot<DerivationMatrixData> derivationMatrix = new MCATDataSlot<>(DerivationMatrixData.class);

    public MCATDataSlot<HyperstackData> getPreprocessedImage() {
        return preprocessedImage;
    }

    public MCATDataSlot<DerivationMatrixData> getDerivationMatrix() {
        return derivationMatrix;
    }
}
