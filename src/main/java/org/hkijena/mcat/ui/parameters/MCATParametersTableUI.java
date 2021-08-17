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
package org.hkijena.mcat.ui.parameters;

import com.google.common.eventbus.Subscribe;
import org.hkijena.mcat.api.MCATDocumentation;
import org.hkijena.mcat.api.events.ParameterChangedEvent;
import org.hkijena.mcat.api.parameters.MCATParameterCollection;
import org.hkijena.mcat.api.parameters.MCATParametersTable;
import org.hkijena.mcat.api.parameters.MCATParametersTableRow;
import org.hkijena.mcat.api.registries.MCATUIParametertypeRegistry;
import org.hkijena.mcat.ui.MCATWorkbenchUI;
import org.hkijena.mcat.ui.MCATWorkbenchUIPanel;
import org.hkijena.mcat.ui.components.MarkdownDocument;
import org.hkijena.mcat.ui.components.MarkdownReader;
import org.hkijena.mcat.ui.components.ParameterColumnSorter;
import org.hkijena.mcat.ui.components.TransposedTableModel;
import org.hkijena.mcat.utils.UIUtils;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MCATParametersTableUI extends MCATWorkbenchUIPanel {

    private JSplitPane splitPane;
    private JXTable table;
    private TransposedTableModel transposedTableModel;
    private Point currentSelection = new Point();
    private ParameterPanel currentEditor;
    private JPopupMenu generatePopupMenu;
    private JPopupMenu replacePopupMenu;
    private boolean tableIsReloading = false;

    public MCATParametersTableUI(MCATWorkbenchUI workbenchUI) {
        super(workbenchUI);
        initialize();
        reloadGeneratePopupMenu();
        reloadReplacePopupMenu();

        // Select first cell
//        table.changeSelection(0, 0, false, false);
    }

    private void initialize() {
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new BorderLayout());

        // Create toolbar for adding/removing rows
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.NORTH);

        JButton addButton = new JButton("Add column", UIUtils.getIconFromResources("add.png"));
        addButton.setToolTipText("Adds a new column to the table. It contains the default values.");
        addButton.addActionListener(e -> addParameterSet());
        toolBar.add(addButton);

        JButton generateButton = new JButton("Generate columns", UIUtils.getIconFromResources("add.png"));
        generateButton.setToolTipText("Generates new columns and adds them to the table. You can select one row to generate data for.\n" +
                "The other columns contain default values.");
        generatePopupMenu = UIUtils.addPopupMenuToComponent(generateButton);
        toolBar.add(generateButton);

        JButton replaceButton = new JButton("Replace cells", UIUtils.getIconFromResources("edit.png"));
        replaceButton.setToolTipText("Replaces the selected cells with generated values. You have to select cells of one specific row.");
        replacePopupMenu = UIUtils.addPopupMenuToComponent(replaceButton);
        toolBar.add(replaceButton);

        toolBar.add(Box.createHorizontalGlue());

        JButton removeButton = new JButton(UIUtils.getIconFromResources("delete.png"));
        removeButton.addActionListener(e -> removeSelectedColumns());
        toolBar.add(removeButton);

        contentPanel.add(toolBar, BorderLayout.NORTH);

        // Create table
        JPanel tablePanel = new JPanel(new BorderLayout());
        transposedTableModel = new TransposedTableModel(getWorkbenchUI().getProject().getParametersTable());
        transposedTableModel.getEventBus().register(this);
        table = new JXTable();
        table.setDefaultRenderer(Object.class, new MCATParametersTableCellRenderer());
//        table.setAutoCreateRowSorter(false);
        table.setSortable(true);
        table.setCellSelectionEnabled(true);
        table.getSelectionModel().addListSelectionListener(e -> onTableCellSelected());
        table.getColumnModel().getSelectionModel().addListSelectionListener(e -> onTableCellSelected());
        table.setRowHeight(32);
        table.setModel(transposedTableModel);
        updateRowSorter();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        packColumns();
        tablePanel.add(table, BorderLayout.CENTER);
