package org.hkijena.mcat.ui.resultanalysis;

import com.fasterxml.jackson.databind.JsonNode;
import org.hkijena.mcat.api.MCATResultDataInterfaces;
import org.hkijena.mcat.utils.StringUtils;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MCATResultSlotManagerUI extends JPanel {
    private MCATResultUI resultUI;
    private JTree sampleTree;

    public MCATResultSlotManagerUI(MCATResultUI resultUI) {
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

    private void rebuildSampleListTree() {
        MCATResultDataInterfaces resultDataInterfaces = resultUI.getResult().getResultDataInterfaces();
        String rootNodeName = "Results";
        if(resultDataInterfaces.getEntries().isEmpty()) {
            rootNodeName = "No results";
        }

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootNodeName);

        Map<String, List<MCATResultDataInterfaces.DataInterfaceEntry>> groupedByName = resultDataInterfaces.getEntries().stream()
                .collect(Collectors.groupingBy(MCATResultDataInterfaces.DataInterfaceEntry::getName));

        for (Map.Entry<String, List<MCATResultDataInterfaces.DataInterfaceEntry>> byNameEntry : groupedByName.entrySet()) {
            DefaultMutableTreeNode nameNode = new DefaultMutableTreeNode(
                    "Name:" + StringUtils.capitalizeFirstLetter( byNameEntry.getKey().replace('-', ' ')));
            for (Map.Entry<String, List<MCATResultDataInterfaces.DataInterfaceEntry>> byParameterEntry :
                    byNameEntry.getValue().stream().collect(Collectors.groupingBy(MCATResultDataInterfaces.DataInterfaceEntry::getParameterString)).entrySet()) {
                DefaultMutableTreeNode parameterNode = new DefaultMutableTreeNode("Parameter:" + byParameterEntry.getKey());

                for (Map.Entry<List<String>, List<MCATResultDataInterfaces.DataInterfaceEntry>> byDataSetsEntry :
                        byParameterEntry.getValue().stream().collect(Collectors.groupingBy(MCATResultDataInterfaces.DataInterfaceEntry::getDataSets)).entrySet()) {
                    DefaultMutableTreeNode dataSetNode = new DefaultMutableTreeNode("DataSet:" + String.join(", ", byDataSetsEntry.getKey()));

                    for (MCATResultDataInterfaces.DataInterfaceEntry dataInterfaceEntry : byDataSetsEntry.getValue()) {
                        for (MCATResultDataInterfaces.SlotEntry slot : dataInterfaceEntry.getSlots()) {
                            dataSetNode.add(new DefaultMutableTreeNode(slot));
                        }
                    }

                    parameterNode.add(dataSetNode);
                }

                nameNode.add(parameterNode);
            }
            rootNode.add(nameNode);
        }


        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        sampleTree.setModel(model);
        UIUtils.expandAllTree(sampleTree);
    }


    public JTree getSampleTree() {
        return sampleTree;
    }
}
