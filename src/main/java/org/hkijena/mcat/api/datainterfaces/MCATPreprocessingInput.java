package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.HyperstackData;
import org.hkijena.mcat.extension.datatypes.ROIData;

import java.util.HashMap;
import java.util.Map;

/**
 * Organizes raw data
 */
public class MCATPreprocessingInput implements MCATDataInterface {

    private MCATDataSlot rawImage = new MCATDataSlot("raw-image", HyperstackData.class);
    private MCATDataSlot tissueROI = new MCATDataSlot("tissue-roi", ROIData.class);

    public MCATPreprocessingInput() {

    }

    public MCATPreprocessingInput(MCATPreprocessingInput other) {
        this.rawImage = new MCATDataSlot(other.rawImage);
        this.tissueROI = new MCATDataSlot(other.tissueROI);
    }

    public MCATDataSlot getRawImage() {
        return rawImage;
    }

    public MCATDataSlot getTissueROI() {
        return tissueROI;
    }

    @Override
    public Map<String, MCATDataSlot> getSlots() {
        Map<String, MCATDataSlot> result = new HashMap<>();
        result.put(rawImage.getName(), rawImage);
        result.put(tissueROI.getName(), tissueROI);
        return result;
    }
}
