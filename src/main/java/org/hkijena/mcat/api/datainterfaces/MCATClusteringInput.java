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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;

/**
 * A data interface that contains the input of an {@link org.hkijena.mcat.api.algorithms.MCATClusteringAlgorithm}
 */
public class MCATClusteringInput implements MCATDataInterface {

    private final String groupSubject;
    private final String groupTreatment;
    private Map<String, MCATClusteringInputDataSetEntry> dataSetEntries = new HashMap<>();
    private List<MCATPreprocessingOutput> allPreprocessingOutputs = new ArrayList<>();

    public MCATClusteringInput(String groupSubject, String groupTreatment) {
        this.groupSubject = groupSubject;
        this.groupTreatment = groupTreatment;
    }

    @Override
    public Map<String, MCATDataSlot> getSlots() {
        Map<String, MCATDataSlot> result = new HashMap<>();
        for (Map.Entry<String, MCATClusteringInputDataSetEntry> entry : dataSetEntries.entrySet()) {
            Map<String, MCATDataSlot> slots = entry.getValue().getSlots();
            for (Map.Entry<String, MCATDataSlot> slotEntry : slots.entrySet()) {
                result.put("entries/" + entry.getKey() + "/" + slotEntry.getKey(), slotEntry.getValue());
            }
        }
        return result;
    }


    /**
     * List of ALL preprocessing results that were involved in ALL clustering operations
     * @return list
     */
    public List<MCATPreprocessingOutput> getAllPreprocessingOutputs() {
        return allPreprocessingOutputs;
    }

    public Map<String, MCATClusteringInputDataSetEntry> getDataSetEntries() {
        return dataSetEntries;
    }

    /**
     * Discriminator used for grouping the data set entries together. Can be null.
     * This discriminator is derived from the data set name
     *
     * @return Discriminator used for grouping the data set entries together. Can be null.
     */
    public String getGroupSubject() {
        return groupSubject;
    }

    /**
     * Discriminator used for grouping the data set entries together. Can be null.
     * This discriminator is derived from the data set's treatment parameter
     *
     * @return Discriminator used for grouping the data set entries together. Can be null.
     */
    public String getGroupTreatment() {
        return groupTreatment;
    }
}
