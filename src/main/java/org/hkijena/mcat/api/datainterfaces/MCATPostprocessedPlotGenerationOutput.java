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
import org.hkijena.mcat.extension.datatypes.AUCPlotData;

import java.util.HashMap;
import java.util.Map;

public class MCATPostprocessedPlotGenerationOutput implements MCATDataInterface {

    private final String groupSubject;
    private final String groupTreatment;
    private MCATDataSlot aucPlotData = new MCATDataSlot("AUC-plot", AUCPlotData.class);

    public MCATPostprocessedPlotGenerationOutput(String groupSubject, String groupTreatment) {
        this.groupSubject = groupSubject;
        this.groupTreatment = groupTreatment;
    }

    @Override
    public Map<String, MCATDataSlot> getSlots() {
        Map<String, MCATDataSlot> result = new HashMap<>();
        result.put(aucPlotData.getName(), aucPlotData);
        return result;
    }

    public MCATDataSlot getAucPlotData() {
        return aucPlotData;
    }

    public String getGroupSubject() {
        return groupSubject;
    }

    public String getGroupTreatment() {
        return groupTreatment;
    }
}
