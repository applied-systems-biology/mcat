package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;

import java.util.Collections;
import java.util.List;

public class MCATPostprocessingDataInterface implements MCATDataInterface {
    @Override
    public List<MCATDataSlot> getSlots() {
        return Collections.emptyList();
    }
}
