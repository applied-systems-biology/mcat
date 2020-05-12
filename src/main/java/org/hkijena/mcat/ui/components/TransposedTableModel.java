package org.hkijena.mcat.ui.components;

import com.google.common.eventbus.EventBus;
import org.hkijena.mcat.api.events.ParameterChangedEvent;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

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
        if(columnIndex == 0)
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
        if(columnIndex > 0)
            return wrappedModel.getValueAt(columnIndex - 1, rowIndex);
        else
            return wrappedModel.getColumnName(rowIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(columnIndex == 0)
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
