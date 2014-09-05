package org.fixprotocol.ui.list;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionListener;

import org.fixprotocol.ui.DataDictionaryAware;
import org.fixprotocol.ui.event.FIXMessageEvent;
import org.fixprotocol.ui.event.FIXMessageListener;

import quickfix.DataDictionary;

public class FIXMessageListPanel extends JPanel implements FIXMessageListener, DataDictionaryAware {

    private static final long serialVersionUID = 1L;

    private JList<FIXMessageListItem> jList;
    private DefaultListModel<FIXMessageListItem> dataModel;
    private DataDictionary dataDictionary;
    
    class ClearAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ClearAction() {
    		super("Clear");
		}
    	
		@Override
		public void actionPerformed(ActionEvent e) {
			dataModel.clear();
		}
    }
    
    class LoadLogFile extends AbstractAction {

    	public LoadLogFile() {
    		super("Load");
		}
    	
		@Override
		public void actionPerformed(ActionEvent e) {
			
		}
    	
    }

    public FIXMessageListPanel() {
        initUI();
    }

    private void initUI() {
        dataModel = new DefaultListModel<FIXMessageListItem>();
        jList = new JList<FIXMessageListItem>(dataModel);

        JScrollPane scrollPane = new JScrollPane(jList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        JToolBar toolBar = new JToolBar();
        toolBar.add(new ClearAction());
        toolBar.setFloatable(false);
        
        setLayout(new BorderLayout(5, 5));
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane);
    }

    public void addElement(FIXMessageListItem element) {
        dataModel.addElement(element);
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        jList.addListSelectionListener(listener);
    }

	@Override
	public void fixMessageUpdated(FIXMessageEvent e) {
		addElement(new FIXMessageListItem(e.getFIXMessage(), dataDictionary));
	}

	@Override
	public void fixMessageAdded(FIXMessageEvent e) {
	}

	@Override
	public void fixMessageRemoved(FIXMessageEvent e) {
	}

	@Override
	public DataDictionary getDataDictionary() {
		return dataDictionary;
	}

	@Override
	public void setDataDictionary(DataDictionary dd) {
		this.dataDictionary = dd;
	}

}
