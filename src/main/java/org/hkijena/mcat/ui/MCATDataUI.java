/*******************************************************************************
 * Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge
 *
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 ******************************************************************************/
package org.hkijena.mcat.ui;

import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.api.MCATProjectDataSet;
import org.hkijena.mcat.api.events.DataSetRemovedEvent;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.BorderLayout;

/**
 * UI that contains a {@link MCATDataSetManagerUI} and allows changing sample settings
 */
public class MCATDataUI extends MCATWorkbenchUIPanel {

    private MCATDataSetManagerUI sampleManagerUI;
    private MCATProjectDataSet currentlyDisplayedSample;
    private JSplitPane splitPane;

    public MCATDataUI(MCATWorkbenchUI workbenchUI) {
        super(workbenchUI);
        sampleManagerUI = new MCATDataSetManagerUI(workbenchUI);
        initialize();
        getProject().getEventBus().register(this);
    }

    private void initialize() {
        setLayout(new BorderLayout());
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sampleManagerUI, new JPanel());
        add(splitPane, BorderLayout.CENTER);

        sampleManagerUI.getSampleTree().addTreeSelectionListener(e -> {
            Object pathComponent = e.getPath().getLastPathComponent();
            if (pathComponent != null) {
                DefaultMutableTreeNode nd = (DefaultMutableTreeNode) pathComponent;
                if (nd.getUserObject() instanceof MCATProjectDataSet) {
                    if (currentlyDisplayedSample != nd.getUserObject()) {
                        setCurrentlyDisplayedSample((MCATProjectDataSet) nd.getUserObject());
                    }
                }
            }
        });
    }

    private void setCurrentlyDisplayedSample(MCATProjectDataSet sample) {
        if (currentlyDisplayedSample == sample)
            return;
        currentlyDisplayedSample = sample;
        if (sample != null) {
            splitPane.setRightComponent(new MCATDataSetUI(getWorkbenchUI(), sample));
        } else {
            splitPane.setRightComponent(new JPanel());
        }
    }

    @Subscribe
    public void onCurrentlyDisplayedSampleDeleted(DataSetRemovedEvent event) {
        if (event.getSample() == currentlyDisplayedSample) {
            setCurrentlyDisplayedSample(null);
        }
    }
}
