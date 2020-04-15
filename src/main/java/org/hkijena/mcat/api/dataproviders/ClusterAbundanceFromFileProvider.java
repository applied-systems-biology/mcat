package org.hkijena.mcat.api.dataproviders;

import org.hkijena.mcat.api.datatypes.ClusterAbundanceData;

/**
 * Loads a {@link ClusterAbundanceData} from a file
 */
public class ClusterAbundanceFromFileProvider extends FileDataProvider<ClusterAbundanceData> {

    public ClusterAbundanceFromFileProvider() {
        super();
    }

    public ClusterAbundanceFromFileProvider(FileDataProvider<?> other) {
        super(other);
    }

    @Override
    public ClusterAbundanceData get() {
    	//TODO
        return null;
    }

    @Override
    public String getName() {
        return "Cluster abundance (*.csv)";
    }

}
