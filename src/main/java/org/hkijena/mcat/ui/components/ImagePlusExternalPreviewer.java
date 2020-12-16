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

import java.awt.Dimension;
import java.awt.Point;

import ij.IJ;
import ij.ImagePlus;

/**
 * Allows preview of multiple {@link ImagePlus} instances.
 * The user can change to another preview that will close the old image, but applies the Window position & sizing to the preview of the new image
 */
public class ImagePlusExternalPreviewer {
    private ImagePlus currentImage;
    private Point lastWindowLocation;
    private Dimension lastWindowSize;

    public ImagePlusExternalPreviewer() {

    }

    public ImagePlus getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(ImagePlus newImage) {
        backupLocationAndSize();
        hide();
        currentImage = newImage.duplicate();
        if (currentImage != null) {
            currentImage.setTitle("Preview");
        }
        show();
    }

    public void show() {
        if (currentImage != null) {
            currentImage.show();
            if (currentImage.getWindow() == null) {
                IJ.wait(100);
            }
            if (currentImage.getWindow() != null && lastWindowLocation != null && lastWindowSize != null) {
                currentImage.getWindow().setBounds(lastWindowLocation.x,
                        lastWindowLocation.y,
                        lastWindowSize.width,
                        lastWindowSize.height);
                currentImage.getWindow().getCanvas().fitToWindow();
            }
        }
    }

    public void hide() {
        if (currentImage != null) {
            currentImage.hide();
        }
    }

    private void backupLocationAndSize() {
        if (currentImage != null && currentImage.getWindow() != null) {
            lastWindowLocation = currentImage.getWindow().getLocation();
            lastWindowSize = currentImage.getWindow().getSize();
        }
    }
}
