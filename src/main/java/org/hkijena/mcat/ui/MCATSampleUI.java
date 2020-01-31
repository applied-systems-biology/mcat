package org.hkijena.mcat.ui;

import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.api.MCATSample;
import org.hkijena.mcat.api.events.MCATSampleRenamedEvent;
import org.hkijena.mcat.ui.components.FormPanel;
import org.hkijena.mcat.ui.components.RadioButtonGroup;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class MCATSampleUI extends MCATUIPanel {

    private static final String COMPONENTS_GENERAL = "General";
    private static final String COMPONENTS_RAW_DATA = "Raw data input";
    private static final String COMPONENTS_PREPROCESSED_DATA = "Preprocessed data input";
    private static final String COMPONENTS_CLUSTERED_DATA = "Clustered data input";

    private MCATSample sample;
    private JLabel sampleTitle;
    private FormPanel formPanel;

    public MCATSampleUI(MCATWorkbenchUI workbenchUI, MCATSample sample) {
        super(workbenchUI);
        this.sample = sample;
        initialize();
        getProject().getEventBus().register(this);
    }

    private void initialize() {
        setLayout(new BorderLayout());
        initializeTitlePanel();
        formPanel = new FormPanel();
        add(formPanel, BorderLayout.CENTER);

        initializeGeneralSettings();
        initializeRawDataSettings();
        initializePreprocessedDataSettings();
        initializeClusteredDataSettings();
        formPanel.addVerticalGlue();
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
        formPanel.setCurrentGroup(COMPONENTS_GENERAL);

        // Add treatment selection
        JComboBox<String> treatmentEditor = formPanel.addToForm(new JComboBox<>(), new JLabel("Treatment"), null);
        treatmentEditor.setEditable(true);
        treatmentEditor.setSelectedItem(sample.getParameters().getTreatment());
        treatmentEditor.addActionListener(e -> {
            sample.getParameters().setTreatment("" + treatmentEditor.getSelectedItem());
        });

        // Add input type selection
        RadioButtonGroup<MCATSample.InputType> inputTypeEditor = formPanel.addToForm(new RadioButtonGroup<>(Arrays.asList(MCATSample.InputType.values())),
                new JLabel("Input type"), null);

    }

    private void initializeClusteredDataSettings() {
        formPanel.setCurrentGroup(COMPONENTS_CLUSTERED_DATA);
    }

    private void initializePreprocessedDataSettings() {
        formPanel.setCurrentGroup(COMPONENTS_PREPROCESSED_DATA);
    }

    private void initializeRawDataSettings() {
        formPanel.setCurrentGroup(COMPONENTS_RAW_DATA);
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
    public void onSampleRenamed(MCATSampleRenamedEvent event) {
        if(event.getSample() == sample) {
            sampleTitle.setText(sample.getName());
        }
    }

    public MCATSample getSample() {
        return sample;
    }
}
