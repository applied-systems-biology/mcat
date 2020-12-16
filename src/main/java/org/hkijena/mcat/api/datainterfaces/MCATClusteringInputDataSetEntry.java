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
