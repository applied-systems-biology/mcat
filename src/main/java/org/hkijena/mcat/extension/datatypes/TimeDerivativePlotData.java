package org.hkijena.mcat.extension.datatypes;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.type.TypeReference;
import ij.measure.ResultsTable;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.hkijena.mcat.api.MCATCentroidCluster;
import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.utils.JsonUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plots multiple {@link ClusterCentersData}
 */
public class TimeDerivativePlotData implements MCATData {

    public static final int AUTO_EXPORT_WIDTH = 500;
    public static final int AUTO_EXPORT_HEIGHT = 400;

    private Map<String, Series> dataSeries = new HashMap<>();

    public TimeDerivativePlotData() {
    }

    /**
     * Loads from an output folder
     *
     * @param folderName the output folder
     */
    public TimeDerivativePlotData(Path folderName) {
        TypeReference<Map<String, Series>> typeReference = new TypeReference<Map<String, Series>>() {
        };
        try {
            dataSeries = JsonUtils.getObjectMapper().readValue(folderName.resolve("series.json").toFile(), typeReference);
            for (Map.Entry<String, Series> entry : dataSeries.entrySet()) {
                entry.getValue().table = ResultsTable.open(folderName.resolve(entry.getKey()).toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

        writeChartAsPNG(folder.resolve(identifier + ".png"));
        writeChartAsSVG(folder.resolve(identifier + ".svg"));
    }

    public void writeChartAsPNG(Path fileName) {
        int w = dataSeries.size() * AUTO_EXPORT_WIDTH;
        int h = AUTO_EXPORT_HEIGHT;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();

        // Render plots
        renderToGraphics(graphics2D);

        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(fileName.toFile()))) {
            EncoderUtil.writeBufferedImage(image, ImageFormat.PNG, stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void renderToGraphics(Graphics2D graphics2D) {
        int x = 0;
        for (Series series : dataSeries.values()) {
            JFreeChart chart = series.renderChart();
            chart.draw(graphics2D, new Rectangle(x, 0, AUTO_EXPORT_WIDTH, AUTO_EXPORT_HEIGHT));
            x += AUTO_EXPORT_WIDTH;
        }
    }

    public void writeChartAsSVG(Path fileName) {
        int w = dataSeries.size() * AUTO_EXPORT_WIDTH;
        int h = AUTO_EXPORT_HEIGHT;
        SVGGraphics2D graphics2D = new SVGGraphics2D(w, h);

        // Render plots
        renderToGraphics(graphics2D);

        try {
            SVGUtils.writeToSVG(fileName.toFile(), graphics2D.getSVGElement());
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                if (rows == -1)
                    rows = dim;
                else if (rows != dim) {
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

        public JFreeChart renderChart() {
            XYSeriesCollection dataset = new XYSeriesCollection();

            int nSeries = table.getLastColumn() + 1;
            for (int col = 0; col < nSeries; col++) {
                XYSeries series = new XYSeries("C" + col);
                for (int row = 0; row < table.getCounter(); row++) {
                    series.add(row, table.getValueAsDouble(col, row));
                }
                dataset.addSeries(series);
            }

            return ChartFactory.createXYLineChart("Time derivative (" + group + ")",
                    "Time (downsampled)",
                    "Time derivative",
                    dataset);
        }
    }

}
