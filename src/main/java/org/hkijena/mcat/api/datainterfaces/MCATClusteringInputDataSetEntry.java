/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/
package org.hkijena.mcat.api.datainterfaces;

import java.util.Map;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;

/**
 * A data interface that contains the input of an {@link org.hkijena.mcat.api.algorithms.MCATClusteringAlgorithm}
 */
public class MCATClusteringInputDataSetEntry implements MCATDataInterface {

    private String dataSetName;
    private MCATPreprocessingInput rawDataInterface;
    private MCATPreprocessingOutput preprocessedDataInterface;

    public MCATClusteringInputDataSetEntry(String dataSetName, MCATPreprocessingInput rawDataInterface, MCATPreprocessingOutput preprocessedDataInterface) {
        this.dataSetName = dataSetName;
        this.rawDataInterface = rawDataInterface;
        this.preprocessedDataInterface = preprocessedDataInterface;
    }

    @Override
    public Map<String, MCATDataSlot> getSlots() {
        return preprocessedDataInterface.getSlots();
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public MCATPreprocessingOutput getPreprocessedDataInterface() {
        return preprocessedDataInterface;
    }

    public MCATPreprocessingInput getRawDataInterface() {
        return rawDataInterface;
    }
}
