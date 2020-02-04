package org.hkijena.mcat.api.dataproviders;

import ij.io.Opener;
import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATParameters;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datatypes.ROIData;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads a {@link ROIData} from a file
 */
public class ROIFromFileDataProvider extends FileDataProvider<ROIData> {

    public ROIFromFileDataProvider(Path filePath) {
        setFilePath(filePath);
    }

    public ROIFromFileDataProvider() {

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
