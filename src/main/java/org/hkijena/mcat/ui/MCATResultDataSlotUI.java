package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATDataSlot;

import javax.swing.*;

public class MCATResultDataSlotUI<T extends MCATDataSlot> extends JPanel {
    private T slot;

    public MCATResultDataSlotUI(T slot) {
        this.slot = slot;
    }

    public T getSlot() {
        return slot;
    }
}
