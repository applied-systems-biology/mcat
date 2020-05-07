package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.DerivativeMatrixData;
import org.hkijena.mcat.extension.datatypes.HyperstackData;

import java.util.Arrays;
import java.util.List;

/**
 * Organizes preprocessed data
 */
public class MCATPreprocessedDataInterface implements MCATDataInterface {
    private MCATDataSlot preprocessedImage = new MCATDataSlot("preprocessed-image", HyperstackData.class);
    private MCATDataSlot derivativeMatrix = new MCATDataSlot("derivative-matrix", DerivativeMatrixData.class);

    public MCATPreprocessedDataInterface() {

    }

    public MCATPreprocessedDataInterface(MCATPreprocessedDataInterface other) {
        this.preprocessedImage = new MCATDataSlot(other.preprocessedImage);
        this.derivativeMatrix = new MCATDataSlot(other.derivativeMatrix);
    }

    public MCATDataSlot getPreprocessedImage() {
        return preprocessedImage;
    }

    public MCATDataSlot getDerivativeMatrix() {
        return derivativeMatrix;
    }

    @Override
    public List<MCATDataSlot> getSlots() {
        return Arrays.asList(preprocessedImage, derivativeMatrix);
    }
}
