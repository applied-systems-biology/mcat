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

import org.hkijena.mcat.api.MCATResult;
import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.ui.components.FileSelection;
import org.hkijena.mcat.ui.components.FormPanel;
import org.hkijena.mcat.ui.components.MarkdownDocument;
import org.hkijena.mcat.ui.resultanalysis.MCATResultUI;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.BorderLayout;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class MCATRunUI extends MCATWorkbenchUIPanel {

    JPanel setupPanel;
    JButton cancelButton;
    JButton runButton;
    JProgressBar progressBar;
    JPanel buttonPanel;
    private MCATRun run;
    private MCATValidityReport validityReport = new MCATValidityReport();
    private Worker worker;

    public MCATRunUI(MCATWorkbenchUI workbenchUI) {
        super(workbenchUI);
        run = new MCATRun(workbenchUI.getProject());
        run.reportValidity(validityReport);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout(8, 8));
        initializeButtons();
        initializeSetupGUI();
    }

    private void initializeSetupGUI() {
        setupPanel = new JPanel(new BorderLayout());
        FormPanel formPanel = new FormPanel(MarkdownDocument.fromPluginResource("documentation/run.md"),
                FormPanel.WITH_SCROLLING | FormPanel.WITH_DOCUMENTATION);

        FileSelection outputFolderSelection = formPanel.addToForm(new FileSelection(FileSelection.IOMode.Save,
                        FileSelection.PathMode.DirectoriesOnly),
                new JLabel("Output folder"),
                null);
        outputFolderSelection.getFileChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        outputFolderSelection.addActionListener(e -> {
            run.setOutputPath(outputFolderSelection.getPath());
            runButton.setEnabled(outputFolderSelection.getPath() != null);
        });
        formPanel.addVerticalGlue();

        setupPanel.add(formPanel, BorderLayout.CENTER);
        add(setupPanel, BorderLayout.CENTER);
    }

    private void initializeButtons() {
        buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        progressBar = new JProgressBar();
        progressBar.setString("Ready");
        progressBar.setStringPainted(true);
        buttonPanel.add(progressBar);
        buttonPanel.add(Box.createHorizontalStrut(16));

        cancelButton = new JButton("Cancel", UIUtils.getIconFromResources("remove.png"));
        buttonPanel.add(cancelButton);

        runButton = new JButton("Run now", UIUtils.getIconFromResources("run.png"));
        runButton.addActionListener(e -> runNow());
        runButton.setEnabled(false);
        buttonPanel.add(runButton);

        add(buttonPanel, BorderLayout.SOUTH);

        cancelButton.setVisible(false);
    }

    private void runNow() {
        runButton.setVisible(false);
        cancelButton.setVisible(true);
        setupPanel.setVisible(false);
        remove(setupPanel);

        Worker worker = new Worker(run, progressBar);
        cancelButton.addActionListener(e -> {
            cancelButton.setEnabled(false);
            worker.cancel(true);
        });
        worker.addPropertyChangeListener(p -> {
            if ("state".equals(p.getPropertyName())) {
                switch ((SwingWorker.StateValue) p.getNewValue()) {
                    case DONE:
                        cancelButton.setEnabled(false);
                        if (!worker.isCancelled()) {
                            cancelButton.setVisible(false);
                        }
                        try {
                            if (worker.isCancelled()) {
                                SwingUtilities.invokeLater(() -> openError(new RuntimeException("Execution was cancelled by user!")));
                            } else if (worker.get() != null) {
                                final Exception e = worker.get();
                                SwingUtilities.invokeLater(() -> openError(e));
                            } else {
                                openResults();
                            }
                        } catch (InterruptedException | ExecutionException | CancellationException e) {
                            SwingUtilities.invokeLater(() -> openError(e));
                        }
                        break;
                }
            }
        });
        worker.execute();
    }

    private void openError(Exception exception) {
        StringWriter writer = new StringWriter();
        exception.printStackTrace(new PrintWriter(writer));
        JTextArea errorPanel = new JTextArea(writer.toString());
        errorPanel.setEditable(false);
        add(new JScrollPane(errorPanel), BorderLayout.CENTER);
        revalidate();
    }

    private void openResults() {
        MCATResultUI resultUI = new MCATResultUI(getWorkbenchUI(), new MCATResult(run.getOutputPath()));
        add(resultUI, BorderLayout.CENTER);
        buttonPanel.setVisible(false);
        revalidate();
    }

    private static class Worker extends SwingWorker<Exception, Object> {

        private JProgressBar progressBar;
        private MCATRun run;

        public Worker(MCATRun run, JProgressBar progressBar) {
            this.progressBar = progressBar;
            this.run = run;

            progressBar.setMaximum(run.getGraph().size());
            progressBar.setValue(0);
        }

        private void onStatus(MCATRun.Status status) {
            publish(status.getCurrentTask());
            publish(status.getProgress());
        }

        @Override
        protected Exception doInBackground() throws Exception {
            try {
                run.run(this::onStatus, this::isCancelled);
            } catch (Exception e) {
                e.printStackTrace();
                return e;
            }

            return null;
        }

        @Override
        protected void process(List<Object> chunks) {
            super.process(chunks);
            for (Object chunk : chunks) {
                if (chunk instanceof Integer) {
                    progressBar.setValue((Integer) chunk);
                } else if (chunk instanceof String) {
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
