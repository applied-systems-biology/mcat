package org.hkijena.mcat.api.dataproviders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.hkijena.mcat.api.MCATCentroidCluster;
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
    	List<MCATCentroidCluster<DoublePoint>> centroids = new ArrayList<MCATCentroidCluster<DoublePoint>>();

    	try {
    		String filePath = getFilePath().toString();
        	
        	BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
        	String line = null;
        	
        	while((line = br.readLine()) != null) {
        		double[] arr = Stream.of(line.split(","))
                        .mapToDouble (Double::parseDouble)
                        .toArray();
        		centroids.add(new MCATCentroidCluster<DoublePoint>(new DoublePoint(arr)));
        	}
        	
        	br.close();
        	
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return new ClusterCentersData(centroids);
    }

    @Override
    public String getName() {
        return "Cluster centers (*.csv)";
    }

}
