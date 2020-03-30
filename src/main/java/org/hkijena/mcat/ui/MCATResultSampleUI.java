package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.MCATRunSample;
import org.hkijena.mcat.api.MCATRunSampleSubject;
import org.hkijena.mcat.ui.components.FormPanel;
import org.hkijena.mcat.ui.registries.MCATResultDataSlotUIRegistry;

import javax.swing.*;
import java.awt.*;

public class MCATResultSampleUI extends JPanel {
    private MCATRunSample sample;
    private MCATRunSampleSubject subject;
    private FormPanel formPanel;

    public MCATResultSampleUI(MCATRunSample sample, MCATRunSampleSubject subject) {
        this.sample = sample;
        this.subject = subject;

        initialize();
    }

    private void addSlotToForm(String name, MCATDataSlot<?> slot, String documentationPath) {
        Component ui = MCATResultDataSlotUIRegistry.getInstance().getUIFor(slot);
        formPanel.addToForm(ui,
                new JLabel(name),
                documentationPath);
    }

    private void initialize() {
        setLayout(new BorderLayout());
        formPanel = new FormPanel();

        if(sample != null) {
            addSlotToForm("Cluster image",
                    sample.getClusteredDataInterface().getClusterImages(),
                    "documentation/parameter_sample_cluster_image.md");
            addSlotToForm("Cluster centers",
                    sample.getClusteredDataInterface().getClusterCenters(),
                    "documentation/parameter_sample_cluster_centers.md");
        }
        if(subject != null) {
            addSlotToForm("Raw image",
                    subject.getRawDataInterface().getRawImage(),
                    "documentation/parameter_sample_raw_image.md");
            addSlotToForm("Tissue ROI",
                    subject.getRawDataInterface().getTissueROI(),
                    "documentation/parameter_sample_roi.md");
            addSlotToForm("Preprocessed image",
                    subject.getPreprocessedDataInterface().getPreprocessedImage(),
                    "documentation/parameter_sample_preprocessed_image.md");
            addSlotToForm("Derivative matrix",
                    subject.getPreprocessedDataInterface().getDerivativeMatrix(),
                    "documentation/parameter_sample_derivative_matrix.md");
        }

        formPanel.addVerticalGlue();

        add(formPanel, BorderLayout.CENTER);
    }
}
