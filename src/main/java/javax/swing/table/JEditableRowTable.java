package javax.swing.table;

import javax.swing.JTable;

public class JEditableRowTable extends JTable {

    private static final long serialVersionUID = 1L;

    protected TableRowEditorModel tableRowEditorModel;

    // new constructor
    public JEditableRowTable(TableModel tableModel, TableRowEditorModel tableRowEditorModel) {
        super(tableModel, null, null);
        this.tableRowEditorModel = tableRowEditorModel;
    }

    public void setTableRowEditorModel(TableRowEditorModel tableRowEditorModel) {
        this.tableRowEditorModel = tableRowEditorModel;
    }

    public TableRowEditorModel getTableRowEditorModel() {
        return tableRowEditorModel;
    }

    @Override
    public TableCellEditor getCellEditor(int row, int col) {
        TableCellEditor tmpEditor = null;

        if (tableRowEditorModel != null) {
            tmpEditor = tableRowEditorModel.getTableRowEditor(row);
        }

        if (tmpEditor != null) { return tmpEditor; }

        return super.getCellEditor(row, col);
    }

}
