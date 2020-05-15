package org.hkijena.mcat.extension.resultanalysis;

import org.hkijena.mcat.api.MCATResultDataInterfaces;
import org.hkijena.mcat.extension.datatypes.AUCPlotData;
import org.hkijena.mcat.ui.MCATWorkbenchUI;
import org.hkijena.mcat.ui.components.DocumentTabPane;
import org.hkijena.mcat.ui.components.PlotReader;
import org.hkijena.mcat.utils.UIUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class AUCPlotDataSlotResultUI extends MCATDefaultDataSlotResultUI {
    public AUCPlotDataSlotResultUI(MCATWorkbenchUI workbenchUI, Path outputPath, MCATResultDataInterfaces.SlotEntry slot) {
        super(workbenchUI, outputPath, slot);
    }

    @Override
    protected void registerActions() {
        super.registerActions();
        if (Files.isRegularFile(getStoragePath().resolve("plot-data.csv"))) {
            registerAction("Open plot", "Opens the plot", UIUtils.getIconFromResources("bar-chart.png"), ui -> {
                AUCPlotData plotData = new AUCPlotData(getStoragePath());
                PlotReader reader = new PlotReader(getWorkbenchUI());
                reader.addChart(plotData.renderChartPerTreatment());
                reader.addChart(plotData.renderChartPerSubject());

                getWorkbenchUI().getDocumentTabPane().addTab("AUC plot",
                        UIUtils.getIconFromResources("bar-chart.png"),
                        reader,
                        DocumentTabPane.CloseMode.withSilentCloseButton,
                        true);
                getWorkbenchUI().getDocumentTabPane().switchToLastTab();
            });
        }
    }
}
