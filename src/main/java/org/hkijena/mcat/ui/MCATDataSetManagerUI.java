package org.hkijena.mcat.ui;

import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.api.MCATProjectDataSet;
import org.hkijena.mcat.api.events.MCATDataSetAddedEvent;
import org.hkijena.mcat.api.events.MCATDataSetRemovedEvent;
import org.hkijena.mcat.api.events.MCATDataSetRenamedEvent;
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
public class MCATDataSetManagerUI extends MCATWorkbenchUIPanel {

    private JTree sampleTree;

    public MCATDataSetManagerUI(MCATWorkbenchUI workbenchUI) {
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
        changeTreatmentButton.setToolTipText("Set treatment of selected data sets");
        changeTreatmentButton.addActionListener(e -> relabelSelectedSamples());
        toolBar.add(changeTreatmentButton);

        JButton removeButton = new JButton(UIUtils.getIconFromResources("delete.png"));
        removeButton.setToolTipText("Remove selected data sets");;
        removeButton.addActionListener(e -> removeSelectedSamples());
        toolBar.add(removeButton);

    }

    private void relabelSelectedSamples() {
        Set<MCATProjectDataSet> toRelabel = new HashSet<>();
        Set<String> treatments = new HashSet<>();
        if(getSampleTree().getSelectionPaths() != null) {
            for(TreePath path : getSampleTree().getSelectionPaths()) {
                DefaultMutableTreeNode nd = (DefaultMutableTreeNode)path.getLastPathComponent();
                if(nd.getUserObject() instanceof MCATProjectDataSet) {
                    toRelabel.add((MCATProjectDataSet)nd.getUserObject());
                    treatments.add(((MCATProjectDataSet)(nd.getUserObject())).getParameters().getTreatment());
                }
            }
        }

        if(!toRelabel.isEmpty()) {
            String suggestion = String.join("_", treatments);
            String newName = JOptionPane.showInputDialog(this,"Please input a new treatment", suggestion);
            if(newName != null && !newName.isEmpty()) {
                for(MCATProjectDataSet sample : toRelabel) {
                    sample.getParameters().setTreatment(newName);
                }
            }
        }
    }

    private void removeSelectedSamples() {
        Set<MCATProjectDataSet> toRemove = new HashSet<>();
        if(getSampleTree().getSelectionPaths() != null) {
            for(TreePath path : getSampleTree().getSelectionPaths()) {
                DefaultMutableTreeNode nd = (DefaultMutableTreeNode)path.getLastPathComponent();
                if(nd.getUserObject() instanceof MCATProjectDataSet) {
                    toRemove.add((MCATProjectDataSet)nd.getUserObject());
                }
            }
        }
        for(MCATProjectDataSet sample : toRemove) {
            getProject().removeSample(sample);
        }
    }

    private void addSamples() {
        MCATAddProjectDataSetsDialog dialog = new MCATAddProjectDataSetsDialog(getWorkbenchUI());
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

        MCATProjectDataSet selectedSample = null;
        if(sampleTree.getLastSelectedPathComponent() != null) {
            DefaultMutableTreeNode nd = (DefaultMutableTreeNode) sampleTree.getLastSelectedPathComponent();
            if(nd.getUserObject() instanceof MCATProjectDataSet) {
                selectedSample = (MCATProjectDataSet)nd.getUserObject();
            }
        }

        DefaultMutableTreeNode toSelect = null;

        String rootNodeName = "Data sets";
        if(getProject().getSamples().isEmpty()) {
            rootNodeName = "No data sets";
        }
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootNodeName);
        Map<String, Set<MCATProjectDataSet>> groupedSamples = getProject().getSamplesGroupedByTreatment();
        for(Map.Entry<String, Set<MCATProjectDataSet>> kv : groupedSamples.entrySet()) {
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(kv.getValue());
            for(MCATProjectDataSet sample : kv.getValue().stream().sorted().collect(Collectors.toList())) {
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
    public void onDataSetAdded(MCATDataSetAddedEvent event) {
        rebuildSampleListTree();
    }

    @Subscribe
    public void onDataSetRemoved(MCATDataSetRemovedEvent event) {
        rebuildSampleListTree();
    }

    @Subscribe
    public void onDataSetRenamed(MCATDataSetRenamedEvent event) {
        rebuildSampleListTree();
    }

    @Subscribe
    public void onDataSetTreatmentChanged(ParameterChangedEvent event) {
        if(event.getSource() instanceof MCATSampleParameters && event.getKey().equals("treatment")) {
            rebuildSampleListTree();
        }
    }

    public JTree getSampleTree() {
        return sampleTree;
    }
}
