package org.hkijena.mcat.api.dataproviders;

import org.hkijena.mcat.api.datatypes.DerivationMatrixData;

/**
 * Loads a {@link DerivationMatrixData} from a file
 */
public class DerivationMatrixFromFileProvider extends FileDataProvider<DerivationMatrixData> {

    public DerivationMatrixFromFileProvider() {
        super();
    }

    public DerivationMatrixFromFileProvider(FileDataProvider<?> other) {
        super(other);
    }

    @Override
    public DerivationMatrixData get() {
        return null;
    }

    @Override
    public String getName() {
        return "Derivation matrix (*.csv)";
    }

}
