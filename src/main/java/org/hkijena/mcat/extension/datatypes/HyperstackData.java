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
