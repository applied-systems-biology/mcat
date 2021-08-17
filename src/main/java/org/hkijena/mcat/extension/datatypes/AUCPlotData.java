/*******************************************************************************
 * Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge
 *
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 ******************************************************************************/
package org.hkijena.mcat.extension.datatypes;

import ij.measure.ResultsTable;
import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.hkijena.mcat.api.parameters.MCATTraversedParameterCollection;
import org.hkijena.mcat.extension.plots.CustomBoxAndWhiskerRenderer;
import org.hkijena.mcat.utils.JsonUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains plots for {@link AUCData}
 */
@MCATDocumentation(name = "AUC Plots")
public class AUCPlotData implements MCATData {

    public static final int AUTO_EXPORT_HEIGHT = 400;

    private Map<String, Object> parameterValues = new HashMap<>();
    private ResultsTable table = new ResultsTable();

    /**
     * @param parameters the parameters the AUC was constructed from. The parameters will be added as columns
     */
    public AUCPlotData(Collection<MCATParameterCollection> parameters) {
        MCATTraversedParameterCollection collection = new MCATTraversedParameterCollection(parameters.toArray(new MCATParameterCollection[0]));
        for (Map.Entry<String, MCATParameterAccess> entry : collection.getParameters().entrySet()) {
            parameterValues.put(entry.getKey(), entry.getValue().get());
        }
    }

    public void addRow(String subject, String treatment, double auc, double cumAUC) {
        table.incrementCounter();
        int row = table.getCounter() - 1;

        table.setValue("subject", row, subject);
        table.setValue("treatment", row, treatment);
        table.setValue("AUC", row, auc);
        table.setValue("cumAUC", row, cumAUC);
        for (Map.Entry<String, Object> entry : parameterValues.entrySet()) {
            table.setValue("p:" + entry.getKey(), row, "" + entry.getValue());
        }
    }

    public JFreeChart renderChartPerTreatment() {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        Map<String, List<Double>> groupedBy = new HashMap<>();
        for (int row = 0; row < table.getCounter(); row++) {
            String category = table.getStringValue("treatment", row);
            List<Double> dataList = groupedBy.getOrDefault(category, null);
            if (dataList == null) {
                dataList = new ArrayList<>();
                groupedBy.put(category, dataList);
            }
            dataList.add(table.getValue("AUC", row));
        }
        for (Map.Entry<String, List<Double>> entry : groupedBy.entrySet()) {
            dataset.add(entry.getValue(), "AUC", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart("AUC plot",
                "Treatment",
                "AUC",
                dataset,
                true);
        CustomBoxAndWhiskerRenderer renderer = new CustomBoxAndWhiskerRenderer();
        chart.getCategoryPlot().setRenderer(renderer);
        return chart;
    }

    public JFreeChart renderChartPerSubject() {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        Map<String, List<Double>> groupedBy = new HashMap<>();
        for (int row = 0; row < table.getCounter(); row++) {
            String category = table.getStringValue("subject", row);
            List<Double> dataList = groupedBy.getOrDefault(category, null);
            if (dataList == null) {
                dataList = new ArrayList<>();
                groupedBy.put(category, dataList);
            }
            dataList.add(table.getValue("AUC", row));
        }
        for (Map.Entry<String, List<Double>> entry : groupedBy.entrySet()) {
            dataset.add(entry.getValue(), "AUC", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart("AUC plot",
                "Subject",
                "AUC",
                dataset,
                true);
        CustomBoxAndWhiskerRenderer renderer = new CustomBoxAndWhiskerRenderer();
        chart.getCategoryPlot().setRenderer(renderer);
        return chart;
    }

    private int getAutoChartWidth(JFreeChart chart) {
        return Math.max(128, 128 * chart.getCategoryPlot().getDataset().getColumnCount());
    }

    public void autoSaveChartToPNG(JFreeChart chart, Path fileName) {
        try {
            ChartUtils.saveChartAsPNG(fileName.toFile(),
                    chart,
                    getAutoChartWidth(chart),
                    400);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void autoSaveChartToSVG(JFreeChart chart, Path fileName) {
        SVGGraphics2D g2 = new SVGGraphics2D(getAutoChartWidth(chart), AUTO_EXPORT_HEIGHT);
        Rectangle r = new Rectangle(0, 0, getAutoChartWidth(chart), AUTO_EXPORT_HEIGHT);
        chart.draw(g2, r);
        try {
            SVGUtils.writeToSVG(fileName.toFile(), g2.getSVGElement());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveTo(Path folder, Path fileName) {
        table.save(folder.resolve(fileName + "Data.csv").toString());

        try {
            JsonUtils.getObjectMapper().writeValue(folder.resolve(fileName + "Data.json").toFile(), parameterValues);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JFreeChart perTreatmentRender = renderChartPerTreatment();
        autoSaveChartToPNG(perTreatmentRender, folder.resolve(fileName + "_perTreatment.png"));
        autoSaveChartToSVG(perTreatmentRender, folder.resolve(fileName + "_perTreatment.svg"));

        JFreeChart perSubjectRender = renderChartPerSubject();
        autoSaveChartToPNG(perSubjectRender, folder.resolve(fileName + "_perSubject.png"));
        autoSaveChartToSVG(perSubjectRender, folder.resolve(fileName + "_perSubject.svg"));
    }
}
