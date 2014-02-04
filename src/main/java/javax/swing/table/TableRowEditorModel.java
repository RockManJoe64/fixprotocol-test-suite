package javax.swing.table;

import java.util.HashMap;
import java.util.Map;

public class TableRowEditorModel {

    private Map<Integer, TableCellEditor> editors = new HashMap<Integer, TableCellEditor>();

    public void addTableRowEditorForRow(int row, TableCellEditor e) {
        editors.put(new Integer(row), e);
    }

    public void removeTableRowEditorForRow(int row) {
        Integer key = new Integer(row);
        editors.remove(key);
    }

    public TableCellEditor getTableRowEditor(int row) {
        Integer key = new Integer(row);
        return editors.get(key);
    }

}
