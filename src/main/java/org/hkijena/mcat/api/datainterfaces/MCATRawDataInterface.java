package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.datatypes.HyperstackData;
import org.hkijena.mcat.api.datatypes.ROIData;

public class MCATRawDataInterface {

    private MCATDataSlot<HyperstackData> rawImage = new MCATDataSlot<>(HyperstackData.class);
    private MCATDataSlot<ROIData> tissueROI = new MCATDataSlot<>(ROIData.class);

    public MCATDataSlot<HyperstackData> getRawImage() {
        return rawImage;
    }

    public MCATDataSlot<ROIData> getTissueROI() {
        return tissueROI;
    }
}
