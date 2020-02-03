package org.hkijena.mcat.api.dataproviders;

import org.hkijena.mcat.api.MCATDataProvider;
import org.hkijena.mcat.api.datatypes.ClusterCentersData;

import java.nio.file.Path;

/**
 * Loads a {@link ClusterCentersData} from a file
 */
public class ClusterCentersFromFileProvider implements MCATDataProvider<ClusterCentersData> {

    private Path filePath;

    @Override
    public ClusterCentersData get() {
        return null;
    }

    @Override
    public String getName() {
        return "Cluster centers (*.csv)";
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
}
