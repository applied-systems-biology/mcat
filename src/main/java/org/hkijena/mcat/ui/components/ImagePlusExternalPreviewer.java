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

import ij.IJ;
import ij.ImagePlus;

import java.awt.*;

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
