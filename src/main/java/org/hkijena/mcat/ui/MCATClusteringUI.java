package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATClusteringHierarchy;
import org.hkijena.mcat.api.parameters.MCATClusteringParameters;
import org.hkijena.mcat.ui.components.FormPanel;

import javax.swing.*;
import java.awt.*;

public class MCATClusteringUI extends MCATUIPanel {
    public MCATClusteringUI(MCATWorkbenchUI workbenchUI) {
        super(workbenchUI);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        FormPanel formPanel = new FormPanel();

        MCATClusteringParameters parameters = getProject().getClusteringParameters();

        JComboBox<MCATClusteringHierarchy> clusteringHierarchyEditor = formPanel.addToForm(new JComboBox<>(MCATClusteringHierarchy.values()),
                new JLabel("Clustering hierarchy"), null);
        clusteringHierarchyEditor.setSelectedItem(parameters.getClusteringHierarchy());
        clusteringHierarchyEditor.addActionListener(e -> parameters.setClusteringHierarchy((MCATClusteringHierarchy) clusteringHierarchyEditor.getSelectedItem()));

        formPanel.addVerticalGlue();
        add(formPanel, BorderLayout.CENTER);
    }
}
