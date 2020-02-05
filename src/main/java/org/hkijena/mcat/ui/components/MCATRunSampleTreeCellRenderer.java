package org.hkijena.mcat.ui.components;

import org.hkijena.mcat.api.MCATRunSample;
import org.hkijena.mcat.api.MCATRunSampleSubject;
import org.hkijena.mcat.ui.MCATResultSampleManagerUI;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * Renders a sample in the {@link MCATResultSampleManagerUI}
 */
public class MCATRunSampleTreeCellRenderer extends JLabel implements TreeCellRenderer {

    public MCATRunSampleTreeCellRenderer() {
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if(tree.getFont() != null) {
            setFont(tree.getFont());
        }

        Object o = ((DefaultMutableTreeNode)value).getUserObject();
        if(o instanceof MCATRunSample) {
            MCATRunSample sample = (MCATRunSample)o;
            setText(sample.getName());
            setIcon(UIUtils.getIconFromResources("object.png"));
        }
        else if(o instanceof MCATRunSampleSubject) {
            MCATRunSampleSubject subject = (MCATRunSampleSubject)o;
            setText(subject.getName());
            setIcon(UIUtils.getIconFromResources("sample.png"));
        }
        else {
            setText(o.toString());
            setIcon(null);
        }

        // Update status
        // Update status
        if(selected) {
            setBackground(new Color(184, 207, 229));
        }
        else {
            setBackground(new Color(255,255,255));
        }

        return this;
    }
}
