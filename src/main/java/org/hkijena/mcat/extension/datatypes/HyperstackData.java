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
