package org.hkijena.mcat.ui.registries;

import org.hkijena.mcat.api.MCATResultDataInterfaces;
import org.hkijena.mcat.ui.dataslots.MCATDefaultDataSlotResultUI;
import org.hkijena.mcat.ui.resultanalysis.MCATResultDataSlotUI;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class MCATResultDataSlotUIRegistry {
    private static MCATResultDataSlotUIRegistry instance;
    private Map<String, Class<? extends MCATResultDataSlotUI>> registry = new HashMap<>();

    private MCATResultDataSlotUIRegistry() {
        // Register here
    }

    public MCATResultDataSlotUI getUIFor(MCATResultDataInterfaces.SlotEntry slot) {
        Class<? extends MCATResultDataSlotUI> uiClass = registry.getOrDefault(slot.getName(), null);
        if (uiClass != null) {
            try {
                return uiClass.getConstructor(MCATResultDataInterfaces.SlotEntry.class).newInstance(slot);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new MCATDefaultDataSlotResultUI(slot);
        }
    }

    public static MCATResultDataSlotUIRegistry getInstance() {
        if (instance == null)
            instance = new MCATResultDataSlotUIRegistry();
        return instance;
    }
}
