package org.fixprotocol.ui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.fixprotocol.fix.FIXMessage;
import org.fixprotocol.ui.event.DataDictionaryEvent;
import org.fixprotocol.ui.event.DataDictionaryListener;
import org.fixprotocol.ui.list.FIXMessageListPanel;
import org.fixprotocol.ui.table.FIXFieldTablePanel;
import org.fixprotocol.ui.table.FIXMessageMetaTablePanel;
import org.fixprotocol.ui.text.FIXMessageTextAreaPanel;

import quickfix.DataDictionary;


public class FIXMessageAnalyzePanel extends JPanel implements WindowListener,
		DataDictionaryListener {

    private static final long serialVersionUID = 1L;
    
    private DataDictionary dataDictionary;

	private class SelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
//			FIXMessage message = fixMsgListPanel.getSelectedValue();
			FIXMessage message = fixMessageMetaTablePanel.getSelectedValue();
			pnlFixMsgTable.setData(message.getAll());
			pnlFixMessageTextArea.updateFIXMessage(message);
		}
	}

    private FIXFieldTablePanel pnlFixMsgTable;
    private FIXMessageListPanel fixMsgListPanel;
    private JSplitPane splitPaneListTbl;
    private JSplitPane splitPaneMain;
	private FIXMessageTextAreaPanel pnlFixMessageTextArea;
	private FIXMessageMetaTablePanel fixMessageMetaTablePanel;

    public FIXMessageAnalyzePanel() {
        initUI();
    }

    private void initUI() {
    	pnlFixMessageTextArea = new FIXMessageTextAreaPanel();
    	
        pnlFixMsgTable = new FIXFieldTablePanel();
        pnlFixMessageTextArea.addFIXMessageListener(pnlFixMsgTable);
        
//        fixMsgListPanel = new FIXMessageListPanel();
//        fixMsgListPanel.addListSelectionListener(new SelectionListener());
//        pnlFixMessageTextArea.addFIXMessageListener(fixMsgListPanel);
        
        fixMessageMetaTablePanel = new FIXMessageMetaTablePanel();
        fixMessageMetaTablePanel.addListSelectionListener(new SelectionListener());
        pnlFixMessageTextArea.addFIXMessageListener(fixMessageMetaTablePanel);

        splitPaneListTbl = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                true, pnlFixMessageTextArea, pnlFixMsgTable);

        splitPaneMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                true, fixMessageMetaTablePanel, splitPaneListTbl);

        setLayout(new BorderLayout());
        add(splitPaneMain);
    }

    public void doPostVisibleAction() {
        splitPaneListTbl.setDividerLocation(0.5);
        splitPaneMain.setDividerLocation(0.25);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        doPostVisibleAction();
    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

	public DataDictionary getDataDictionary() {
		return this.dataDictionary;
	}

	public void setDataDictionary(DataDictionary dd) {
		this.dataDictionary = dd;
		pnlFixMessageTextArea.setDataDictionary(dd);
//		fixMsgListPanel.setDataDictionary(dd);
		fixMessageMetaTablePanel.setDataDictionary(dd);
	}

	@Override
	public void dataDictionaryAdded(DataDictionaryEvent e) {
	}

	@Override
	public void dataDictionaryRemoved(DataDictionaryEvent e) {
	}

	@Override
	public void dataDictionarySelected(DataDictionaryEvent e) {
		setDataDictionary(e.getDataDictionary());
	}

}
