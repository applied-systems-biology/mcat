package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATProjectSample;
import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATRunSample;
import org.hkijena.mcat.api.MCATRunSampleSubject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class MCATResultUI extends JPanel {
    private MCATRun run;
    private JSplitPane splitPane;

    public MCATResultUI(MCATRun run) {
        this.run = run;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        MCATResultSampleManagerUI sampleManagerUI = new MCATResultSampleManagerUI(this);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sampleManagerUI, new JPanel());
        add(splitPane, BorderLayout.CENTER);

        sampleManagerUI.getSampleTree().addTreeSelectionListener(e -> {
            Object pathComponent = e.getPath().getLastPathComponent();
            if(pathComponent != null) {
                DefaultMutableTreeNode nd = (DefaultMutableTreeNode) pathComponent;
                if(nd.getUserObject() instanceof MCATRunSample) {
                   setCurrentDisplayed((MCATRunSample)nd.getUserObject());
                }
                else if(nd.getUserObject() instanceof MCATRunSampleSubject) {
                    setCurrentDisplayed((MCATRunSampleSubject)nd.getUserObject());
                }
            }
        });
    }

    private void setCurrentDisplayed(MCATRunSample sample) {
        splitPane.setRightComponent(new MCATResultSampleUI(sample, null));
    }

    private void setCurrentDisplayed(MCATRunSampleSubject subject) {
        splitPane.setRightComponent(new MCATResultSampleUI(subject.getSample(), subject));
    }

    public MCATRun getRun() {
        return run;
    }
}
