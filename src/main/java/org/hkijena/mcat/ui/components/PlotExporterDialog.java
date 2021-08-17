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
package org.hkijena.mcat.ui.components;

import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.parameters.MCATParameter;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.hkijena.mcat.api.parameters.MCATTraversedParameterCollection;
import org.hkijena.mcat.extension.parameters.editors.FilePathParameterSettings;
import org.hkijena.mcat.ui.parameters.ParameterPanel;
import org.hkijena.mcat.utils.UIUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.scijava.Context;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Dialog that exports plots as image
 */
public class PlotExporterDialog extends JDialog {

    private Context context;
    private List<JFreeChart> charts;
    private Settings settings = new Settings();

    /**
     * @param context the scijava context
     * @param charts  the plots
     */
    public PlotExporterDialog(Context context, List<JFreeChart> charts) {
        this.context = context;
        this.charts = charts;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setTitle("Export plot");

        ParameterPanel parameterPanel = new ParameterPanel(context,
                new MCATTraversedParameterCollection(settings),
                null,
                ParameterPanel.NONE);
        add(parameterPanel, BorderLayout.CENTER);

        // Add buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());

        JButton cancelButton = new JButton("Cancel", UIUtils.getIconFromResources("remove.png"));
        cancelButton.addActionListener(e -> setVisible(false));
        buttonPanel.add(cancelButton);

        JButton exportButton = new JButton("Export", UIUtils.getIconFromResources("save.png"));
        exportButton.setDefaultCapable(true);
        exportButton.addActionListener(e -> exportPlot());
        buttonPanel.add(exportButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void writeChartAsPNG(Path fileName) {
        int w = charts.size() * settings.getWidth();
        int h = settings.getHeight();
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

    public void writeChartAsJPEG(Path fileName) {
        int w = charts.size() * settings.getWidth();
        int h = settings.getHeight();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();

        // Render plots
        renderToGraphics(graphics2D);

        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(fileName.toFile()))) {
            EncoderUtil.writeBufferedImage(image, ImageFormat.JPEG, stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void renderToGraphics(Graphics2D graphics2D) {
        int x = 0;
        for (JFreeChart chart : charts) {
            chart.draw(graphics2D, new Rectangle(x, 0, settings.getWidth(), settings.getHeight()));
            x += settings.getWidth();
        }
    }

    public void writeChartAsSVG(Path fileName) {
        int w = charts.size() * settings.getWidth();
        int h = settings.getHeight();
        SVGGraphics2D graphics2D = new SVGGraphics2D(w, h);

        // Render plots
        renderToGraphics(graphics2D);

        try {
            SVGUtils.writeToSVG(fileName.toFile(), graphics2D.getSVGElement());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void exportPlot() {
        if (settings.getExportPath() == null)
            return;
        switch (settings.getFileFormat()) {
            case PNG:
                writeChartAsPNG(settings.getExportPath());
                break;
            case JPEG:
                writeChartAsJPEG(settings.getExportPath());
                break;
            case SVG: {
                writeChartAsSVG(settings.getExportPath());
            }
            break;
        }

        setVisible(false);
    }

    /**
     * Available file formats
     */
    public enum FileFormat {
        PNG,
        JPEG,
        SVG;

        @Override
        public String toString() {
            switch (this) {
                case PNG:
                    return "PNG (*.png)";
                case JPEG:
                    return "JPEG (*.jpeg)";
                case SVG:
                    return "SVG (*.svg)";
                default:
                    throw new UnsupportedOperationException();
            }
        }

        public Icon toIcon() {
            return UIUtils.getIconFromResources("filetype-image.png");
        }
    }

    /**
     * Settings for exporting
     */
    public static class Settings implements MCATParameterCollection {
        private EventBus eventBus = new EventBus();
        private Path exportPath;
        private int width = 800;
        private int height = 600;
        private FileFormat fileFormat = FileFormat.PNG;

        @FilePathParameterSettings(pathMode = FileSelection.PathMode.FilesOnly, ioMode = FileSelection.IOMode.Save)
        @MCATDocumentation(name = "Export path")
        @MCATParameter(value = "export-path", uiOrder = 1)
        public Path getExportPath() {
            return exportPath;
        }

        @MCATParameter("export-path")
        public void setExportPath(Path exportPath) {
            this.exportPath = exportPath;
        }

        @MCATDocumentation(name = "Width (per plot)")
        @MCATParameter(value = "width", uiOrder = 3)
        public int getWidth() {
            return width;
        }

        @MCATParameter("width")
        public void setWidth(int width) {
            this.width = width;
        }

        @MCATDocumentation(name = "Height (per plot)")
        @MCATParameter(value = "height", uiOrder = 4)
        public int getHeight() {
            return height;
        }

        @MCATParameter("height")
        public void setHeight(int height) {
            this.height = height;
        }

        @MCATDocumentation(name = "File format")
        @MCATParameter(value = "format", uiOrder = 2)
        public FileFormat getFileFormat() {
            return fileFormat;
        }

        @MCATParameter("format")
        public void setFileFormat(FileFormat fileFormat) {
            this.fileFormat = fileFormat;
        }

        @Override
        public EventBus getEventBus() {
            return eventBus;
        }
    }
}
