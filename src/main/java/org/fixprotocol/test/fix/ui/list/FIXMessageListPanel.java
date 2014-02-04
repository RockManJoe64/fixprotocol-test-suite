package org.fixprotocol.test.fix.ui.list;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;

public class FIXMessageListPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JList<FIXMessageListItem> jList;
    private DefaultListModel<FIXMessageListItem> dataModel;

    public FIXMessageListPanel() {
        initUI();
    }

    private void initUI() {
        dataModel = new DefaultListModel<FIXMessageListItem>();
        jList = new JList<FIXMessageListItem>(dataModel);

        JScrollPane scrollPane = new JScrollPane(jList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        setLayout(new BorderLayout(5, 5));
        add(scrollPane);
    }

    public void addElement(FIXMessageListItem element) {
        dataModel.addElement(element);
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        jList.addListSelectionListener(listener);
    }

}
