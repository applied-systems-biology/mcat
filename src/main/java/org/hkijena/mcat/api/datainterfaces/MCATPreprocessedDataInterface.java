package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataslots.DerivativeMatrixDataSlot;
import org.hkijena.mcat.api.dataslots.HyperstackDataSlot;

import java.util.Arrays;
import java.util.List;

/**
 * Organizes preprocessed data
 */
public class MCATPreprocessedDataInterface implements MCATDataInterface {
    private HyperstackDataSlot preprocessedImage = new HyperstackDataSlot("preprocessed-image");
    private DerivativeMatrixDataSlot derivativeMatrix = new DerivativeMatrixDataSlot("derivative-matrix");

    public MCATPreprocessedDataInterface() {

    }

    public MCATPreprocessedDataInterface(MCATPreprocessedDataInterface other) {
        this.preprocessedImage = new HyperstackDataSlot(other.preprocessedImage);
        this.derivativeMatrix = new DerivativeMatrixDataSlot(other.derivativeMatrix);
    }

    public HyperstackDataSlot getPreprocessedImage() {
        return preprocessedImage;
    }

    public DerivativeMatrixDataSlot getDerivativeMatrix() {
        return derivativeMatrix;
    }

    @Override
    public List<MCATDataSlot<?>> getSlots() {
        return Arrays.asList(preprocessedImage, derivativeMatrix);
    }
}
