package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.dataslots.HyperstackDataSlot;
import org.hkijena.mcat.api.dataslots.ROIDataSlot;

/**
 * Organizes raw data
 */
public class MCATRawDataInterface {

    private HyperstackDataSlot rawImage = new HyperstackDataSlot();
    private ROIDataSlot tissueROI = new ROIDataSlot();

    public HyperstackDataSlot getRawImage() {
        return rawImage;
    }

    public ROIDataSlot getTissueROI() {
        return tissueROI;
    }
}
