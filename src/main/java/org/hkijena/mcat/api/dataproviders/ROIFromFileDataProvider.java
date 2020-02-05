package org.hkijena.mcat.api.dataproviders;

import ij.io.Opener;
import org.hkijena.mcat.api.datatypes.ROIData;

/**
 * Loads a {@link ROIData} from a file
 */
public class ROIFromFileDataProvider extends FileDataProvider<ROIData> {

    public ROIFromFileDataProvider() {
        super();
    }

    public ROIFromFileDataProvider(FileDataProvider<?> other) {
        super(other);
    }

    @Override
    public ROIData get() {
        Opener opener = new Opener();
        return new ROIData(opener.openRoi(getFilePath().toString()));
    }

    @Override
    public String getName() {
        return "ROI (*.roi)";
    }
}
