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
import org.hkijena.mcat.api.MCATDataInterfaceKey;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.parameters.MCATCustomParameterCollection;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@MCATDocumentation(name = "AUC")
public class AUCData implements MCATData {

    /**
     * Contains condition -> AUC
     */
    private Map<MCATDataInterfaceKey, Row> aucMap = new HashMap<>();

    @Override
    public void saveTo(Path folder, Path fileName) {
        ResultsTable table = new ResultsTable();
        int index = 0;
        for (Map.Entry<MCATDataInterfaceKey, Row> entry : aucMap.entrySet()) {
            table.incrementCounter();

            String dataSetString = String.join(";", entry.getKey().getDataSetNames());
            String parameterString = MCATCustomParameterCollection.parameterCollectionsToString(entry.getKey().getParameters(), ";", "=");
            table.setValue("datasets", index, dataSetString);
            table.setValue("parameters", index, parameterString);
            table.setValue("AUC", index, entry.getValue().auc);
            table.setValue("AUC cum", index, entry.getValue().aucCum);

            ++index;
        }

        try {
            table.saveAs(folder.resolve(fileName).toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<MCATDataInterfaceKey, Row> getAucMap() {
        return aucMap;
    }

    public void setAucMap(Map<MCATDataInterfaceKey, Row> aucMap) {
        this.aucMap = aucMap;
    }

    public static class Row {
        private double auc;
        private double aucCum;

        public Row(double auc, double aucCum) {
            this.auc = auc;
            this.aucCum = aucCum;
        }

        public double getAuc() {
            return auc;
        }

        public void setAuc(double auc) {
            this.auc = auc;
        }

        public double getAucCum() {
            return aucCum;
        }

        public void setAucCum(double aucCum) {
            this.aucCum = aucCum;
        }
    }
}
