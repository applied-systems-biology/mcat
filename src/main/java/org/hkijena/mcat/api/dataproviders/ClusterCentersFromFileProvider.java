package org.hkijena.mcat.api.dataproviders;

import org.hkijena.mcat.api.datatypes.ClusterCentersData;

/**
 * Loads a {@link ClusterCentersData} from a file
 */
public class ClusterCentersFromFileProvider extends FileDataProvider<ClusterCentersData> {

    public ClusterCentersFromFileProvider() {
        super();
    }

    public ClusterCentersFromFileProvider(FileDataProvider<?> other) {
        super(other);
    }

    @Override
    public ClusterCentersData get() {
    	//TODO
    	return null;
    }

    @Override
    public String getName() {
        return "Cluster centers (*.csv)";
    }

}
