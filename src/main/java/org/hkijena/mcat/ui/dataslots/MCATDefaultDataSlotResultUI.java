package org.hkijena.mcat.ui.dataslots;

import org.hkijena.mcat.api.MCATResultDataInterfaces;
import org.hkijena.mcat.ui.resultanalysis.MCATResultDataSlotUI;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class MCATDefaultDataSlotResultUI extends MCATResultDataSlotUI {
    public MCATDefaultDataSlotResultUI(MCATResultDataInterfaces.SlotEntry slot) {
        super(slot);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        if (getSlot().getStoragePath() != null) {
            JButton openFolderButton = new JButton("Open folder", UIUtils.getIconFromResources("open.png"));
            openFolderButton.setToolTipText(getSlot().getStoragePath().toString());
            add(openFolderButton, BorderLayout.EAST);
            openFolderButton.addActionListener(e -> openFolder());
        }
    }

    private void openFolder() {
        try {
            Desktop.getDesktop().open(Objects.requireNonNull(getSlot().getStoragePath()).toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
