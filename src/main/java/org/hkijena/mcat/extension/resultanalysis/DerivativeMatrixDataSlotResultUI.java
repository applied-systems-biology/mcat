package org.hkijena.mcat.extension.resultanalysis;

import org.hkijena.mcat.api.MCATResultDataInterfaces;
import org.hkijena.mcat.utils.UIUtils;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

public class DerivativeMatrixDataSlotResultUI extends MCATDefaultDataSlotResultUI {
    public DerivativeMatrixDataSlotResultUI(Path outputPath, MCATResultDataInterfaces.SlotEntry slot) {
        super(outputPath, slot);
    }

    @Override
    protected void registerActions() {
        super.registerActions();
        Path csvFile = findFirstFileWithExtension(".csv");
        if(csvFile != null) {
            registerAction("Open *.csv", "Opens the CSV table", UIUtils.getIconFromResources("filetype-csv.png"), ui -> {
                try {
                    Desktop.getDesktop().open(csvFile.toFile());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
