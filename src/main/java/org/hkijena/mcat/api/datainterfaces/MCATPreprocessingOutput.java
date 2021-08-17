/*******************************************************************************
 * Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge
 *
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 ******************************************************************************/
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
    private int nSlices = -1;

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

    public int getNSlices() {
        return nSlices;
    }

    public void setNSlices(int nSlices) {
        this.nSlices = nSlices;
    }
}
