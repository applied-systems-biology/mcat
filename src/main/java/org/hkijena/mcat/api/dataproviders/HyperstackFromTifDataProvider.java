package org.hkijena.mcat.api.dataproviders;

import ij.IJ;
import org.hkijena.mcat.api.datatypes.HyperstackData;

/**
 * Loads a {@link HyperstackData} from a file
 */
public class HyperstackFromTifDataProvider extends FileDataProvider<HyperstackData> {

    public HyperstackFromTifDataProvider() {
        super();
    }

    public HyperstackFromTifDataProvider(FileDataProvider<?> other) {
        super(other);
    }

    @Override
    public HyperstackData get() {
        return new HyperstackData(IJ.openImage(getFilePath().toString()));
    }

    @Override
    public String getName() {
        return "Hyperstack (*.tif)";
    }

}
