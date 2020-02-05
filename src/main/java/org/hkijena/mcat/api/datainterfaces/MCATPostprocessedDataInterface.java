package org.hkijena.mcat.api.datainterfaces;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;

import java.util.ArrayList;
import java.util.List;

public class MCATPostprocessedDataInterface implements MCATDataInterface {

    public MCATPostprocessedDataInterface() {

    }

    public MCATPostprocessedDataInterface(MCATPostprocessedDataInterface other) {

    }

    @Override
    public List<MCATDataSlot<?>> getSlots() {
        return new ArrayList<>();
    }
}
