package org.hkijena.mcat.ui.components;

import org.hkijena.mcat.api.MCATProjectDataSet;
import org.hkijena.mcat.ui.MCATDataSetManagerUI;
import org.hkijena.mcat.utils.UIUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.Collection;

/**
 * Renders a sample in the {@link MCATDataSetManagerUI}
 */
public class MCATSampleTreeCellRenderer extends JLabel implements TreeCellRenderer {

    public MCATSampleTreeCellRenderer() {
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if(tree.getFont() != null) {
            setFont(tree.getFont());
        }

        Object o = ((DefaultMutableTreeNode)value).getUserObject();
        if(o instanceof MCATProjectDataSet) {
            MCATProjectDataSet sample = (MCATProjectDataSet)o;
            setText(sample.getName());
            setIcon(new MonochromeColorIcon(UIUtils.getIconFromResources("sample-template.png"), sample.getTreatmentColor()));
        }
        else if(o instanceof Collection) {
            Collection<MCATProjectDataSet> samples = (Collection<MCATProjectDataSet>)o;
            String treatment = samples.iterator().next().getParameters().getTreatment();
            if(treatment == null || treatment.isEmpty())
                treatment = "<No treatment>";
            setText(treatment);
            setIcon(UIUtils.getIconFromResources("object.png"));
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
