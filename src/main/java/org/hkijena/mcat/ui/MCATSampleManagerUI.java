package org.hkijena.mcat.ui;

import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.api.MCATSample;
import org.hkijena.mcat.api.events.MCATParameterChangedEvent;
import org.hkijena.mcat.api.events.MCATSampleAddedEvent;
import org.hkijena.mcat.api.events.MCATSampleRemovedEvent;
import org.hkijena.mcat.api.events.MCATSampleRenamedEvent;
import org.hkijena.mcat.api.parameters.MCATSampleParameters;
import org.hkijena.mcat.ui.components.MCATSampleTreeCellRenderer;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

        JButton removeButton = new JButton("Remove selected", UIUtils.getIconFromResources("delete.png"));
        removeButton.addActionListener(e -> removeSelectedSamples());
        toolBar.add(removeButton);
    }

    private void removeSelectedSamples() {
        Set<MCATSample> toRemove = new HashSet<>();
        if(getSampleTree().getSelectionPaths() != null) {
            for(TreePath path : getSampleTree().getSelectionPaths()) {
                DefaultMutableTreeNode nd = (DefaultMutableTreeNode)path.getLastPathComponent();
                if(nd.getUserObject() instanceof MCATSample) {
                    toRemove.add((MCATSample)nd.getUserObject());
                }
            }
        }
        for(MCATSample sample : toRemove) {
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

        MCATSample selectedSample = null;
        if(sampleTree.getLastSelectedPathComponent() != null) {
            DefaultMutableTreeNode nd = (DefaultMutableTreeNode) sampleTree.getLastSelectedPathComponent();
            if(nd.getUserObject() instanceof MCATSample) {
                selectedSample = (MCATSample)nd.getUserObject();
            }
        }

        DefaultMutableTreeNode toSelect = null;

        String rootNodeName = "Samples";
        if(getProject().getSamples().isEmpty()) {
            rootNodeName = "No samples";
        }
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootNodeName);
        Map<String, Set<MCATSample>> groupedSamples = getProject().getSamplesGroupedByTreatment();
        for(Map.Entry<String, Set<MCATSample>> kv : groupedSamples.entrySet()) {
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(kv.getValue());
            for(MCATSample sample : kv.getValue().stream().sorted().collect(Collectors.toList())) {
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
    public void onSampleTreatmentChanged(MCATParameterChangedEvent event) {
        if(event.getSource() instanceof MCATSampleParameters && event.getName().equals("treatment")) {
            rebuildSampleListTree();
        }
    }

    public JTree getSampleTree() {
        return sampleTree;
    }
}
