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

        Object o = ((DefaultMutableTreeNode)value).getUserObject();
        if(o instanceof MCATResultDataInterfaces.SlotEntry) {
            MCATResultDataInterfaces.SlotEntry slotEntry = (MCATResultDataInterfaces.SlotEntry) o;
            setText(slotEntry.getName());
            setIcon(UIUtils.getIconFromResources("database.png"));
        }
        else {
            String content = "" + o;
            if(content.startsWith("Name:")) {
                content = content.substring("Name:".length());
                setIcon(UIUtils.getIconFromResources("database.png"));
            }
            else if(content.startsWith("Parameter:")) {
                content = content.substring("Parameter:".length());
                setIcon(UIUtils.getIconFromResources("cog.png"));
            }
            else if(content.startsWith("DataSet:")) {
                content = content.substring("DataSet:".length());
                setIcon(UIUtils.getIconFromResources("sample.png"));
            }

            setText(content);
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
