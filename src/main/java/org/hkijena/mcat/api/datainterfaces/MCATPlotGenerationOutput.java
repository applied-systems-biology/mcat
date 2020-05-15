package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.AUCPlotData;

import java.util.HashMap;
import java.util.Map;

public class MCATPlotGenerationOutput implements MCATDataInterface {

    private MCATDataSlot aucPlotData = new MCATDataSlot("AUC-plot", AUCPlotData.class);

    @Override
    public Map<String, MCATDataSlot> getSlots() {
        Map<String, MCATDataSlot> result = new HashMap<>();
        result.put(aucPlotData.getName(), aucPlotData);
        return result;
    }

    public MCATDataSlot getAucPlotData() {
        return aucPlotData;
    }
}
