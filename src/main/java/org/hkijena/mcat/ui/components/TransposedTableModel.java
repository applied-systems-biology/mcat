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
import org.hkijena.mcat.api.events.ParameterChangedEvent;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class TransposedTableModel implements TableModel, TableModelListener {

    private TableModel wrappedModel;
    private EventBus eventBus = new EventBus();

    public TransposedTableModel(TableModel wrappedModel) {
        this.wrappedModel = wrappedModel;
        this.wrappedModel.addTableModelListener(this);
    }

    @Override
    public int getRowCount() {
        return wrappedModel.getColumnCount();
    }

    @Override
    public int getColumnCount() {
        return wrappedModel.getRowCount() + 1;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0)
            return "Parameter types";
        else
            return "Parameters " + columnIndex;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return wrappedModel.isCellEditable(columnIndex - 1, rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex > 0)
            return wrappedModel.getValueAt(columnIndex - 1, rowIndex);
        else
            return wrappedModel.getColumnName(rowIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0)
            return;
        wrappedModel.setValueAt(aValue, columnIndex - 1, rowIndex);
    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }

    public TableModel getWrappedModel() {
        return wrappedModel;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        eventBus.post(new ParameterChangedEvent(this, "table-data"));
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
