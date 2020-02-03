package org.hkijena.mcat.api.dataproviders;

import ij.io.Opener;
import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.datatypes.ROIData;

import java.nio.file.Path;

public class ROIFromFileDataProvider implements MCATDataProvider<ROIData> {

    private Path filePath;

    public ROIFromFileDataProvider(Path filePath) {
        this.filePath = filePath;
    }

    public ROIFromFileDataProvider() {

    }

    @Override
    public ROIData get() {
        Opener opener = new Opener();
        return new ROIData(opener.openRoi(filePath.toString()));
    }

    @Override
    public String getName() {
        return "ROI (*.roi)";
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
}
