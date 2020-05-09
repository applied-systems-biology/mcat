package org.hkijena.mcat.api.parameters;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.utils.StringUtils;
import org.hkijena.mcat.api.events.ParameterChangedEvent;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MCATParametersTable implements TableModel {

    private List<MCATParametersTableRow> rows = new ArrayList<>();
    private List<MCATTraversedParameterCollection> rowParameterAccessors = new ArrayList<>();
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
            rowParameterAccessors.add(new MCATTraversedParameterCollection(row));
        }

        // Post full change event
        for (TableModelListener listener : listeners) {
            listener.tableChanged(new TableModelEvent(this));
        }
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
     *
     * @param row the row
     */
    public void addRow(MCATParametersTableRow row) {
        rows.add(row);
        rowParameterAccessors.add(new MCATTraversedParameterCollection(row));
        row.getEventBus().register(this);
        for (TableModelListener listener : listeners) {
            listener.tableChanged(new TableModelEvent(this));
        }
    }

    /**
     * Removes an existing row. Silently fails of row does not exist.
     *
     * @param row the row
     */
    public void removeRow(MCATParametersTableRow row) {
        int index = rows.indexOf(row);
        if (index < 0)
            return;
        removeRowAt(index);
    }

    private void initializeColumns() {
        MCATParametersTableRow row = new MCATParametersTableRow();
        MCATTraversedParameterCollection parameterCollection = new MCATTraversedParameterCollection(row);
        columnKeys = parameterCollection.getParameters().keySet().stream().sorted().collect(Collectors.toList());
        for (String key : columnKeys) {
            columnClasses.add(parameterCollection.getParameters().get(key).getFieldClass());
            int slashIndex = key.indexOf('/');
            String rootName = StringUtils.capitalizeFirstLetter(key.substring(0, slashIndex));
            String parameterName = "<html>" + rootName + "<br/><strong>" + parameterCollection.getParameters().get(key).getName() + "</strong></html>";
            columnNames.add(parameterName);
        }
    }

    @Subscribe
    public void onParameterChanged(ParameterChangedEvent event) {
        for (int i = 0; i < rows.size(); i++) {
            if (rowParameterAccessors.get(i).getParameters().values().stream().anyMatch(a -> a.getSource() == event.getSource())) {
                for (TableModelListener listener : listeners) {
                    listener.tableChanged(new TableModelEvent(this, i));
                }
                return;
            }
        }
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

    public String getColumnKey(int columnIndex) {
        return columnKeys.get(columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses.get(columnIndex);
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
        String key = columnKeys.get(columnIndex);
        rowParameterAccessors.get(rowIndex).getParameters().get(key).set(aValue);
        for (TableModelListener listener : listeners) {
            listener.tableChanged(new TableModelEvent(this, rowIndex));
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

    public void removeRowAt(int index) {
        MCATParametersTableRow row = rows.get(index);
        rows.remove(index);
        rowParameterAccessors.remove(index);
        row.getEventBus().unregister(this);

        // Post change event
        for (TableModelListener listener : listeners) {
            listener.tableChanged(new TableModelEvent(this, index));
        }
    }
}
