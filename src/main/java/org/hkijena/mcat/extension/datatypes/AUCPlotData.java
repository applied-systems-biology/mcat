package org.hkijena.mcat.extension.datatypes;

import ij.measure.ResultsTable;
import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.parameters.MCATParameterAccess;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.hkijena.mcat.api.parameters.MCATTraversedParameterCollection;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Contains plots for {@link AUCData}
 */
@MCATDocumentation(name = "AUC Plots")
public class AUCPlotData implements MCATData {
    private Map<String, String> parameterValues = new HashMap<>();
    private ResultsTable table = new ResultsTable();

    /**
     * @param parameters the parameters the AUC was constructed from. The parameters will be added as columns
     */
    public AUCPlotData(Collection<MCATParameterCollection> parameters) {
        MCATTraversedParameterCollection collection = new MCATTraversedParameterCollection(parameters.toArray(new MCATParameterCollection[0]));
        for (Map.Entry<String, MCATParameterAccess> entry : collection.getParameters().entrySet()) {
            parameterValues.put(entry.getKey(), "" + entry.getValue().get());
        }
    }

    public void addRow(String subject, String treatment, double auc, double cumAUC) {
        table.incrementCounter();
        int row = table.getCounter() - 1;

        table.setValue("subject", row, subject);
        table.setValue("treatment", row, treatment);
        table.setValue("AUC", row, auc);
        table.setValue("cumAUC", row, cumAUC);
        for (Map.Entry<String, String> entry : parameterValues.entrySet()) {
            table.setValue("p:" + entry.getKey(), row, entry.getValue());
        }
    }

    @Override
    public void saveTo(Path folder, String name, String identifier) {
        table.save(folder.resolve("plot-data.csv").toString());
    }
}
