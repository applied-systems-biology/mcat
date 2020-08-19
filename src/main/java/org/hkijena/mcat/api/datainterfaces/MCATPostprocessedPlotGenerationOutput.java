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

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.AUCPlotData;

import java.util.HashMap;
import java.util.Map;

public class MCATPostprocessedPlotGenerationOutput implements MCATDataInterface {

    private MCATDataSlot aucPlotData = new MCATDataSlot("AUC-plot", AUCPlotData.class);
    private final String groupSubject;
    private final String groupTreatment;

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
