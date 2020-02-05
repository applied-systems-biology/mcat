package org.hkijena.mcat.ui.dataslots;

import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.ui.MCATResultDataSlotUI;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MCATDefaultDataSlotResultUI extends MCATResultDataSlotUI<MCATDataSlot<?>> {
    public MCATDefaultDataSlotResultUI(MCATDataSlot<?> slot) {
        super(slot);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        if(getSlot().getStorageFilePath() != null) {
            JButton openFolderButton = new JButton("Open folder", UIUtils.getIconFromResources("open.png"));
            add(openFolderButton, BorderLayout.EAST);
            openFolderButton.addActionListener(e -> openFolder());
        }
    }

    private void openFolder() {
        try {
            Desktop.getDesktop().open(getSlot().getStorageFilePath().toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
