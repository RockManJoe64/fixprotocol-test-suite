package org.fixprotocol.ui;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.fixprotocol.fix.FIXMessage;
import org.fixprotocol.ui.event.DataDictionaryEvent;
import org.fixprotocol.ui.event.DataDictionaryListener;
import org.fixprotocol.ui.icon.IconCache;
import org.fixprotocol.ui.list.DataDictionaryFileDialog;
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
	
	class SetDataDictionaryAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SetDataDictionaryAction() {
			super("Set DataDictionary");
			putValue(LARGE_ICON_KEY, IconCache.getInstance().getIcon("dictionary", 24));
			putValue(SMALL_ICON, IconCache.getInstance().getIcon("dictionary", 16));
			putValue(SHORT_DESCRIPTION, "Set the DataDictionary");
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			dictionariesDialog.setLocationRelativeTo(FIXMessageAnalyzePanel.this);
			dictionariesDialog.setVisible(true);
		}
	}

    private FIXFieldTablePanel pnlFixMsgTable;
    private FIXMessageListPanel fixMsgListPanel;
    private JSplitPane splitPaneListTbl;
    private JSplitPane splitPaneMain;
	private FIXMessageTextAreaPanel pnlFixMessageTextArea;
	private FIXMessageMetaTablePanel fixMessageMetaTablePanel;
	private DataDictionaryFileDialog dictionariesDialog;
	
    public FIXMessageAnalyzePanel() {
        initUI();
    }

    private void initUI() {
    	List<Image> frameIcons = IconCache.getInstance().getIconImageList(
				"fixprotocol", 16, 24, 32, 48);
    	
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
        
        Window ancestor = SwingUtilities.getWindowAncestor(this);
        dictionariesDialog = new DataDictionaryFileDialog(ancestor,
				"Data Dictionaries", ModalityType.APPLICATION_MODAL);
		dictionariesDialog.addDataDictionaryListener(this);
		dictionariesDialog.setIconImages(frameIcons);
		dictionariesDialog.setSize(640, 320);
		dictionariesDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(new SetDataDictionaryAction());
		
        setLayout(new BorderLayout());
        add(splitPaneMain);
        add(toolBar, BorderLayout.NORTH);
    }

    public void doPostVisibleAction() {
        splitPaneListTbl.setDividerLocation(0.5);
        splitPaneMain.setDividerLocation(0.25);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        doPostVisibleAction();
        dictionariesDialog.loadDictionaries();
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
