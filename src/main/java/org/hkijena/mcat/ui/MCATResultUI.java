package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATRunSample;
import org.hkijena.mcat.api.MCATRunSampleSubject;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.IOException;

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

        initializeToolbar();
    }

    private void initializeToolbar() {
        JToolBar toolBar = new JToolBar();
        JButton openFolderButton = new JButton("Open output folder", UIUtils.getIconFromResources("open.png"));
        openFolderButton.addActionListener(e -> openOutputFolder());
        toolBar.add(openFolderButton);
        add(toolBar, BorderLayout.NORTH);
    }

    private void openOutputFolder() {
        try {
            Desktop.getDesktop().open(run.getOutputPath().toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
