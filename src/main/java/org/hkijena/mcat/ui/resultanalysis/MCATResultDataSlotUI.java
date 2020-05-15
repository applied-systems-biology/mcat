package org.hkijena.mcat.ui.resultanalysis;

import org.hkijena.mcat.api.MCATResultDataInterfaces;

import javax.swing.*;
import java.nio.file.Path;

public class MCATResultDataSlotUI extends JPanel {
    private Path outputPath;
    private MCATResultDataInterfaces.SlotEntry slot;

    public MCATResultDataSlotUI(Path outputPath, MCATResultDataInterfaces.SlotEntry slot) {
        this.outputPath = outputPath;
        this.slot = slot;
    }

    public MCATResultDataInterfaces.SlotEntry getSlot() {
        return slot;
    }

    public Path getOutputPath() {
        return outputPath;
    }
}
