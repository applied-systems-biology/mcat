package org.hkijena.mcat.ui.dataslots;

import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.dataproviders.FileDataProvider;
import org.hkijena.mcat.ui.MCATResultDataSlotUI;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class MCATDefaultDataSlotResultUI extends MCATResultDataSlotUI<MCATDataSlot<?>> {
    public MCATDefaultDataSlotResultUI(MCATDataSlot<?> slot) {
        super(slot);
        initialize();
    }

    private Path getStoragePath() {
        if(getSlot().getCurrentProvider() != null && getSlot().getCurrentProvider().isValid()) {
            if(getSlot().getCurrentProvider() instanceof FileDataProvider<?>) {
                FileDataProvider<?> provider = (FileDataProvider<?>)getSlot().getCurrentProvider();
                return provider.getFilePath().getParent();
            }
        }
        else if(getSlot().getStorageFilePath() != null) {
            return getSlot().getStorageFilePath();
        }

        return null;
    }

    private void initialize() {
        setLayout(new BorderLayout());
        if(getStoragePath() != null) {
            JButton openFolderButton = new JButton("Open folder", UIUtils.getIconFromResources("open.png"));
            openFolderButton.setToolTipText(getStoragePath().toString());
            add(openFolderButton, BorderLayout.EAST);
            openFolderButton.addActionListener(e -> openFolder());
        }
    }

    private void openFolder() {
        try {
            Desktop.getDesktop().open(Objects.requireNonNull(getStoragePath()).toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
