package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataslots.HyperstackDataSlot;
import org.hkijena.mcat.api.dataslots.ROIDataSlot;

import java.util.Arrays;
import java.util.List;

/**
 * Organizes raw data
 */
public class MCATRawDataInterface implements MCATDataInterface {

    private HyperstackDataSlot rawImage = new HyperstackDataSlot("raw-image");
    private ROIDataSlot tissueROI = new ROIDataSlot("tissue-roi");

    public MCATRawDataInterface() {

    }

    public MCATRawDataInterface(MCATRawDataInterface other) {
        this.rawImage = new HyperstackDataSlot(other.rawImage);
        this.tissueROI = new ROIDataSlot(other.tissueROI);
    }

    public HyperstackDataSlot getRawImage() {
        return rawImage;
    }

    public ROIDataSlot getTissueROI() {
        return tissueROI;
    }

    @Override
    public List<MCATDataSlot<?>> getSlots() {
        return Arrays.asList(rawImage, tissueROI);
    }
}
