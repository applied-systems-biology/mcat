package org.hkijena.mcat.api.dataproviders;

import ij.IJ;
import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.datatypes.HyperstackData;

import java.nio.file.Path;

public class HyperstackFromTifDataProvider implements MCATDataProvider<HyperstackData> {

    private Path filePath;

    public HyperstackFromTifDataProvider(Path filePath) {
        this.filePath = filePath;
    }

    public HyperstackFromTifDataProvider() {

    }

    @Override
    public HyperstackData get() {
        return new HyperstackData(IJ.openImage(filePath.toString()));
    }

    @Override
    public String getName() {
        return "TIFF file";
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
}
