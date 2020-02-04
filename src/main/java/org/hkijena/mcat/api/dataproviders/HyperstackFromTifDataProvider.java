package org.hkijena.mcat.api.dataproviders;

import ij.IJ;
import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATParameters;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datatypes.HyperstackData;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads a {@link HyperstackData} from a file
 */
public class HyperstackFromTifDataProvider extends FileDataProvider<HyperstackData> {

    public HyperstackFromTifDataProvider(Path filePath) {
        setFilePath(filePath);
    }

    public HyperstackFromTifDataProvider() {

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
