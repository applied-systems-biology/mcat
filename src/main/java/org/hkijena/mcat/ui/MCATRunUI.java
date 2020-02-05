package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATAlgorithm;
import org.hkijena.mcat.api.MCATAlgorithmGraph;
import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.ui.components.FileSelection;
import org.hkijena.mcat.ui.components.FormPanel;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MCATRunUI extends MCATUIPanel {

    private MCATRun run;
    private MCATValidityReport validityReport;
    private Worker worker;

    public MCATRunUI(MCATWorkbenchUI workbenchUI) {
        super(workbenchUI);
        run = new MCATRun(workbenchUI.getProject());
        validityReport = run.getValidityReport();
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout(8, 8));
        initializeSetupGUI();
        initializeButtons();
    }

    private void initializeSetupGUI() {
        JPanel setupPanel = new JPanel(new BorderLayout());
        FormPanel formPanel = new FormPanel();

        FileSelection outputFolderSelection = formPanel.addToForm(new FileSelection(FileSelection.Mode.OPEN),
                new JLabel("Output folder"),
                null);
        outputFolderSelection.getFileChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        formPanel.addVerticalGlue();

        setupPanel.add(formPanel, BorderLayout.CENTER);
        add(setupPanel, BorderLayout.CENTER);
    }

    private void initializeButtons() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,8,8,8));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setString("Ready");
        progressBar.setStringPainted(true);
        buttonPanel.add(progressBar);
        buttonPanel.add(Box.createHorizontalStrut(16));

        JButton cancelButton = new JButton("Cancel", UIUtils.getIconFromResources("remove.png"));
        buttonPanel.add(cancelButton);

        JButton runButton = new JButton("Run now", UIUtils.getIconFromResources("run.png"));
        buttonPanel.add(runButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private static class Worker extends SwingWorker<Object, Object> {

        private JProgressBar progressBar;
        private List<MCATAlgorithm> algorithms;

        public Worker(MCATAlgorithmGraph graph, JProgressBar progressBar) {
            this.progressBar = progressBar;
            this.algorithms = graph.traverse();

            progressBar.setMaximum(algorithms.size());
            progressBar.setValue(0);
        }

        @Override
        protected Object doInBackground() throws Exception {
            int count = 0;
            for(MCATAlgorithm algorithm : algorithms) {
                publish(algorithm.getName());
                algorithm.run();
                ++count;
                publish(count);
            }
            return null;
        }

        @Override
        protected void process(List<Object> chunks) {
            super.process(chunks);
            for(Object chunk : chunks) {
                if(chunk instanceof Integer) {
                    progressBar.setValue((Integer)chunk);
                }
                else if(chunk instanceof String) {
                    progressBar.setString("(" + progressBar.getValue() + "/" + progressBar.getMaximum() + ") " + chunk);
                }
            }
        }

        @Override
        protected void done() {
            progressBar.setString("Finished.");
        }
    }
}
