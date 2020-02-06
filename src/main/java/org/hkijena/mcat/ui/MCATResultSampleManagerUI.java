package org.hkijena.mcat.ui;

import org.hkijena.mcat.api.MCATRun;
import org.hkijena.mcat.api.MCATRunSample;
import org.hkijena.mcat.api.MCATRunSampleSubject;
import org.hkijena.mcat.ui.components.MCATRunSampleTreeCellRenderer;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.stream.Collectors;

public class MCATResultSampleManagerUI extends JPanel {
    private MCATResultUI resultUI;
    private JTree sampleTree;

    public MCATResultSampleManagerUI(MCATResultUI resultUI) {
        this.resultUI = resultUI;
        initialize();
        rebuildSampleListTree();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        sampleTree = new JTree();
        sampleTree.setCellRenderer(new MCATRunSampleTreeCellRenderer());
        add(new JScrollPane(sampleTree), BorderLayout.CENTER);
    }

    public MCATRun getRun() {
        return resultUI.getRun();
    }

    private void rebuildSampleListTree() {
        String rootNodeName = "Samples";
        if(getRun().getSamples().isEmpty()) {
            rootNodeName = "No samples";
        }

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootNodeName);

        for(MCATRunSample sample : getRun().getSamples().values().stream().sorted().collect(Collectors.toList())) {
            DefaultMutableTreeNode sampleNode = new DefaultMutableTreeNode(sample, true);
            for(MCATRunSampleSubject subject : sample.getSubjects().values().stream().sorted().collect(Collectors.toList())) {
                DefaultMutableTreeNode subjectNode = new DefaultMutableTreeNode(subject);
                sampleNode.add(subjectNode);
            }
            rootNode.add(sampleNode);
        }

        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        sampleTree.setModel(model);
        UIUtils.expandAllTree(sampleTree);
    }


    public JTree getSampleTree() {
        return sampleTree;
    }
}
