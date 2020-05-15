package org.hkijena.mcat.ui.registries;

import org.hkijena.mcat.api.MCATResultDataInterfaces;
import org.hkijena.mcat.extension.dataproviders.ui.DerivationMatrixFromFileDataProviderUI;
import org.hkijena.mcat.extension.resultanalysis.DerivativeMatrixDataSlotResultUI;
import org.hkijena.mcat.extension.resultanalysis.MCATDefaultDataSlotResultUI;
import org.hkijena.mcat.extension.resultanalysis.StandardResultDataSlotUIExtension;
import org.hkijena.mcat.ui.resultanalysis.MCATResultDataSlotUI;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MCATResultDataSlotUIRegistry {
    private static MCATResultDataSlotUIRegistry instance;
    private Map<String, Class<? extends MCATResultDataSlotUI>> registry = new HashMap<>();

    private MCATResultDataSlotUIRegistry() {
    }

    public void register(String dataTypeId, Class<? extends MCATResultDataSlotUI> uiClass) {
        registry.put(dataTypeId, uiClass);
    }

    public MCATResultDataSlotUI getUIFor(Path outputPath, MCATResultDataInterfaces.SlotEntry slot) {
        Class<? extends MCATResultDataSlotUI> uiClass = registry.getOrDefault(slot.getDataTypeId(), null);
        if (uiClass != null) {
            try {
                return uiClass.getConstructor(Path.class, MCATResultDataInterfaces.SlotEntry.class).newInstance(outputPath, slot);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new MCATDefaultDataSlotResultUI(outputPath, slot);
        }
    }

    public static MCATResultDataSlotUIRegistry getInstance() {
        if (instance == null) {
            instance = new MCATResultDataSlotUIRegistry();
            StandardResultDataSlotUIExtension.register();
        }
        return instance;
    }
}
