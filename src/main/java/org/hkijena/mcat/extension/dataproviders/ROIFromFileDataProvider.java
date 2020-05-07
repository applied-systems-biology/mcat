package org.hkijena.mcat.extension.dataproviders;

import ij.io.Opener;
import org.hkijena.mcat.extension.datatypes.ROIData;
import org.hkijena.mcat.utils.api.ACAQDocumentation;

/**
 * Loads a {@link ROIData} from a file
 */
@ACAQDocumentation(name = "ROI (*.roi, *.zip)")
public class ROIFromFileDataProvider extends FileDataProvider {

    public ROIFromFileDataProvider() {
        super();
    }

    public ROIFromFileDataProvider(ROIFromFileDataProvider other) {
        super(other);
    }

    @Override
    public ROIData get() {
        Opener opener = new Opener();
        return new ROIData(opener.openRoi(getFilePath().toString()));
    }
}
