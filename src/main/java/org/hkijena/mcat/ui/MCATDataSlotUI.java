package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.MCATProjectDataSet;
import org.hkijena.mcat.api.registries.MCATDataTypeRegistry;
import org.hkijena.mcat.ui.registries.MCATDataProviderUIRegistry;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * UI for a {@link MCATDataSlot}
 */
public class MCATDataSlotUI extends JPanel {
    private MCATProjectDataSet sample;
    private MCATDataSlot slot;
    private MCATDataProviderUI currentProviderUI = null;
    private JButton selectionButton;

    public MCATDataSlotUI(MCATProjectDataSet sample, MCATDataSlot slot) {
        this.sample = sample;
        this.slot = slot;
        setLayout(new BorderLayout());
        initializeDataProviderSelection();
        createDataProviderUI();
        refreshSelectionButton();
    }

    private void initializeDataProviderSelection() {
        selectionButton = new JButton();
        UIUtils.makeFlat(selectionButton);
        selectionButton.setHorizontalAlignment(SwingConstants.LEFT);
        add(selectionButton, BorderLayout.EAST);
    }

    public void refreshSelectionButton() {
        if (slot.getCurrentProvider() != null) {
            MCATDocumentation documentation = slot.getAcceptedDataType().getAnnotation(MCATDocumentation.class);
            selectionButton.setText(documentation.name());
            selectionButton.setIcon(UIUtils.getIconFromResources("database.png"));
            selectionButton.setToolTipText(documentation.description());
        } else {
            selectionButton.setText("None selected");
            selectionButton.setIcon(UIUtils.getIconFromResources("error.png"));
        }

        JPopupMenu menu = UIUtils.addPopupMenuToComponent(selectionButton);
        for (Class<? extends MCATDataProvider> providerClass : MCATDataTypeRegistry.getInstance().getProvidersFor(slot.getAcceptedDataType())) {
            MCATDocumentation documentation = providerClass.getAnnotation(MCATDocumentation.class);
            JMenuItem item = new JMenuItem(documentation.name(), UIUtils.getIconFromResources("database.png"));
            item.setToolTipText(documentation.description());

            item.addActionListener(e -> {
                try {
                    slot.setCurrentProvider(providerClass.newInstance());
                    refreshSelectionButton();
                    createDataProviderUI();
                } catch (InstantiationException | IllegalAccessException exception) {
                    throw new RuntimeException(exception);
                }
            });

            menu.add(item);
        }
    }

    private void createDataProviderUI() {
        if (currentProviderUI != null) {
            remove(currentProviderUI);
            currentProviderUI = null;
        }

        if (slot.getCurrentProvider() != null) {
            currentProviderUI = MCATDataProviderUIRegistry.getInstance().getUIFor(sample, slot.getCurrentProvider());
            add(currentProviderUI, BorderLayout.CENTER);
        }
    }


    public JButton getSelectionButton() {
        return selectionButton;
    }
}
