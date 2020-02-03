package org.hkijena.mcat.ui;

import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.api.MCATSample;
import org.hkijena.mcat.api.events.MCATSampleRemovedEvent;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * UI that contains a {@link MCATSampleManagerUI} and allows changing sample settings
 */
public class MCATDataUI extends MCATUIPanel {

    private MCATSampleManagerUI sampleManagerUI;
    private MCATSample currentlyDisplayedSample;
    private JSplitPane splitPane;

    public MCATDataUI(MCATWorkbenchUI workbenchUI) {
        super(workbenchUI);
        sampleManagerUI = new MCATSampleManagerUI(workbenchUI);
        initialize();
        getProject().getEventBus().register(this);
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
        else {
            splitPane.setRightComponent(new JPanel());
        }
    }

    @Subscribe
    public void onCurrentlyDisplayedSampleDeleted(MCATSampleRemovedEvent event) {
        if(event.getSample() == currentlyDisplayedSample) {
            setCurrentlyDisplayedSample(null);
        }
    }
}
