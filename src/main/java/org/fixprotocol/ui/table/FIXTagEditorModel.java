package org.fixprotocol.ui.table;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.TableCellEditor;

public class FIXTagEditorModel {

    private Map<Integer, TableCellEditor> editors = new HashMap<Integer, TableCellEditor>();

    public void addEditorForTag(int tag, TableCellEditor e) {
        editors.put(new Integer(tag), e);
    }

    public void removeEditorForTag(int tag) {
        Integer key = new Integer(tag);
        editors.remove(key);
    }

    public TableCellEditor getTagEditor(int row) {
        Integer key = new Integer(row);
        return editors.get(key);
    }
    
}
