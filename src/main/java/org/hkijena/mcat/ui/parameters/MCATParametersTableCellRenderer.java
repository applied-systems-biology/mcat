/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/
package org.hkijena.mcat.ui.parameters;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class MCATParametersTableCellRenderer extends JLabel implements TableCellRenderer {

    public MCATParametersTableCellRenderer() {
        setFont(new Font("Dialog", Font.PLAIN, 12));
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if(value instanceof Integer && (int)value == Integer.MAX_VALUE) {
            setText("Max");
        }
        else {
            setText("" + value);
        }
        if (isSelected) {
            setBackground(new Color(184, 207, 229));
        } else {
            if (column == 0) {
                setBackground(new Color(242, 242, 242));
            } else {
                setBackground(new Color(255, 255, 255));
            }
        }

        return this;
    }
}
