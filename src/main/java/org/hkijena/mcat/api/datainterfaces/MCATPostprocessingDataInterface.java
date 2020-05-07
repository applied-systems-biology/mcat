package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;

import java.util.Collections;
import java.util.Map;

public class MCATPostprocessingDataInterface implements MCATDataInterface {
    @Override
    public Map<String, MCATDataSlot> getSlots() {
        return Collections.emptyMap();
    }
}
