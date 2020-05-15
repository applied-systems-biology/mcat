package org.hkijena.mcat.extension.resultanalysis;

import org.hkijena.mcat.ui.registries.MCATResultDataSlotUIRegistry;

/**
 * Registers custom UI for result data types.
 * Please do not forget to register each data type in {@link org.hkijena.mcat.extension.datatypes.StandardDataTypeExtension}
 */
public class StandardResultDataSlotUIExtension {

    private StandardResultDataSlotUIExtension() {
    }

    public static void register() {
        MCATResultDataSlotUIRegistry.getInstance().register("derivative-matrix", DerivativeMatrixDataSlotResultUI.class);
        MCATResultDataSlotUIRegistry.getInstance().register("imageplus-hyperstack", HyperStackDataSlotResultUI.class);
        MCATResultDataSlotUIRegistry.getInstance().register("auc-plot", AUCPlotDataSlotResultUI.class);
        MCATResultDataSlotUIRegistry.getInstance().register("time-derivative-plot", TimeDerivativePlotDataSlotResultUI.class);
    }
}
