package org.hkijena.mcat.api.dataslots;

import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataproviders.ROIFromFileDataProvider;
import org.hkijena.mcat.api.datatypes.ROIData;

/**
 * Slot for {@link ROIData}
 */
public class ROIDataSlot extends MCATDataSlot<ROIData> {
    public ROIDataSlot() {
        super(ROIData.class, new ROIFromFileDataProvider());
    }
}
