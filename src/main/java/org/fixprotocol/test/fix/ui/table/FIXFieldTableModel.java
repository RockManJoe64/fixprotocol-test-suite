package org.fixprotocol.test.fix.ui.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.fixprotocol.test.fix.FIXField;


public class FIXFieldTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final int COLUMN_INDEX_NAME = 0;
    private static final int COLUMN_INDEX_TAG = 1;
    private static final int COLUMN_INDEX_VALUE = 2;

    private List<FIXField> data = new ArrayList<FIXField>();

    public FIXFieldTableModel() {
    }

    public FIXFieldTableModel(List<FIXField> fixFields) {
        update(fixFields);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case COLUMN_INDEX_NAME:
            return String.class;
        case COLUMN_INDEX_TAG:
            return Integer.class;
        case COLUMN_INDEX_VALUE:
            return Object.class;
        default:
            throw new IndexOutOfBoundsException("Column index does not exist: " + columnIndex);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == COLUMN_INDEX_VALUE;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == COLUMN_INDEX_VALUE) {
            FIXField field = data.get(rowIndex);
            field.set(aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public void update(List<FIXField> fixFields) {
        data = new ArrayList<FIXField>(fixFields);
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
        case COLUMN_INDEX_NAME:
            return "Name";
        case COLUMN_INDEX_TAG:
            return "Tag";
        case COLUMN_INDEX_VALUE:
            return "Value";
        default:
            throw new IndexOutOfBoundsException("Column index does not exist: " + column);
        }
    }

    @Override
	public int getRowCount() {
        return data.size();
    }

    @Override
	public int getColumnCount() {
        return 3;
    }

    @Override
	public Object getValueAt(int rowIndex, int columnIndex) {
        FIXField field = data.get(rowIndex);
        switch (columnIndex) {
        case COLUMN_INDEX_NAME:
            return field.getName();
        case COLUMN_INDEX_TAG:
            return field.getTag();
        case COLUMN_INDEX_VALUE:
            return field.getValue();
        default:
            throw new IndexOutOfBoundsException("Column index does not exist: " + columnIndex);
        }
    }

}
