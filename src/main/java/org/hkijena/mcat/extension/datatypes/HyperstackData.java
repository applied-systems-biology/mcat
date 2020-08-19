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
package org.hkijena.mcat.extension.datatypes;

import ij.IJ;
import ij.ImagePlus;
import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDocumentation;

import java.nio.file.Path;

/**
 * Contains a hyperstack as {@link ImagePlus}
 */
@MCATDocumentation(name = "Image hyperstack")
public class HyperstackData implements MCATData {

    private ImagePlus image;

    public HyperstackData(ImagePlus image) {
        this.image = image;
    }

    @Override
    public void saveTo(Path folder, Path fileName) {
        IJ.save(image, folder.resolve(fileName).toString());
    }

    public ImagePlus getImage() {
        return image;
    }
}
