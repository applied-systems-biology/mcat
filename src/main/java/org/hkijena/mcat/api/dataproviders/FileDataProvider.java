package org.hkijena.mcat.api.dataproviders;

import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATValidityReport;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class FileDataProvider <T extends MCATData> extends MCATParameters implements MCATDataProvider<T>  {
    private Path filePath;

    public FileDataProvider() {

    }

    public FileDataProvider(FileDataProvider<?> other) {
        this.filePath = other.filePath;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
        postChangedEvent("filePath");
    }

    @Override
    public boolean providesData() {
        return filePath != null && Files.exists(filePath);
    }

    @Override
    public MCATValidityReport getValidityReport() {
        if(Files.exists(filePath))
            return new MCATValidityReport(this, getName(), true, "");
        else
            return new MCATValidityReport(this, getName(), false, "File " + filePath + " does not exist!");
    }
}
