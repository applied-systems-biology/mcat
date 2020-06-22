package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.TimeDerivativePlotData;

import java.util.HashMap;
import java.util.Map;

public class MCATClusteredPlotGenerationOutput implements MCATDataInterface {

    private MCATDataSlot timeDerivativePlot = new MCATDataSlot("cluster-centers-plot", TimeDerivativePlotData.class);

    @Override
    public Map<String, MCATDataSlot> getSlots() {
        Map<String, MCATDataSlot> result = new HashMap<>();
        result.put(timeDerivativePlot.getName(), timeDerivativePlot);
        return result;
    }

    public MCATDataSlot getTimeDerivativePlot() {
        return timeDerivativePlot;
    }
}
