package org.fixprotocol.ui.table;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;

import org.fixprotocol.fix.FIXField;
import org.fixprotocol.ui.event.FIXMessageEvent;
import org.fixprotocol.ui.event.FIXMessageListener;
import org.jdesktop.swingx.decorator.HighlighterFactory;


public class FIXFieldTablePanel extends JPanel implements FIXMessageListener {

    private static final long serialVersionUID = 1L;

    private static final String[] COL_NAMES = new String[] { "Name", "Tag", "Value" };

    private FIXFieldTable jTable;

    public FIXFieldTablePanel() {
        initUI();
    }

    private void initUI() {
        FIXFieldTableModel model = new FIXFieldTableModel();
        FIXTagEditorModel tagEditorModel = new FIXTagEditorModel();

        jTable = new FIXFieldTable(model, tagEditorModel);
        jTable.setAutoscrolls(false);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable.setFont(new Font("Consolas", Font.PLAIN, 12));
        jTable.addHighlighter(HighlighterFactory.createSimpleStriping());
		jTable.setSortOrderCycle(new SortOrder[] { 
				SortOrder.ASCENDING,
				SortOrder.DESCENDING, 
				SortOrder.UNSORTED });

        JScrollPane scrollPane = new JScrollPane(jTable);
        this.setLayout(new BorderLayout());
        this.add(scrollPane);
    }

    public void setData(List<FIXField> fixFields) {
        FIXFieldTableModel model = (FIXFieldTableModel) jTable.getModel();
        model.update(fixFields);
    }

    public void setModel(TableModel dataModel) {
        if (dataModel instanceof FIXFieldTableModel) {
            jTable.setModel(dataModel);
        } else {
            throw new IllegalArgumentException("TableModel not of type FIXFieldTableModel");
        }
    }

	@Override
	public void fixMessageUpdated(FIXMessageEvent e) {
		setData(e.getFIXMessage().getAll());
	}

	@Override
	public void fixMessageAdded(FIXMessageEvent e) {
	}

	@Override
	public void fixMessageRemoved(FIXMessageEvent e) {
	}

}
