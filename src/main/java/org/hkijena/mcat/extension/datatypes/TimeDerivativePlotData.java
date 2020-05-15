package org.hkijena.mcat.extension.datatypes;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import ij.measure.ResultsTable;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.hkijena.mcat.api.MCATCentroidCluster;
import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.utils.JsonUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plots multiple {@link ClusterCentersData}
 */
public class TimeDerivativePlotData implements MCATData {

    private Map<String, Series> dataSeries = new HashMap<>();

    @Override
    public void saveTo(Path folder, String name, String identifier) {
        try {
            JsonUtils.getObjectMapper().writeValue(folder.resolve("series.json").toFile(), dataSeries);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Map.Entry<String, Series> entry : dataSeries.entrySet()) {
            entry.getValue().getTable().save(folder.resolve(entry.getKey()).toString());
        }
    }

    public Map<String, Series> getDataSeries() {
        return dataSeries;
    }

    public void setDataSeries(Map<String, Series> dataSeries) {
        this.dataSeries = dataSeries;
    }

    public static class Series {
        private String group;
        private ResultsTable table = new ResultsTable();

        @JsonGetter("group")
        public String getGroup() {
            return group;
        }

        @JsonSetter("group")
        public void setGroup(String group) {
            this.group = group;
        }

        public ResultsTable getTable() {
            return table;
        }

        public void setTable(ResultsTable table) {
            this.table = table;
        }

        public void setData(List<MCATCentroidCluster<DoublePoint>> centroids) {
            int rows = -1;
            for (MCATCentroidCluster<DoublePoint> centroid : centroids) {
                int dim = centroid.getCenter().getPoint().length;
                if(rows == -1)
                    rows = dim;
                else if(rows != dim) {
                    throw new UnsupportedOperationException("Clusters returned different time dimensions");
                }
            }
            table = new ResultsTable(rows);

            for (int i = 0; i < centroids.size(); i++) {
                int col = table.getFreeColumn("C" + i);
                double[] data = centroids.get(i).getCenter().getPoint();
                for (int row = 0; row < rows; row++) {
                    table.setValue(col, row, data[row]);
                }
            }

        }
    }

}
