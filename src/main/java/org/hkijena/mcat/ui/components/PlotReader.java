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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.hkijena.mcat.ui.MCATWorkbenchUI;
import org.hkijena.mcat.ui.MCATWorkbenchUIPanel;
import org.hkijena.mcat.utils.UIUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * Displays a plot
 */
public class PlotReader extends MCATWorkbenchUIPanel {

    private List<JFreeChart> charts = new ArrayList<>();
    private List<ChartPanel> chartPanels = new ArrayList<>();
    private FormPanel centerPanel;
    private JToolBar toolBar;

    /**
     * Creates a new instance
     *
     * @param workbenchUI the workbench
     */
    public PlotReader(MCATWorkbenchUI workbenchUI) {
        super(workbenchUI);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        toolBar = new JToolBar();

        JButton exportButton = new JButton("Export", UIUtils.getIconFromResources("save.png"));
        exportButton.addActionListener(e -> exportPlot());
        toolBar.add(exportButton);

        add(toolBar, BorderLayout.NORTH);

        centerPanel = new FormPanel(null, FormPanel.WITH_SCROLLING);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void exportPlot() {
        if (!charts.isEmpty()) {
            PlotExporterDialog dialog = new PlotExporterDialog(getWorkbenchUI().getContext(), charts);
            dialog.pack();
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
            dialog.setModal(true);
            dialog.setVisible(true);
        }
    }

    public JToolBar getToolBar() {
        return toolBar;
    }

    public void addChart(JFreeChart chart) {
        charts.add(chart);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanels.add(chartPanel);
        centerPanel.addWideToForm(chartPanel, null);
    }
}
