package org.hkijena.mcat.ui.resultanalysis;

import com.fasterxml.jackson.databind.JsonNode;
import org.hkijena.mcat.api.MCATResultDataInterfaces;
import org.hkijena.mcat.utils.StringUtils;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
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

        List<MCATResultDataInterfaces.SlotEntry> rootNodeEntries = new ArrayList<>();
        MCATResultTreeNode rootNode = new MCATResultTreeNode(rootNodeName, MCATResultTreeNode.NodeType.RootGroup, rootNodeEntries);

        Map<String, List<MCATResultDataInterfaces.DataInterfaceEntry>> groupedByName = resultDataInterfaces.getEntries().stream()
                .collect(Collectors.groupingBy(MCATResultDataInterfaces.DataInterfaceEntry::getName));

        for (Map.Entry<String, List<MCATResultDataInterfaces.DataInterfaceEntry>> byNameEntry : groupedByName.entrySet()) {
            List<MCATResultDataInterfaces.SlotEntry> nameNodeEntries = new ArrayList<>();
            MCATResultTreeNode nameNode = new MCATResultTreeNode(StringUtils.capitalizeFirstLetter( byNameEntry.getKey().replace('-', ' ')),
                    MCATResultTreeNode.NodeType.DataInterfaceGroup,
                    nameNodeEntries);

            for (Map.Entry<String, List<MCATResultDataInterfaces.DataInterfaceEntry>> byParameterEntry :
                    byNameEntry.getValue().stream().collect(Collectors.groupingBy(MCATResultDataInterfaces.DataInterfaceEntry::getParameterString)).entrySet()) {
                List<MCATResultDataInterfaces.SlotEntry> parameterNodeEntries = new ArrayList<>();
                MCATResultTreeNode parameterNode = new MCATResultTreeNode(byParameterEntry.getKey(),
                        MCATResultTreeNode.NodeType.ParameterGroup,
                        parameterNodeEntries);

                for (Map.Entry<List<String>, List<MCATResultDataInterfaces.DataInterfaceEntry>> byDataSetsEntry :
                        byParameterEntry.getValue().stream().collect(Collectors.groupingBy(MCATResultDataInterfaces.DataInterfaceEntry::getDataSets)).entrySet()) {
                    List<MCATResultDataInterfaces.SlotEntry> dataSetNodeEntries = new ArrayList<>();
                    MCATResultTreeNode dataSetNode = new MCATResultTreeNode(String.join(", ", byDataSetsEntry.getKey()),
                            MCATResultTreeNode.NodeType.DataSetGroup,
                            dataSetNodeEntries);

                    for (MCATResultDataInterfaces.DataInterfaceEntry dataInterfaceEntry : byDataSetsEntry.getValue()) {
                        for (MCATResultDataInterfaces.SlotEntry slot : dataInterfaceEntry.getSlots()) {
                            dataSetNode.add(new MCATResultTreeNode(slot.getName(), MCATResultTreeNode.NodeType.Slot,
                                    Arrays.asList(slot)));
                            dataSetNodeEntries.add(slot);
                        }
                    }

                    parameterNode.add(dataSetNode);
                    parameterNodeEntries.addAll(dataSetNodeEntries);
                }

                nameNode.add(parameterNode);
                nameNodeEntries.addAll(parameterNodeEntries);
            }
            rootNode.add(nameNode);
            rootNodeEntries.addAll(nameNodeEntries);
        }


        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        sampleTree.setModel(model);
        UIUtils.expandAllTree(sampleTree);
    }


    public JTree getSampleTree() {
        return sampleTree;
    }
}
