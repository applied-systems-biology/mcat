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
    public void saveTo(Path file) {
        IJ.save(image, file.toString());
    }

    public ImagePlus getImage() {
        return image;
    }
}
