package org.hkijena.mcat.api.dataproviders;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.datatypes.DerivationMatrixData;

import java.nio.file.Path;

public class DerivationMatrixFromFileProvider implements MCATDataProvider<DerivationMatrixData> {

    private Path filePath;

    @Override
    public DerivationMatrixData get() {
        return null;
    }

    @Override
    public String getName() {
        return "Derivation matrix (*.csv)";
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
}
