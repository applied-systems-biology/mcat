package org.hkijena.mcat.api.dataproviders;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATParameters;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datatypes.DerivationMatrixData;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads a {@link DerivationMatrixData} from a file
 */
public class DerivationMatrixFromFileProvider extends FileDataProvider<DerivationMatrixData> {

    @Override
    public DerivationMatrixData get() {
        return null;
    }

    @Override
    public String getName() {
        return "Derivation matrix (*.csv)";
    }

}
