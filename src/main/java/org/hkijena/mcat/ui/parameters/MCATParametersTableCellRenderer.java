package org.hkijena.mcat.ui.parameters;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MCATParametersTableCellRenderer extends JLabel implements TableCellRenderer {

    public MCATParametersTableCellRenderer() {
        setFont(new Font("Dialog", Font.PLAIN, 12));
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        setText("" + value);
        if (isSelected) {
            setBackground(new Color(184, 207, 229));
        } else {
            if(column == 0) {
                setBackground(new Color(242, 242, 242));
            }
            else {
                setBackground(new Color(255, 255, 255));
            }
        }

        return this;
    }
}
