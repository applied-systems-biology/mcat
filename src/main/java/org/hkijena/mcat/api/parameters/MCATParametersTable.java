package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.utils.api.events.ParameterChangedEvent;
import org.hkijena.mcat.utils.api.parameters.ACAQTraversedParameterCollection;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MCATParametersTable implements TableModel {

    private List<MCATParametersTableRow> rows = new ArrayList<>();
    private List<ACAQTraversedParameterCollection> rowParameterAccessors = new ArrayList<>();
    private List<TableModelListener> listeners = new ArrayList<>();
    private List<String> columnKeys;
    private List<Class<?>> columnClasses = new ArrayList<>();
    private List<String> columnNames = new ArrayList<>();

    public MCATParametersTable() {
        initializeColumns();
    }

    public MCATParametersTable(MCATParametersTable other) {
        initializeColumns();
        for (MCATParametersTableRow row : other.rows) {
           addRow(new MCATParametersTableRow(row));
        }
    }

    @JsonGetter("rows")
    public List<MCATParametersTableRow> getRows() {
        return rows;
    }

    @JsonSetter("rows")
    public void setRows(List<MCATParametersTableRow> rows) {
        this.rows = rows;
        this.rowParameterAccessors.clear();
        for (MCATParametersTableRow row : rows) {
            rowParameterAccessors.add(new ACAQTraversedParameterCollection(row));
        }
        postChangedEvent();
    }

    /**
     * Adds an empty row
     */
    public void addRow() {
        addRow(new MCATParametersTableRow());
    }

    /**
     * Adds an existing row.
     * The row is not copied, so create a copy if its from another table.
     * @param row the row
     */
    public void addRow(MCATParametersTableRow row) {
        rows.add(row);
        rowParameterAccessors.add(new ACAQTraversedParameterCollection(row));
        row.getEventBus().register(this);
    }

    /**
     * Removes an existing row. Silently fails of row does not exist.
     * @param row the row
     */
    public void removeRow(MCATParametersTableRow row) {
        int index = rows.indexOf(row);
        if(index < 0)
            return;
        rows.remove(index);
        rowParameterAccessors.remove(index);
        row.getEventBus().unregister(this);
        postChangedEvent();
    }

    private void initializeColumns() {
        MCATParametersTableRow row = new MCATParametersTableRow();
        ACAQTraversedParameterCollection parameterCollection = new ACAQTraversedParameterCollection(row);
        columnKeys = parameterCollection.getParameters().keySet().stream().sorted().collect(Collectors.toList());
        for (String key : columnKeys) {
            columnClasses.add(parameterCollection.getParameters().get(key).getFieldClass());
            columnNames.add(key);
        }
    }

    @Subscribe
    public void onParameterChanged(ParameterChangedEvent event) {
        postChangedEvent();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columnKeys.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames.get(columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return  columnClasses.get(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String key = columnKeys.get(columnIndex);
        return rowParameterAccessors.get(rowIndex).getParameters().get(key).get();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    private void postChangedEvent() {
        for (TableModelListener listener : listeners) {
            listener.tableChanged(new TableModelEvent(this));
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }
}
