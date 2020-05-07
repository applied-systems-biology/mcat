package org.hkijena.mcat.ui.resultanalysis;

import javax.swing.*;

public class MCATResultSampleUI extends JPanel {
//    private MCATRunSample sample;
//    private MCATRunSampleSubject subject;
//    private FormPanel formPanel;
//
//    public MCATResultSampleUI(MCATRunSample sample, MCATRunSampleSubject subject) {
//        this.sample = sample;
//        this.subject = subject;
//
//        initialize();
//    }
//
//    private void addSlotToForm(String name, MCATDataSlot slot, MarkdownDocument documentation) {
//        Component ui = MCATResultDataSlotUIRegistry.getInstance().getUIFor(slot);
//        formPanel.addToForm(ui,
//                new JLabel(name),
//                documentation);
//    }
//
//    private void initialize() {
//        setLayout(new BorderLayout());
//        formPanel = new FormPanel(null, FormPanel.WITH_DOCUMENTATION | FormPanel.WITH_SCROLLING);
//
//        if(sample != null) {
//            addSlotToForm("Cluster image",
//                    sample.getClusteredDataInterface().getClusterImages(),
//                    MarkdownDocument.fromPluginResource("documentation/parameter_sample_cluster_image.md"));
//            addSlotToForm("Cluster centers",
//                    sample.getClusteredDataInterface().getClusterCenters(),
//                    MarkdownDocument.fromPluginResource("documentation/parameter_sample_cluster_centers.md"));
//        }
//        if(subject != null) {
//            addSlotToForm("Raw image",
//                    subject.getRawDataInterface().getRawImage(),
//                    MarkdownDocument.fromPluginResource("documentation/parameter_sample_raw_image.md"));
//            addSlotToForm("Tissue ROI",
//                    subject.getRawDataInterface().getTissueROI(),
//                    MarkdownDocument.fromPluginResource("documentation/parameter_sample_roi.md"));
//            addSlotToForm("Preprocessed image",
//                    subject.getPreprocessedDataInterface().getPreprocessedImage(),
//                    MarkdownDocument.fromPluginResource("documentation/parameter_sample_preprocessed_image.md"));
//            addSlotToForm("Derivative matrix",
//                    subject.getPreprocessedDataInterface().getDerivativeMatrix(),
//                    MarkdownDocument.fromPluginResource("documentation/parameter_sample_derivative_matrix.md"));
//        }
//
//        formPanel.addVerticalGlue();
//
//        add(formPanel, BorderLayout.CENTER);
//    }
}
