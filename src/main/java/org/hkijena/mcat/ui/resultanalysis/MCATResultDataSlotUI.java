package org.hkijena.mcat.ui.resultanalysis;

import org.hkijena.mcat.api.MCATDataSlot;

import javax.swing.*;

public class MCATResultDataSlotUI extends JPanel {
    private MCATDataSlot slot;

    public MCATResultDataSlotUI(MCATDataSlot slot) {
        this.slot = slot;
    }

    public MCATDataSlot getSlot() {
        return slot;
    }
}
