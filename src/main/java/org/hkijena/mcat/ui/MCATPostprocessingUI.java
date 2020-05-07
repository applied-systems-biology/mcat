package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.parameters.MCATPostprocessingParameters;
import org.hkijena.mcat.ui.components.FormPanel;
import org.hkijena.mcat.ui.components.MarkdownDocument;

import javax.swing.*;
import java.awt.*;

/**
 * UI for postprocessing parameters
 */
public class MCATPostprocessingUI extends MCATUIPanel {
    public MCATPostprocessingUI(MCATWorkbenchUI workbenchUI) {
        super(workbenchUI);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        FormPanel formPanel = new FormPanel(null, FormPanel.WITH_DOCUMENTATION | FormPanel.WITH_SCROLLING);

        MCATPostprocessingParameters parameters = getProject().getPostprocessingParameters();

        JCheckBox analyzeNetIncreaseEditor = formPanel.addToForm(new JCheckBox("Analyze net increase", parameters.isAnalyzeNetIncrease()),
                MarkdownDocument.fromPluginResource("documentation/parameter_postprocessing_analyze_net_increase.md"));
        analyzeNetIncreaseEditor.addActionListener(e -> parameters.setAnalyzeNetIncrease(analyzeNetIncreaseEditor.isSelected()));

        JCheckBox analyzeNetDecreaseEditor = formPanel.addToForm(new JCheckBox("Analyze net decrease", parameters.isAnalyzeNetDecrease()),
                MarkdownDocument.fromPluginResource("documentation/parameter_postprocessing_analyze_net_decrease.md"));
        analyzeNetDecreaseEditor.addActionListener(e -> parameters.setAnalyzeNetDecrease(analyzeNetDecreaseEditor.isSelected()));

        JCheckBox analyzeMaxIncreaseEditor = formPanel.addToForm(new JCheckBox("Analyze max increase", parameters.isAnalyzeMaxIncrease()),
                MarkdownDocument.fromPluginResource("documentation/parameter_postprocessing_analyze_max_increase.md"));
        analyzeMaxIncreaseEditor.addActionListener(e -> parameters.setAnalyzeMaxIncrease(analyzeMaxIncreaseEditor.isSelected()));

        JCheckBox analyzeMaxDecreaseEditor = formPanel.addToForm(new JCheckBox("Analyze max decrease", parameters.isAnalyzeMaxDecrease()),
                MarkdownDocument.fromPluginResource("documentation/parameter_postprocessing_analyze_max_decrease.md"));
        analyzeMaxDecreaseEditor.addActionListener(e -> parameters.setAnalyzeMaxDecrease(analyzeMaxDecreaseEditor.isSelected()));

        JCheckBox performClusterMorphologyAnalysisEditor = formPanel.addToForm(new JCheckBox("Perform cluster morphology analysis", parameters.isPerformClusterMorphologyAnalysis()),
                MarkdownDocument.fromPluginResource("documentation/parameter_postprocessing_perform_cluster_morphology_analysis.md"));
        performClusterMorphologyAnalysisEditor.addActionListener(e -> parameters.setPerformClusterMorphologyAnalysis(performClusterMorphologyAnalysisEditor.isSelected()));

        formPanel.addVerticalGlue();
        add(formPanel, BorderLayout.CENTER);
    }
}
