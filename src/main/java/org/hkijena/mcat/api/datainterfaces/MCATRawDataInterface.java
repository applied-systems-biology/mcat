package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.HyperstackData;
import org.hkijena.mcat.extension.datatypes.ROIData;

import java.util.Arrays;
import java.util.List;

/**
 * Organizes raw data
 */
public class MCATRawDataInterface implements MCATDataInterface {

    private MCATDataSlot rawImage = new MCATDataSlot("raw-image", HyperstackData.class);
    private MCATDataSlot tissueROI = new MCATDataSlot("tissue-roi", ROIData.class);

    public MCATRawDataInterface() {

    }

    public MCATRawDataInterface(MCATRawDataInterface other) {
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
    public List<MCATDataSlot> getSlots() {
        return Arrays.asList(rawImage, tissueROI);
    }
}
