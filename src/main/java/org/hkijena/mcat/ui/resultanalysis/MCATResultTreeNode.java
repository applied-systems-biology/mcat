package org.hkijena.mcat.ui.resultanalysis;

import org.hkijena.mcat.api.MCATResultDataInterfaces;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

public class MCATResultTreeNode extends DefaultMutableTreeNode {

    private NodeType nodeType;
    private List<MCATResultDataInterfaces.SlotEntry> slotEntries;

    public MCATResultTreeNode(String label, NodeType nodeType, List<MCATResultDataInterfaces.SlotEntry> slotEntries) {
        super(label);
        this.nodeType = nodeType;
        this.slotEntries = slotEntries;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public List<MCATResultDataInterfaces.SlotEntry> getSlotEntries() {
        return slotEntries;
    }

    public enum NodeType {
        RootGroup,
        DataInterfaceGroup,
        ParameterGroup,
        DataSetGroup,
        Slot
    }

}
