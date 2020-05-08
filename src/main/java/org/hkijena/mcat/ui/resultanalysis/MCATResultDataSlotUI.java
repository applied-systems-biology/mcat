package org.hkijena.mcat.ui.resultanalysis;

import org.hkijena.mcat.api.MCATResultDataInterfaces;

import javax.swing.*;

public class MCATResultDataSlotUI extends JPanel {
    private MCATResultDataInterfaces.SlotEntry slot;

    public MCATResultDataSlotUI(MCATResultDataInterfaces.SlotEntry slot) {
        this.slot = slot;
    }

    public MCATResultDataInterfaces.SlotEntry getSlot() {
        return slot;
    }
}
