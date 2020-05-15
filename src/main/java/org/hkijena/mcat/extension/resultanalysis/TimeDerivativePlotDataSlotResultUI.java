package org.hkijena.mcat.extension.resultanalysis;

import org.hkijena.mcat.api.MCATResultDataInterfaces;
import org.hkijena.mcat.extension.datatypes.TimeDerivativePlotData;
import org.hkijena.mcat.ui.MCATWorkbenchUI;
import org.hkijena.mcat.ui.components.DocumentTabPane;
import org.hkijena.mcat.ui.components.PlotReader;
import org.hkijena.mcat.utils.UIUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class TimeDerivativePlotDataSlotResultUI extends MCATDefaultDataSlotResultUI {
    public TimeDerivativePlotDataSlotResultUI(MCATWorkbenchUI workbenchUI, Path outputPath, MCATResultDataInterfaces.SlotEntry slot) {
        super(workbenchUI, outputPath, slot);
    }

    @Override
    protected void registerActions() {
        super.registerActions();
        if (Files.isRegularFile(getStoragePath().resolve("series.json"))) {
            registerAction("Open plot", "Opens the plot", UIUtils.getIconFromResources("line-chart.png"), ui -> {
                TimeDerivativePlotData plotData = new TimeDerivativePlotData(getStoragePath());
                PlotReader reader = new PlotReader(getWorkbenchUI());
                for (TimeDerivativePlotData.Series series : plotData.getDataSeries().values()) {
                    reader.addChart(series.renderChart());
                }

                getWorkbenchUI().getDocumentTabPane().addTab("Time derivative plot",
                        UIUtils.getIconFromResources("line-chart.png"),
                        reader,
                        DocumentTabPane.CloseMode.withSilentCloseButton,
                        true);
                getWorkbenchUI().getDocumentTabPane().switchToLastTab();
            });
        }
    }
}
