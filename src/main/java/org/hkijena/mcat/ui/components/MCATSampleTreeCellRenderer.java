/*******************************************************************************
 * Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge
 * 
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 * 
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 ******************************************************************************/
package org.hkijena.mcat.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.hkijena.mcat.api.MCATProjectDataSet;
import org.hkijena.mcat.ui.MCATDataSetManagerUI;
import org.hkijena.mcat.utils.UIUtils;

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
        if (tree.getFont() != null) {
            setFont(tree.getFont());
        }

        Object o = ((DefaultMutableTreeNode) value).getUserObject();
        if (o instanceof MCATProjectDataSet) {
            MCATProjectDataSet sample = (MCATProjectDataSet) o;
            setText(sample.getName());
            setIcon(new MonochromeColorIcon(UIUtils.getIconFromResources("sample-template.png"), sample.getTreatmentColor()));
        } else if (o instanceof Collection) {
            Collection<MCATProjectDataSet> samples = (Collection<MCATProjectDataSet>) o;
            String treatment = samples.iterator().next().getParameters().getTreatment();
            if (treatment == null || treatment.isEmpty())
                treatment = "<No treatment>";
            setText(treatment);
            setIcon(UIUtils.getIconFromResources("object.png"));
        } else {
            setText(o.toString());
            setIcon(null);
        }

        // Update status
        // Update status
        if (selected) {
            setBackground(new Color(184, 207, 229));
        } else {
            setBackground(new Color(255, 255, 255));
        }

        return this;
    }
}
