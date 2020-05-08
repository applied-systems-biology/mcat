package org.hkijena.mcat.ui.resultanalysis;

import org.hkijena.mcat.api.MCATResultDataInterfaces;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * Renders a sample in the {@link MCATResultSlotManagerUI}
 */
public class MCATRunSampleTreeCellRenderer extends JLabel implements TreeCellRenderer {

    public MCATRunSampleTreeCellRenderer() {
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (tree.getFont() != null) {
            setFont(tree.getFont());
        }

        if(value instanceof MCATResultTreeNode) {

            MCATResultTreeNode treeNode = (MCATResultTreeNode) value;
            setText("" + treeNode.getUserObject());
            switch (treeNode.getNodeType()) {
                case RootGroup:
                    setIcon(UIUtils.getIconFromResources("run.png"));
                    break;
                case DataInterfaceGroup:
                    setIcon(UIUtils.getIconFromResources("database.png"));
                    break;
                case ParameterGroup:
                    setIcon(UIUtils.getIconFromResources("cog.png"));
                    break;
                case DataSetGroup:
                    setIcon(UIUtils.getIconFromResources("sample.png"));
                    break;
                case Slot:
                    setIcon(UIUtils.getIconFromResources("database.png"));
                    break;
            }
        }
        else {
            setText("<Invalid>");
        }

        // Update status
        if (selected) {
            setBackground(new Color(184, 207, 229));
        } else {
            setBackground(new Color(255, 255, 255));
        }

        return this;
    }
}
