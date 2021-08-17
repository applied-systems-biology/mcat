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

import org.hkijena.mcat.api.parameters.MCATParametersTable;

public class ParameterColumnSorter implements java.util.Comparator<Object> {
    private final MCATParametersTable parametersTable;

    public ParameterColumnSorter(MCATParametersTable parametersTable) {
        this.parametersTable = parametersTable;
    }

    @Override
    public int compare(Object o1, Object o2) {
        String s1 = "" + o1;
        String s2 = "" + o2;
        return Integer.compare(parametersTable.getColumnNames().indexOf(s1), parametersTable.getColumnNames().indexOf(s2));
    }
}
