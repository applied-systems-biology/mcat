package org.hkijena.mcat.extension.datatypes;

import ij.IJ;
import ij.ImagePlus;
import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.utils.api.ACAQDocumentation;

import java.nio.file.Path;

/**
 * Contains a hyperstack as {@link ImagePlus}
 */
@ACAQDocumentation(name = "Image hyperstack")
public class HyperstackData implements MCATData {

    private ImagePlus image;

    public HyperstackData(ImagePlus image) {
        this.image = image;
    }

    @Override
    public void saveTo(Path folder, String name, String identifier) {
        IJ.save(image, folder.resolve(identifier + name + ".tif").toString());
    }

    public ImagePlus getImage() {
        return image;
    }
}
