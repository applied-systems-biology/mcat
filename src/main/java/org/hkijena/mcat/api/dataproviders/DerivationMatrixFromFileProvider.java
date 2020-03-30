package org.hkijena.mcat.api.dataproviders;

import org.hkijena.mcat.api.datatypes.DerivativeMatrixData;

/**
 * Loads a {@link DerivativeMatrixData} from a file
 */
public class DerivationMatrixFromFileProvider extends FileDataProvider<DerivativeMatrixData> {

    public DerivationMatrixFromFileProvider() {
        super();
    }

    public DerivationMatrixFromFileProvider(FileDataProvider<?> other) {
        super(other);
    }

    @Override
    public DerivativeMatrixData get() {
        return null;
    }

    @Override
    public String getName() {
        return "Derivation matrix (*.csv)";
    }

}
