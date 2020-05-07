package org.hkijena.mcat.api;

import java.util.Map;

public interface MCATDataInterface {
    /**
     * Gets each slot including an unique ID
     *
     * @return Map from unique ID to slot
     */
    Map<String, MCATDataSlot> getSlots();
}
