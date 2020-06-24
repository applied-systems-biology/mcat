package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.AUCData;

import java.util.HashMap;
import java.util.Map;

public class MCATPostprocessingOutput implements MCATDataInterface {

    private MCATDataSlot auc = new MCATDataSlot("auc", AUCData.class);
    private final String groupSubject;
    private final String groupTreatment;

    public MCATPostprocessingOutput(String groupSubject, String groupTreatment) {
        this.groupSubject = groupSubject;
        this.groupTreatment = groupTreatment;
    }

    @Override
    public Map<String, MCATDataSlot> getSlots() {
        Map<String, MCATDataSlot> result = new HashMap<>();
        result.put(auc.getName(), auc);
        return result;
    }

    public MCATDataSlot getAuc() {
        return auc;
    }

    public String getGroupSubject() {
        return groupSubject;
    }

    public String getGroupTreatment() {
        return groupTreatment;
    }
}
