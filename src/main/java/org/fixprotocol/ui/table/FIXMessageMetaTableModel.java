package org.fixprotocol.ui.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.fixprotocol.fix.FIXMessage;

import quickfix.field.BeginString;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.SenderCompID;
import quickfix.field.SenderSubID;
import quickfix.field.SendingTime;
import quickfix.field.TargetCompID;
import quickfix.field.TargetSubID;

/**
 * @author Jose Chavez <chavez.j@eoxlive.com>
 * @since Sep 11, 2014
 */
public class FIXMessageMetaTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<FIXMessage> data = new ArrayList<FIXMessage>();
	
	public FIXMessageMetaTableModel() {
	}
	
	public FIXMessageMetaTableModel(ArrayList<FIXMessage> data) {
		this.data = data;
	}

	public FIXMessage getFIXMessageAt(int rowIndex) {
		return data.get(rowIndex);
	}
	
	public boolean add(FIXMessage e) {
		boolean success = data.add(e);
		fireTableDataChanged();
		return success;
	}

	public void add(int index, FIXMessage element) {
		data.add(index, element);
		fireTableDataChanged();
	}

	public FIXMessage remove(int index) {
		FIXMessage success = data.remove(index);
		fireTableDataChanged();
		return success;
	}

	public boolean remove(FIXMessage o) {
		boolean success = data.remove(o);
		fireTableDataChanged();
		return success;
	}

	public void clear() {
		data.clear();
		fireTableDataChanged();
	}

	public void update(List<FIXMessage> data) {
		this.data.clear();
		this.data.addAll(data);
		fireTableDataChanged();
	}

	private enum Column {
		BEGINSTRING("BeginString"),
		MSGTYPE("MsgType"),
		SENDERCOMPID("SenderCompID"),
		SENDERSUBID("SenderSubID"),
		TARGETCOMPID("TargetCompID"),
		TARGETSUBID("TargetSubID"),
		MSGSEQNUM("MsgSeqNum"),
		SENDINGTIME("SendingTime")
		;
		
		static Column getColumn(int index){
			Column[] columns = Column.values();
			return columns[index];
		}
		
		private String columnName;

		private Column(String columnName) {
			this.columnName = columnName;
		}

		public String getColumnName() {
			return columnName;
		}
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return Column.getColumn(columnIndex).getColumnName();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (data.size() < 1) {
			return null;
		}
		Column column = Column.getColumn(columnIndex);
		FIXMessage row = data.get(rowIndex);
		switch (column) {
		case BEGINSTRING:
			return row.get(BeginString.FIELD).getValue();
		case MSGTYPE:
			return row.get(MsgType.FIELD).getValue();
		case SENDERCOMPID:
			return row.get(SenderCompID.FIELD).getValue();
		case SENDERSUBID:
			return row.has(SenderSubID.FIELD) ? row.get(SenderSubID.FIELD)
					.getValue() : null;
		case TARGETCOMPID:
			return row.get(TargetCompID.FIELD).getValue();
		case TARGETSUBID:
			return row.has(TargetSubID.FIELD) ? row.get(TargetSubID.FIELD)
					.getValue() : null;
		case MSGSEQNUM:
			return row.get(MsgSeqNum.FIELD).getValue();
		case SENDINGTIME:
			return row.get(SendingTime.FIELD).getValue();
		default:
			return null;
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Do nothing
	}

}
