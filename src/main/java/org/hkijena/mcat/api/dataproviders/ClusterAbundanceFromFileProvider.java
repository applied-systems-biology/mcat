package org.hkijena.mcat.api.dataproviders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.hkijena.mcat.api.MCATCentroidCluster;
import org.hkijena.mcat.api.datatypes.ClusterAbundanceData;
import org.hkijena.mcat.api.datatypes.ClusterCentersData;

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
    	List<MCATCentroidCluster<DoublePoint>> centroids = new ArrayList<MCATCentroidCluster<DoublePoint>>();
    	int[] abundance = null;

    	try {
    		String filePath = getFilePath().toString();
    		long lines = Files.lines(getFilePath()).count();
    		abundance = new int[Math.toIntExact(lines)];
        	
        	BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
        	String line = null;
        	
        	int counter = 0;
        	while((line = br.readLine()) != null) {
        		String[] splits = line.split(":");
        		abundance[counter++] = Integer.valueOf(splits[0]);
        		double[] arr = Stream.of(splits[1].split(","))
                        .mapToDouble (Double::parseDouble)
                        .toArray();
        		centroids.add(new MCATCentroidCluster<DoublePoint>(new DoublePoint(arr)));
        	}
        	
        	br.close();
        	
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return new ClusterAbundanceData(centroids, abundance);
    }

    @Override
    public String getName() {
        return "Cluster abundance (*.csv)";
    }

}
