package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.MCATSample;
import org.hkijena.mcat.ui.registries.MCATDataProviderUIRegistry;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

public class MCATDataSlotUI extends JPanel {
    private MCATSample sample;
    private MCATDataSlot<?> slot;
    private MCATDataProviderUI<?> currentProviderUI = null;

    public MCATDataSlotUI(MCATSample sample, MCATDataSlot<?> slot) {
        this.sample = sample;
        this.slot = slot;
        slot.ensureDataProvider();
        setLayout(new BorderLayout());
        initializeDataProviderSelection();
        createDataProviderUI();
    }

    private void initializeDataProviderSelection() {
        JButton button = new JButton(slot.getCurrentProvider().getName(), UIUtils.getIconFromResources("database.png"));
        JPopupMenu menu = UIUtils.addPopupMenuToComponent(button);
        for(MCATDataProvider<?> provider : slot.getAvailableProviders().values()) {
            JMenuItem item = new JMenuItem(provider.getName(), UIUtils.getIconFromResources("database.png"));
            menu.add(item);
        }

        add(button, BorderLayout.EAST);
    }

    private void createDataProviderUI() {
        if(currentProviderUI != null)
            remove(currentProviderUI);

        currentProviderUI = MCATDataProviderUIRegistry.getInstance().getUIFor(sample, slot.getCurrentProvider());
        add(currentProviderUI, BorderLayout.CENTER);
    }


}
