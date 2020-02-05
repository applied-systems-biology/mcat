package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataslots.DerivationMatrixDataSlot;
import org.hkijena.mcat.api.dataslots.HyperstackDataSlot;

import java.util.Arrays;
import java.util.List;

/**
 * Organizes preprocessed data
 */
public class MCATPreprocessedDataInterface implements MCATDataInterface {
    private HyperstackDataSlot preprocessedImage = new HyperstackDataSlot("preprocessed-image");
    private DerivationMatrixDataSlot derivationMatrix = new DerivationMatrixDataSlot("derivation-matrix");

    public MCATPreprocessedDataInterface() {

    }

    public MCATPreprocessedDataInterface(MCATPreprocessedDataInterface other) {
        this.preprocessedImage = new HyperstackDataSlot(other.preprocessedImage);
        this.derivationMatrix = new DerivationMatrixDataSlot(other.derivationMatrix);
    }

    public HyperstackDataSlot getPreprocessedImage() {
        return preprocessedImage;
    }

    public DerivationMatrixDataSlot getDerivationMatrix() {
        return derivationMatrix;
    }

    @Override
    public List<MCATDataSlot<?>> getSlots() {
        return Arrays.asList(preprocessedImage, derivationMatrix);
    }
}
