package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.parameters.MCATPreprocessingParameters;
import org.hkijena.mcat.ui.components.FormPanel;

import javax.swing.*;
import java.awt.*;

/**
 * UI for preprocessing parameters
 */
public class MCATPreprocessingUI extends MCATUIPanel {

    public MCATPreprocessingUI(MCATWorkbenchUI workbenchUI) {
        super(workbenchUI);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        FormPanel formPanel = new FormPanel();

        MCATPreprocessingParameters parameters = getProject().getPreprocessingParameters();

        // Downsampling factor
        JSpinner downsamplingFactorEditor = formPanel.addToForm(new JSpinner(new SpinnerNumberModel(parameters.getDownsamplingFactor(),
                0, Integer.MAX_VALUE, 1)),
                new JLabel("Downsampling factor"),
                "documentation/parameter_preprocessing_downsampling_factor.md");
        downsamplingFactorEditor.addChangeListener(e -> parameters.setDownsamplingFactor((Integer)downsamplingFactorEditor.getValue()));

        // Channel of interest
        JSpinner channelOfInterestEditor = formPanel.addToForm(new JSpinner(new SpinnerNumberModel(parameters.getChannelOfInterest(),
                0, Integer.MAX_VALUE, 1)), new JLabel("Channel of interest"),
                "documentation/parameter_preprocessing_channel_of_interest.md");
        channelOfInterestEditor.addChangeListener(e -> parameters.setChannelOfInterest((Integer)channelOfInterestEditor.getValue()));

        // Anatomic channel
        JSpinner anatomicChannelEditor = formPanel.addToForm(new JSpinner(new SpinnerNumberModel(parameters.getAnatomicChannel(),
                0, Integer.MAX_VALUE, 1)), new JLabel("Anatomic channel"),
                "documentation/parameter_preprocessing_anatomic_channel.md");
        anatomicChannelEditor.addChangeListener(e -> parameters.setAnatomicChannel((Integer)anatomicChannelEditor.getValue()));
        
        // Save raw image?
        JCheckBox saveRawImageEditor = formPanel.addToForm(new JCheckBox("Save raw images in output folder?", parameters.isSaveRawImage()),
                "documentation/parameter_preprocessing_save_raw_image.md");
        saveRawImageEditor.addActionListener(e -> parameters.setSaveRawImage(saveRawImageEditor.isSelected()));
        
        // Save tissue roi?
        JCheckBox saveRoiEditor = formPanel.addToForm(new JCheckBox("Save tissue ROI in output folder?", parameters.isSaveRoi()),
                "documentation/parameter_preprocessing_save_roi.md");
        saveRoiEditor.addActionListener(e -> parameters.setSaveRoi(saveRoiEditor.isSelected()));
        	
        formPanel.addVerticalGlue();
        add(formPanel, BorderLayout.CENTER);
    }
}
