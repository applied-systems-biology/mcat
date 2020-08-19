/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/
package org.hkijena.mcat.extension.dataproviders.api;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.hkijena.mcat.api.MCATCentroidCluster;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.extension.datatypes.ClusterCentersData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Loads a {@link ClusterCentersData} from a file
 */
@MCATDocumentation(name = "Cluster centers (*.csv)")
public class ClusterCentersFromFileProvider extends FileDataProvider {

    public ClusterCentersFromFileProvider() {
        super();
    }

    public ClusterCentersFromFileProvider(ClusterCentersFromFileProvider other) {
        super(other);
    }

    @Override
    public ClusterCentersData get() {
        List<MCATCentroidCluster<DoublePoint>> centroids = new ArrayList<MCATCentroidCluster<DoublePoint>>();

        try {
            String filePath = getFilePath().toString();

            BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
            String line = null;

            while ((line = br.readLine()) != null) {
                double[] arr = Stream.of(line.split(","))
                        .mapToDouble(Double::parseDouble)
                        .toArray();
                centroids.add(new MCATCentroidCluster<DoublePoint>(new DoublePoint(arr)));
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ClusterCentersData(centroids);
    }

}
