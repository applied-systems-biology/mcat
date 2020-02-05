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

    private void addSlotToForm(String name, MCATDataSlot<?> slot) {
        Component ui = MCATResultDataSlotUIRegistry.getInstance().getUIFor(slot);
        formPanel.addToForm(ui,
                new JLabel(name),
                null);
    }

    private void initialize() {
        setLayout(new BorderLayout());
        formPanel = new FormPanel();

        if(sample != null) {
            addSlotToForm("Cluster images", sample.getClusteredDataInterface().getClusterImages());
            addSlotToForm("Cluster centers", sample.getClusteredDataInterface().getClusterCenters());
        }
        if(subject != null) {
            addSlotToForm("Raw image", subject.getRawDataInterface().getRawImage());
            addSlotToForm("Tissue ROI", subject.getRawDataInterface().getTissueROI());
            addSlotToForm("Preprocessed image", subject.getPreprocessedDataInterface().getPreprocessedImage());
            addSlotToForm("Derivation matrix", subject.getPreprocessedDataInterface().getDerivationMatrix());
        }

        formPanel.addVerticalGlue();

        add(formPanel, BorderLayout.CENTER);
    }
}
