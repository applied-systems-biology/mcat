package org.hkijena.mcat.ui;

import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.api.MCATProjectDataSet;
import org.hkijena.mcat.api.events.MCATDataSetRenamedEvent;
import org.hkijena.mcat.ui.components.FormPanel;
import org.hkijena.mcat.ui.components.MarkdownDocument;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UI for a {@link MCATProjectDataSet}
 */
public class MCATDataSetUI extends MCATUIPanel {

    private static final String COMPONENTS_GENERAL = "General";
    private static final String COMPONENTS_RAW_DATA = "Raw data input";
    private static final String COMPONENTS_PREPROCESSED_DATA = "Preprocessed data input";
    private static final String COMPONENTS_CLUSTERED_DATA = "Clustered data input";

    private MCATProjectDataSet sample;
    private JLabel sampleTitle;
    private FormPanel formPanel;

    private List<MCATDataSlotUI> slotUIList = new ArrayList<>();

    public MCATDataSetUI(MCATWorkbenchUI workbenchUI, MCATProjectDataSet sample) {
        super(workbenchUI);
        this.sample = sample;
        initialize();
        getProject().getEventBus().register(this);
    }

    private void initialize() {
        setLayout(new BorderLayout());
        initializeTitlePanel();
        formPanel = new FormPanel(null, FormPanel.WITH_SCROLLING);
        add(formPanel, BorderLayout.CENTER);

        initializeGeneralSettings();
        initializeRawDataSettings();
        initializePreprocessedDataSettings();
        initializeClusteredDataSettings();
        formPanel.addVerticalGlue();

        resizeSlotButtons();
    }

    private void resizeSlotButtons() {
        int buttonSize = 0;
        for(MCATDataSlotUI ui : slotUIList) {
            buttonSize = Math.max(buttonSize, ui.getSelectionButton().getPreferredSize().width);
        }
        for(MCATDataSlotUI ui : slotUIList) {
            ui.getSelectionButton().setPreferredSize(new Dimension(buttonSize, ui.getSelectionButton().getPreferredSize().height));
        }
    }

    private void initializeTitlePanel() {
        JToolBar panel = new JToolBar();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        sampleTitle = new JLabel(sample.getName(), sample.getIcon(), SwingConstants.LEFT);
        panel.add(sampleTitle);

        panel.add(Box.createHorizontalGlue());

        JButton renameButton = new JButton("Rename", UIUtils.getIconFromResources("edit.png"));
        renameButton.addActionListener(e -> renameSample());
        panel.add(renameButton);

        JButton removeButton = new JButton(UIUtils.getIconFromResources("delete.png"));
        removeButton.addActionListener(e -> removeSample());
        panel.add(removeButton);

        add(panel, BorderLayout.NORTH);
    }

    private void initializeGeneralSettings() {
        // Add treatment selection
        JComboBox<String> treatmentEditor = formPanel.addToForm(new JComboBox<>(sample.getProject().getKnownTreatments().toArray(new String[0])),
                new JLabel("Treatment"),
                MarkdownDocument.fromPluginResource("documentation/parameter_sample_treatment.md"));
        treatmentEditor.setEditable(true);
        treatmentEditor.setSelectedItem(sample.getParameters().getTreatment());
        treatmentEditor.addActionListener(e -> sample.getParameters().setTreatment("" + treatmentEditor.getSelectedItem()));
    }

    private void initializeClusteredDataSettings() {
        MCATDataSlotUI clusterCentersEditor = formPanel.addToForm(new MCATDataSlotUI(sample, sample.getClusteredDataInterface().getClusterCenters()),
                new JLabel("Cluster centers"),
                MarkdownDocument.fromPluginResource("documentation/parameter_sample_cluster_centers.md"));
        slotUIList.add(clusterCentersEditor);
        MCATDataSlotUI clusterImageEditor = formPanel.addToForm(new MCATDataSlotUI(sample, sample.getClusteredDataInterface().getClusterImages()),
                new JLabel("Cluster image"),
                MarkdownDocument.fromPluginResource("documentation/parameter_sample_cluster_image.md"));
        slotUIList.add(clusterImageEditor);
    }

    private void initializePreprocessedDataSettings() {
        MCATDataSlotUI preprocessedImageEditor = formPanel.addToForm(new MCATDataSlotUI(sample, sample.getPreprocessedDataInterface().getPreprocessedImage()),
                new JLabel("Preprocessed image"),
                MarkdownDocument.fromPluginResource("documentation/parameter_sample_preprocessed_image.md"));
        slotUIList.add(preprocessedImageEditor);
        MCATDataSlotUI derivativeMatrixEditor = formPanel.addToForm(new MCATDataSlotUI(sample, sample.getPreprocessedDataInterface().getDerivativeMatrix()),
                new JLabel("Derivative matrix"),
                MarkdownDocument.fromPluginResource("documentation/parameter_sample_derivative_matrix.md"));
        slotUIList.add(derivativeMatrixEditor);
    }

    private void initializeRawDataSettings() {
        MCATDataSlotUI rawImageEditor = formPanel.addToForm(new MCATDataSlotUI(sample, sample.getRawDataInterface().getRawImage()),
                new JLabel("Raw image"),
                MarkdownDocument.fromPluginResource("documentation/parameter_sample_raw_image.md"));
        slotUIList.add(rawImageEditor);
        MCATDataSlotUI roiEditor = formPanel.addToForm(new MCATDataSlotUI(sample, sample.getRawDataInterface().getTissueROI()),
                new JLabel("Tissue ROI"),
                MarkdownDocument.fromPluginResource("documentation/parameter_sample_roi.md"));
        slotUIList.add(roiEditor);
    }

    private void renameSample() {
        String newName = JOptionPane.showInputDialog(this,"Please input a new name", sample.getName());
        if(newName != null && !newName.isEmpty() && !newName.equals(sample.getName())) {
            getProject().renameSample(sample, newName);
        }
    }

    private void removeSample() {
        getProject().removeSample(getSample());
    }

    @Subscribe
    public void onSampleRenamed(MCATDataSetRenamedEvent event) {
        if(event.getSample() == sample) {
            sampleTitle.setText(sample.getName());
        }
    }

    public MCATProjectDataSet getSample() {
        return sample;
    }
}
