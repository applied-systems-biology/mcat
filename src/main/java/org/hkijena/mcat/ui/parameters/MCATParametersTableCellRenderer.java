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
package org.hkijena.mcat.ui.parameters;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

public class MCATParametersTableCellRenderer extends JLabel implements TableCellRenderer {

    public MCATParametersTableCellRenderer() {
        setFont(new Font("Dialog", Font.PLAIN, 12));
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (value instanceof Integer && (int) value == Integer.MAX_VALUE) {
            setText("Max");
        } else {
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
