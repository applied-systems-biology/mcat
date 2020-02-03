package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.dataslots.DerivationMatrixDataSlot;
import org.hkijena.mcat.api.dataslots.HyperstackDataSlot;

/**
 * Organizes preprocessed data
 */
public class MCATPreprocessedDataInterface {
    private HyperstackDataSlot preprocessedImage = new HyperstackDataSlot();
    private DerivationMatrixDataSlot derivationMatrix = new DerivationMatrixDataSlot();

    public HyperstackDataSlot getPreprocessedImage() {
        return preprocessedImage;
    }

    public DerivationMatrixDataSlot getDerivationMatrix() {
        return derivationMatrix;
    }
}
