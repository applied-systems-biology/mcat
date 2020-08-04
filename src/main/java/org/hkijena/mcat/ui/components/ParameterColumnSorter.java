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
