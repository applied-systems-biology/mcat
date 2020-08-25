/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/
package org.hkijena.mcat.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.MCATProjectDataSet;
import org.hkijena.mcat.api.registries.MCATDataTypeRegistry;
import org.hkijena.mcat.ui.registries.MCATDataProviderUIRegistry;
import org.hkijena.mcat.utils.UIUtils;

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
