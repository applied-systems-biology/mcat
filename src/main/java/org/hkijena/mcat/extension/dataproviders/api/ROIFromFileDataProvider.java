package org.hkijena.mcat.extension.dataproviders.api;

import ij.io.Opener;
import org.hkijena.mcat.extension.datatypes.ROIData;
import org.hkijena.mcat.api.MCATDocumentation;

/**
 * Loads a {@link ROIData} from a file
 */
@MCATDocumentation(name = "ROI (*.roi, *.zip)")
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
