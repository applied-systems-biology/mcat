/*
 * Copyright by Ruman Gerst
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 *
 * This code is licensed under BSD 2-Clause
 * See the LICENSE file provided with this code for the full license.
 */

package org.hkijena.mcat.utils;

import org.hkijena.mcat.ui.components.ColorIcon;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UIUtils {

    public static final Insets UI_PADDING = new Insets(4,4,4,4);

    /**
     * Adds a popup menu to a button component that will be opened next to it if the button is clicked
     * @param target
     * @return
     */
    public static JPopupMenu addPopupMenuToComponent(AbstractButton target) {
        JPopupMenu popupMenu = new JPopupMenu();
        target.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }
        });
        target.addActionListener(e -> {

            if(MouseInfo.getPointerInfo().getLocation().x >= target.getLocationOnScreen().x
                    && MouseInfo.getPointerInfo().getLocation().x <= target.getLocationOnScreen().x + target.getWidth()
                    && MouseInfo.getPointerInfo().getLocation().y >= target.getLocationOnScreen().y
                    && MouseInfo.getPointerInfo().getLocation().y <= target.getLocationOnScreen().y + target.getHeight()) {

            }
            else {
                popupMenu.show(target, 0, target.getHeight());
            }
        });
        return popupMenu;
    }

    /**
     * Gets an icon from the "icons" resource folder
     * @param iconName Must include the file extension
     * @return
     */
    public static ImageIcon getIconFromResources(String iconName) {
        return new ImageIcon(ResourceUtils.getPluginResource("icons/" + iconName));
    }

    /**
     * Generates a 16x16 icon that has a single color
     * @param color
     * @return
     */
    public static ColorIcon getIconFromColor(Color color) {
        return new ColorIcon(16, 16, color);
    }

    /**
     * Makes a button have a flat style
     * @param component
     */
    public static void makeFlat(AbstractButton component) {
        component.setBackground(Color.WHITE);
        component.setOpaque(false);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder( BorderFactory.createEtchedBorder(), margin);
        component.setBorder(compound);
    }

    /**
     * Makes a button have a flat style
     * @param component
     */
    public static void makeFlatWithoutMargin(AbstractButton component) {
        component.setBackground(Color.WHITE);
        component.setOpaque(false);
        component.setBorder(null);
    }

    /**
     * Adds behavior to a window to let the user confirm to actually close the window
     * @param window
     * @param message
     * @param title
     */
    public static void setToAskOnClose(JFrame window, String message, String title) {
        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if(JOptionPane.showConfirmDialog(windowEvent.getComponent(), message, title,
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    windowEvent.getWindow().dispose();
                }
            }
        });
    }

    /**
     * Converts a color hash for a string
     * @param string Input string
     * @param s HSV Saturation 0.0 - 1.0
     * @param b HSV Brightness 0.0 - 1.0
     * @return
     */
    public static Color stringToColor(String string, float s, float b) {
        long hash = string == null ? 0 : string.hashCode();
        float h = Math.abs(hash % 256) / 255.0f;
        return Color.getHSBColor(h, s, b);
    }

    /**
     * Expands the whole tree
     * @param tree
     */
    public static void expandAllTree(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

}
