package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.DerivativeMatrixData;
import org.hkijena.mcat.extension.datatypes.HyperstackData;

import java.util.HashMap;
import java.util.Map;

/**
 * Organizes preprocessed data
 */
public class MCATPreprocessingOutput implements MCATDataInterface {
    private MCATDataSlot preprocessedImage = new MCATDataSlot("preprocessed-image", HyperstackData.class);
    private MCATDataSlot derivativeMatrix = new MCATDataSlot("derivative-matrix", DerivativeMatrixData.class);

    // Must be set by preprocessing
    private int minLength = -1;

    public MCATPreprocessingOutput() {

    }

    public MCATPreprocessingOutput(MCATPreprocessingOutput other) {
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
    public Map<String, MCATDataSlot> getSlots() {
        Map<String, MCATDataSlot> result = new HashMap<>();
        result.put(preprocessedImage.getName(), preprocessedImage);
        result.put(derivativeMatrix.getName(), derivativeMatrix);
        return result;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }
}
