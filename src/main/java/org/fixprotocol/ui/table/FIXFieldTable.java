package org.fixprotocol.ui.table;

import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;

public class FIXFieldTable extends JXTable {

    private static final long serialVersionUID = 1L;

    protected FIXTagEditorModel tagEditorModel;

    public FIXFieldTable() {
        super();
    }

    public FIXFieldTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public FIXFieldTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    public FIXFieldTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public FIXFieldTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public FIXFieldTable(TableModel dm) {
        super(dm);
    }

    @SuppressWarnings("rawtypes")
	public FIXFieldTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
    }

    // new constructor
    public FIXFieldTable(TableModel tableModel, FIXTagEditorModel tagEditorModel) {
        super(tableModel, null, null);
        this.tagEditorModel = tagEditorModel;
    }

    public void setTableRowEditorModel(FIXTagEditorModel tagEditorModel) {
        this.tagEditorModel = tagEditorModel;
    }

    public FIXTagEditorModel getTableRowEditorModel() {
        return tagEditorModel;
    }

    @Override
    public TableCellEditor getCellEditor(int row, int col) {
        TableCellEditor tmpEditor = null;

        if (tagEditorModel != null) {
            tmpEditor = tagEditorModel.getTagEditor(row);
        }

        if (tmpEditor != null) { return tmpEditor; }

        return super.getCellEditor(row, col);
    }

}
