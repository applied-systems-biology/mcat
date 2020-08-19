/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/
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
