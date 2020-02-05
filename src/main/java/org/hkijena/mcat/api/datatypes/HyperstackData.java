package org.hkijena.mcat.api.datatypes;

import ij.IJ;
import ij.ImagePlus;
import org.hkijena.mcat.api.MCATData;

import java.nio.file.Path;

/**
 * Contains a hyperstack as {@link ImagePlus}
 */
public class HyperstackData extends MCATData {

    private ImagePlus image;

    public HyperstackData(ImagePlus image) {
        this.image = image;
    }

    @Override
    public void saveTo(Path folder, String name) {
        IJ.save(image, folder.resolve(name + "tif").toString());
    }

    public ImagePlus getImage() {
        return image;
    }
}
