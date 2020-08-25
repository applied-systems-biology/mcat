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

package org.hkijena.mcat.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;

/**
 * Icon that is only one specific color, including border
 */
public class ColorIcon implements Icon {
    private int imageWidth;
    private int imageHeight;

    private Color color;
    private Color border;
    private Insets insets;

    public ColorIcon() {
        this(16, 16);
    }

    public ColorIcon(int width, int height) {
        this(width, height, Color.black);
    }

    public ColorIcon(int width, int height, Color c) {
        imageWidth = width;
        imageHeight = height;

        color = c;
        border = Color.black;
        insets = new Insets(1, 1, 1, 1);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color c) {
        color = c;
    }

    public void setBorderColor(Color c) {
        border = c;
    }

    public int getIconWidth() {
        return imageWidth;
    }

    public int getIconHeight() {
        return imageHeight;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(border);
        g.drawRect(x, y, imageWidth - 1, imageHeight - 2);

        x += insets.left;
        y += insets.top;

        int w = imageWidth - insets.left - insets.right;
        int h = imageHeight - insets.top - insets.bottom - 1;

        g.setColor(color);
        g.fillRect(x, y, w, h);
    }
}