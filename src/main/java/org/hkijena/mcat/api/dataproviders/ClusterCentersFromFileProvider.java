package org.hkijena.mcat.api.dataproviders;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.MCATParameters;
import org.hkijena.mcat.api.MCATValidityReport;
import org.hkijena.mcat.api.datatypes.ClusterCentersData;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads a {@link ClusterCentersData} from a file
 */
public class ClusterCentersFromFileProvider extends FileDataProvider<ClusterCentersData> {

    @Override
    public ClusterCentersData get() {
        return null;
    }

    @Override
    public String getName() {
        return "Cluster centers (*.csv)";
    }

}
