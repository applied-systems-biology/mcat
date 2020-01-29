package org.hkijena.mcat.api;

import org.hkijena.mcat.api.datatypes.HyperstackData;
import org.hkijena.mcat.api.datatypes.ROIData;

public class MCATSample {

    private MCATProject project;

    /**
     * The raw input image
     */
    private MCATDataSlot<HyperstackData> rawInputImage = new MCATDataSlot<>(HyperstackData.class);

    /**
     * The ROI of the tissue
     */
    private MCATDataSlot<ROIData> tissueROI = new MCATDataSlot<>(ROIData.class);


    public MCATSample(MCATProject project) {
        this.project = project;
    }

    public MCATProject getProject() {
        return project;
    }

    public MCATDataSlot<HyperstackData> getRawInputImage() {
        return rawInputImage;
    }

    public MCATDataSlot<ROIData> getTissueROI() {
        return tissueROI;
    }
}
