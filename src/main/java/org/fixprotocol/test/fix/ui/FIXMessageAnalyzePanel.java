package org.fixprotocol.test.fix.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.fixprotocol.test.fix.ASCII;
import org.fixprotocol.test.fix.FIXField;
import org.fixprotocol.test.fix.FIXMessage;
import org.fixprotocol.test.fix.FIXProtocol;
import org.fixprotocol.test.fix.ui.list.FIXMessageListItem;
import org.fixprotocol.test.fix.ui.list.FIXMessageListPanel;
import org.fixprotocol.test.fix.ui.table.FIXFieldTablePanel;

import quickfix.ConfigError;


public class FIXMessageAnalyzePanel extends JPanel implements WindowListener {

    private static final long serialVersionUID = 1L;

    private class UpdateAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public UpdateAction() {
            super("Update");
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            String messageString = txtInputArea.getText();
            messageString = FIXProtocol.normalize(messageString);
            try {
                List<FIXField> fixFields = FIXProtocol.getInstance().getFields(
                        messageString);
                pnlFixMsgTable.setData(fixFields);

                FIXMessage message = new FIXMessage(fixFields);
                fixMsgListPanel.addElement(new FIXMessageListItem(message));
            } catch (ConfigError | IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class ConvertToAegisAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public ConvertToAegisAction() {
            super("Aegis");
            putValue(SHORT_DESCRIPTION, "Convert to Aegis input");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String text = txtInputArea.getText();
            if (text.contains(String.valueOf(ASCII.SOH))) {
                text = text.replace(ASCII.SOH, ASCII.EOL);
            } else if (text.contains("|")) {
                text = text.replace('|', ASCII.EOL);
            }

            txtInputArea.setText(text);
            txtInputArea.selectAll();
        }
    }

    private class ConvertToExcelAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public ConvertToExcelAction() {
            super("Excel");
            putValue(SHORT_DESCRIPTION, "Convert to Excel input");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String text = txtInputArea.getText();
            if (text.contains(String.valueOf(ASCII.SOH))) {
                text = text.replace(ASCII.SOH, ASCII.EOL);
            } else if (text.contains("|")) {
                text = text.replace('|', ASCII.EOL);
            }

            text = text.replace('=', '\t');

            txtInputArea.setText(text);
            txtInputArea.selectAll();
        }
    }

    private class SelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            @SuppressWarnings("unchecked")
            JList<FIXMessageListItem> list = (JList<FIXMessageListItem>) e
                    .getSource();
            FIXMessageListItem listItem = list.getSelectedValue();
            FIXMessage message = listItem.getMessage();
            pnlFixMsgTable.setData(message.getAll());
            txtInputArea.setText(message.toString());
        }
    }

    private JTextArea txtInputArea;
    private FIXFieldTablePanel pnlFixMsgTable;
    private FIXMessageListPanel fixMsgListPanel;
    private JSplitPane splitPaneListTbl;
    private JSplitPane splitPaneMain;

    public FIXMessageAnalyzePanel() {
        initUI();
    }

    private void initUI() {
        pnlFixMsgTable = new FIXFieldTablePanel();

        txtInputArea = new JTextArea();
        txtInputArea.setLineWrap(true);
        txtInputArea.setWrapStyleWord(false);

        JScrollPane inputScrollPane = new JScrollPane(txtInputArea);

        JButton excelButton = new JButton(new ConvertToExcelAction());
        JButton aegisButton = new JButton(new ConvertToAegisAction());
        JButton addButton = new JButton(new UpdateAction());
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButton.add(excelButton);
        pnlButton.add(aegisButton);
        pnlButton.add(addButton);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout(5, 5));
        textPanel.add(inputScrollPane);
        textPanel.add(pnlButton, BorderLayout.SOUTH);

        fixMsgListPanel = new FIXMessageListPanel();
        fixMsgListPanel.addListSelectionListener(new SelectionListener());

        splitPaneListTbl = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                true, fixMsgListPanel, pnlFixMsgTable);

        splitPaneMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                true, splitPaneListTbl, textPanel);

        setLayout(new BorderLayout());
        add(splitPaneMain);
    }

    public void doPostVisibleAction() {
        splitPaneListTbl.setDividerLocation(0.5);
        splitPaneMain.setDividerLocation(0.75);
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

}
