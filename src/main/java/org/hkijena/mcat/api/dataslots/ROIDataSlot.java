package org.hkijena.mcat.api.dataslots;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataproviders.ROIFromFileDataProvider;
import org.hkijena.mcat.api.datatypes.ROIData;

/**
 * Slot for {@link ROIData}
 */
@JsonSerialize(using = MCATDataSlot.Serializer.class)
public class ROIDataSlot extends MCATDataSlot<ROIData> {

    public ROIDataSlot(String name) {
        super(name, ROIData.class, new ROIFromFileDataProvider());
    }

    public ROIDataSlot(ROIDataSlot other) {
        super(other);
    }
}
