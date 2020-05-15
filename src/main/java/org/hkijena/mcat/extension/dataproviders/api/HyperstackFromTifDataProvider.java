package org.hkijena.mcat.extension.dataproviders.api;

import ij.IJ;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.extension.datatypes.HyperstackData;

/**
 * Loads a {@link HyperstackData} from a file
 */
@MCATDocumentation(name = "Hyperstack (*.tif)")
public class HyperstackFromTifDataProvider extends FileDataProvider {

    public HyperstackFromTifDataProvider() {
        super();
    }

    public HyperstackFromTifDataProvider(HyperstackFromTifDataProvider other) {
        super(other);
    }

    @Override
    public HyperstackData get() {
        return new HyperstackData(IJ.openImage(getFilePath().toString()));
    }

}