//        tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Create split pane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contentPanel,
                new MarkdownReader(false, MarkdownDocument.fromPluginResource("documentation/parameters.md")));
        splitPane.setDividerSize(3);
        splitPane.setResizeWeight(0.66);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                splitPane.setDividerLocation(0.66);
            }
        });

        add(splitPane, BorderLayout.CENTER);
    }

    private void updateRowSorter() {
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>();
        rowSorter.setModel(table.getModel());
        rowSorter.setComparator(0, new ParameterColumnSorter(getWorkbenchUI().getProject().getParametersTable()));
        table.setRowSorter(rowSorter);
    }

    private void packColumns() {
        table.packAll();
    }

    private void removeSelectedColumns() {
        int[] selectedColumns = selectedColumns(true);
        MCATParametersTable parametersTable = getWorkbenchUI().getProject().getParametersTable();
        for (int i = selectedColumns.length - 1; i >= 0; --i) {
            int row = selectedColumns[i] - 1;
            if (row >= 0)
                parametersTable.removeRowAt(row);
        }
    }

    private void addParameterSet() {
        int selectedColumn = table.getSelectedColumn();
        MCATParametersTableRow row;
        if (selectedColumn >= 1) {
            int modelColumn = table.convertColumnIndexToModel(selectedColumn) - 1;
            if (modelColumn >= 0) {
                MCATParametersTableRow existing = getWorkbenchUI().getProject().getParametersTable().getRows().get(modelColumn);
                row = new MCATParametersTableRow(existing);
            } else {
                row = new MCATParametersTableRow();
            }
        } else {
            row = new MCATParametersTableRow();
        }
        getWorkbenchUI().getProject().getParametersTable().addRow(row);
        packColumns();
    }

    private void replaceColumnValues(int column, Class<? extends MCATParameterGeneratorUI> generator) {
        if (table.getSelectedRowCount() > 1 || table.getSelectedColumnCount() == 0) {
            return;
        }

        List<Object> generatedObjects = MCATParameterGeneratorUI.showDialog(this, getWorkbenchUI().getContext(), generator);
        if (generatedObjects == null || generatedObjects.isEmpty())
            return;

        int[] cols = selectedColumns(false);
        if (generatedObjects.size() < cols.length) {
            String[] options = new String[]{
                    "Repeat last",
                    "Repeat periodic",
                    "Ignore",
                    "Cancel"
            };
            int response = JOptionPane.showOptionDialog(this,
                    "The generator generated " + generatedObjects.size() + " parameters, but you have " + cols.length + " columns selected. What to do with the extra columns?",
                    "Replace cells",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (response == 3)
                return;
            else if (response == 0) {
                while (generatedObjects.size() < cols.length) {
                    generatedObjects.add(generatedObjects.get(generatedObjects.size() - 1));
                }
            } else if (response == 1) {
                int sz = generatedObjects.size();
                for (int i = 0; i < cols.length; i++) {
                    if (i >= sz) {
                        generatedObjects.add(generatedObjects.get(i % sz));
                    }
                }
            }
        }

        MCATParametersTable parametersTable = getWorkbenchUI().getProject().getParametersTable();

        for (int i = 0; i < Math.min(generatedObjects.size(), cols.length); ++i) {
            int row = cols[i] - 1;
            if (row >= 0)
                parametersTable.setValueAt(generatedObjects.get(i), row, column);
        }
    }

    /**
     * Returns selected rows as sorted array of model indices
     *
     * @return selected rows in model indices
     */
    private int[] selectedColumns(boolean sort) {
        int[] displayColumns = table.getSelectedColumns();
        for (int i = 0; i < displayColumns.length; ++i) {
            displayColumns[i] = table.getRowSorter().convertRowIndexToModel(displayColumns[i]);
        }
        if (sort)
            Arrays.sort(displayColumns);
        return displayColumns;
    }


    private void generateRow(int columnIndex, Class<? extends MCATParameterGeneratorUI> generator) {
        List<Object> generatedObjects = MCATParameterGeneratorUI.showDialog(this, getWorkbenchUI().getContext(), generator);
        if (generatedObjects != null) {
            MCATParametersTable parametersTable = getWorkbenchUI().getProject().getParametersTable();
            for (Object generatedObject : generatedObjects) {
                parametersTable.addRow();
                parametersTable.setValueAt(generatedObject, parametersTable.getRowCount() - 1, columnIndex);
            }
        }
    }

    private void onTableCellSelected() {
        if (tableIsReloading)
            return;
        Point selection = new Point(table.getSelectedRow(), table.getSelectedColumn());
        if (!Objects.equals(selection, currentSelection)) {
            currentSelection = selection;
            MCATParameterCollection selectedRow = null;
            if (currentSelection.x != -1 && currentSelection.y > 0) {
                selectedRow = getWorkbenchUI().getProject().getParametersTable().getRows().get(currentSelection.y - 1);
            }
            if (selectedRow == null) {
                splitPane.setRightComponent(new MarkdownReader(false, MarkdownDocument.fromPluginResource("documentation/parameters.md")));
                currentEditor = null;
            } else if (currentEditor == null || currentEditor.getParameterCollection() != selectedRow) {
                currentEditor = new ParameterPanel(getWorkbenchUI().getContext(),
                        selectedRow,
                        MarkdownDocument.fromPluginResource("documentation/parameters.md"),
                        ParameterPanel.WITH_SCROLLING | ParameterPanel.WITH_DOCUMENTATION | ParameterPanel.DOCUMENTATION_BELOW);
                currentEditor.setPreferredSize(new Dimension((int) (0.33 * splitPane.getWidth()), (int) currentEditor.getPreferredSize().getHeight()));
                splitPane.setRightComponent(currentEditor);
                revalidate();
            }
        }
        reloadReplacePopupMenu();
    }

    @Subscribe
    public void onTableDataChanged(ParameterChangedEvent event) {
        if ("table-data".equals(event.getKey())) {
            Point selection = new Point(table.getSelectedRow(), table.getSelectedColumn());
            tableIsReloading = true;
            table.setModel(new DefaultTableModel());
            table.setModel(transposedTableModel);
            updateRowSorter();
            if (selection.x >= 0 && selection.x < table.getRowCount() && selection.y >= 0 && selection.y < table.getColumnCount()) {
                table.changeSelection(selection.x, selection.y, false, false);
            }
            tableIsReloading = false;
            packColumns();
        }
    }

    private void reloadGeneratePopupMenu() {
        generatePopupMenu.removeAll();
        MCATParametersTable parametersTable = getWorkbenchUI().getProject().getParametersTable();
        boolean hasColumnEntries = false;

        for (int col = 0; col < parametersTable.getColumnCount(); ++col) {
            JMenu columnMenu = new JMenu(parametersTable.getColumnName(col));
            columnMenu.setIcon(UIUtils.getIconFromResources("wrench.png"));

            for (Class<? extends MCATParameterGeneratorUI> generator : MCATUIParametertypeRegistry.getInstance()
                    .getGeneratorsFor(parametersTable.getColumnClass(col))) {
                MCATDocumentation documentation = MCATUIParametertypeRegistry.getInstance().getGeneratorDocumentationFor(generator);
                JMenuItem generateRowItem = new JMenuItem(documentation.name());
                generateRowItem.setToolTipText(documentation.description());
                generateRowItem.setIcon(UIUtils.getIconFromResources("add.png"));
                int finalCol = col;
                generateRowItem.addActionListener(e -> generateRow(finalCol, generator));

                columnMenu.add(generateRowItem);
            }

            if (columnMenu.getItemCount() > 0) {
                generatePopupMenu.add(columnMenu);
                hasColumnEntries = true;
            }
        }

        if (!hasColumnEntries) {
            JMenuItem noItem = new JMenuItem("Nothing to generate");
            noItem.setEnabled(false);
            generatePopupMenu.add(noItem);
        }
    }

    private void reloadReplacePopupMenu() {
        replacePopupMenu.removeAll();
        if (table.getSelectedRowCount() == 0) {
            JMenuItem noItem = new JMenuItem("Please select cells of one row");
            noItem.setEnabled(false);
            replacePopupMenu.add(noItem);
            return;
        }
        if (table.getSelectedRowCount() > 1) {
            JMenuItem noItem = new JMenuItem("Please select only cells of one row");
            noItem.setEnabled(false);
            replacePopupMenu.add(noItem);
            return;
        }
        int col = table.getSelectedRow();
        boolean hasColumnEntries = false;

        if (col != -1) {
            for (Class<? extends MCATParameterGeneratorUI> generator : MCATUIParametertypeRegistry.getInstance()
                    .getGeneratorsFor(getWorkbenchUI().getProject().getParametersTable().getColumnClass(col))) {
                MCATDocumentation documentation = MCATUIParametertypeRegistry.getInstance().getGeneratorDocumentationFor(generator);
                JMenuItem replaceCellItem = new JMenuItem(documentation.name());
                replaceCellItem.setToolTipText(documentation.description());
                replaceCellItem.setIcon(UIUtils.getIconFromResources("edit.png"));
                replaceCellItem.addActionListener(e -> replaceColumnValues(col, generator));

                replacePopupMenu.add(replaceCellItem);
                hasColumnEntries = true;
            }
        }

        if (!hasColumnEntries) {
            JMenuItem noItem = new JMenuItem("Nothing to generate");
            noItem.setEnabled(false);
            replacePopupMenu.add(noItem);
        }
    }


}
