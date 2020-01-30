package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATSample;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class MCATDataUI extends MCATUIPanel {

    private MCATSampleManagerUI sampleManagerUI;
    private MCATSample currentlyDisplayedSample;
    private JSplitPane splitPane;

    public MCATDataUI(MCATWorkbenchUI workbenchUI) {
        super(workbenchUI);
        sampleManagerUI = new MCATSampleManagerUI(workbenchUI);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sampleManagerUI, new JPanel());
        add(splitPane, BorderLayout.CENTER);

        sampleManagerUI.getSampleTree().addTreeSelectionListener(e -> {
            Object pathComponent = e.getPath().getLastPathComponent();
            if(pathComponent != null) {
                DefaultMutableTreeNode nd = (DefaultMutableTreeNode) pathComponent;
                if(nd.getUserObject() instanceof MCATSample) {
                    if(currentlyDisplayedSample != nd.getUserObject()) {
                        setCurrentlyDisplayedSample((MCATSample)nd.getUserObject());
                    }
                }
            }
        });
    }

    private void setCurrentlyDisplayedSample(MCATSample sample) {
        if(currentlyDisplayedSample == sample)
            return;
        currentlyDisplayedSample = sample;
        if(sample != null) {
            splitPane.setRightComponent(new MCATSampleUI(getWorkbenchUI(), sample));
        }
    }
}
