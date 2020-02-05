package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATRun;

import javax.swing.*;
import java.awt.*;

public class MCATResultUI extends JPanel {
    private MCATRun run;

    public MCATResultUI(MCATRun run) {
        this.run = run;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        MCATResultSampleManagerUI sampleManagerUI = new MCATResultSampleManagerUI(this);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sampleManagerUI, new JPanel());
        add(splitPane, BorderLayout.CENTER);
    }

    public MCATRun getRun() {
        return run;
    }
}
