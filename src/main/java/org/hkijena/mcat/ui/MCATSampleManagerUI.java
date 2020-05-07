package org.hkijena.mcat.ui;

import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.api.MCATProjectSample;
import org.hkijena.mcat.api.events.MCATSampleAddedEvent;
import org.hkijena.mcat.api.events.MCATSampleRemovedEvent;
import org.hkijena.mcat.api.events.MCATSampleRenamedEvent;
import org.hkijena.mcat.api.parameters.MCATSampleParameters;
import org.hkijena.mcat.ui.components.MCATSampleTreeCellRenderer;
import org.hkijena.mcat.utils.UIUtils;
import org.hkijena.mcat.utils.api.events.ParameterChangedEvent;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UI that manages the samples in a {@link org.hkijena.mcat.api.MCATProject}
 */
public class MCATSampleManagerUI extends MCATUIPanel {

    private JTree sampleTree;

    public MCATSampleManagerUI(MCATWorkbenchUI workbenchUI) {
        super(workbenchUI);

        // Register events
        workbenchUI.getProject().getEventBus().register(this);

        // Initialize UI
        initialize();
        rebuildSampleListTree();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        sampleTree = new JTree();
        getSampleTree().setCellRenderer(new MCATSampleTreeCellRenderer());
        add(new JScrollPane(getSampleTree()), BorderLayout.CENTER);

        initializeToolbar();
    }

    private void initializeToolbar() {
        JToolBar toolBar = new JToolBar();
        add(toolBar, BorderLayout.NORTH);

        JButton addSamplesButton = new JButton("Add ...", UIUtils.getIconFromResources("add.png"));
        addSamplesButton.addActionListener(e -> addSamples());
        toolBar.add(addSamplesButton);

        JButton batchImportSamplesButton = new JButton("Batch import ...", UIUtils.getIconFromResources("import.png"));
        batchImportSamplesButton.addActionListener(e -> batchImportSamples());
        toolBar.add(batchImportSamplesButton);

        toolBar.add(Box.createHorizontalGlue());

        JButton changeTreatmentButton = new JButton(UIUtils.getIconFromResources("relabel-sample.png"));
        changeTreatmentButton.setToolTipText("Set treatment of selected samples");
        changeTreatmentButton.addActionListener(e -> relabelSelectedSamples());
        toolBar.add(changeTreatmentButton);

        JButton removeButton = new JButton(UIUtils.getIconFromResources("delete.png"));
        removeButton.setToolTipText("Remove selected samples");;
        removeButton.addActionListener(e -> removeSelectedSamples());
        toolBar.add(removeButton);

    }

    private void relabelSelectedSamples() {
        Set<MCATProjectSample> toRelabel = new HashSet<>();
        Set<String> treatments = new HashSet<>();
        if(getSampleTree().getSelectionPaths() != null) {
            for(TreePath path : getSampleTree().getSelectionPaths()) {
                DefaultMutableTreeNode nd = (DefaultMutableTreeNode)path.getLastPathComponent();
                if(nd.getUserObject() instanceof MCATProjectSample) {
                    toRelabel.add((MCATProjectSample)nd.getUserObject());
                    treatments.add(((MCATProjectSample)(nd.getUserObject())).getParameters().getTreatment());
                }
            }
        }

        if(!toRelabel.isEmpty()) {
            String suggestion = String.join("_", treatments);
            String newName = JOptionPane.showInputDialog(this,"Please input a new treatment", suggestion);
            if(newName != null && !newName.isEmpty()) {
                for(MCATProjectSample sample : toRelabel) {
                    sample.getParameters().setTreatment(newName);
                }
            }
        }
    }

    private void removeSelectedSamples() {
        Set<MCATProjectSample> toRemove = new HashSet<>();
        if(getSampleTree().getSelectionPaths() != null) {
            for(TreePath path : getSampleTree().getSelectionPaths()) {
                DefaultMutableTreeNode nd = (DefaultMutableTreeNode)path.getLastPathComponent();
                if(nd.getUserObject() instanceof MCATProjectSample) {
                    toRemove.add((MCATProjectSample)nd.getUserObject());
                }
            }
        }
        for(MCATProjectSample sample : toRemove) {
            getProject().removeSample(sample);
        }
    }

    private void addSamples() {
        MCATAddSamplesDialog dialog = new MCATAddSamplesDialog(getWorkbenchUI());
        dialog.setModal(true);
        dialog.pack();
        dialog.setSize(new Dimension(500,400));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void batchImportSamples() {
        MCATBatchImporterDialog dialog = new MCATBatchImporterDialog(getWorkbenchUI());
        dialog.setModal(true);
        dialog.pack();
        dialog.setSize(new Dimension(800,600));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void rebuildSampleListTree() {

        MCATProjectSample selectedSample = null;
        if(sampleTree.getLastSelectedPathComponent() != null) {
            DefaultMutableTreeNode nd = (DefaultMutableTreeNode) sampleTree.getLastSelectedPathComponent();
            if(nd.getUserObject() instanceof MCATProjectSample) {
                selectedSample = (MCATProjectSample)nd.getUserObject();
            }
        }

        DefaultMutableTreeNode toSelect = null;

        String rootNodeName = "Samples";
        if(getProject().getSamples().isEmpty()) {
            rootNodeName = "No samples";
        }
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootNodeName);
        Map<String, Set<MCATProjectSample>> groupedSamples = getProject().getSamplesGroupedByTreatment();
        for(Map.Entry<String, Set<MCATProjectSample>> kv : groupedSamples.entrySet()) {
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(kv.getValue());
            for(MCATProjectSample sample : kv.getValue().stream().sorted().collect(Collectors.toList())) {
                sample.getParameters().getEventBus().register(this);
                DefaultMutableTreeNode sampleNode = new DefaultMutableTreeNode(sample);
                if(sample == selectedSample) {
                    toSelect = sampleNode;
                }
                groupNode.add(sampleNode);
            }
            rootNode.add(groupNode);
        }

        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        getSampleTree().setModel(model);
        UIUtils.expandAllTree(getSampleTree());
        if(toSelect != null) {
            getSampleTree().setSelectionPath(new TreePath(model.getPathToRoot(toSelect)));
        }

    }

    @Subscribe
    public void onSampleAdded(MCATSampleAddedEvent event) {
        rebuildSampleListTree();
    }

    @Subscribe
    public void onSampleRemoved(MCATSampleRemovedEvent event) {
        rebuildSampleListTree();
    }

    @Subscribe
    public void onSampleRenamed(MCATSampleRenamedEvent event) {
        rebuildSampleListTree();
    }

    @Subscribe
    public void onSampleTreatmentChanged(ParameterChangedEvent event) {
        if(event.getSource() instanceof MCATSampleParameters && event.getKey().equals("treatment")) {
            rebuildSampleListTree();
        }
    }

    public JTree getSampleTree() {
        return sampleTree;
    }
}
