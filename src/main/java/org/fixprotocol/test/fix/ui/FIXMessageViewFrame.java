package org.fixprotocol.test.fix.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.fixprotocol.test.fix.FIXMessage;
import org.fixprotocol.test.fix.ui.table.FIXFieldTablePanel;


public class FIXMessageViewFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private FIXMessage fixMessage;

    public FIXMessageViewFrame(FIXMessage fixMessage) throws HeadlessException {
        this.fixMessage = fixMessage;
        initUI();
    }

    @SuppressWarnings("serial")
    private void initUI() {
        JTextArea txtInputArea = new JTextArea();
        txtInputArea.setLineWrap(true);
        txtInputArea.setWrapStyleWord(false);
        txtInputArea.setEditable(false);
        txtInputArea.setText(fixMessage.getMessageString());

        JScrollPane inputScrollPane = new JScrollPane(txtInputArea);

        FIXFieldTablePanel pnlFixMsgTable = new FIXFieldTablePanel();
        pnlFixMsgTable.setData(fixMessage.getFields());

        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                true, inputScrollPane, pnlFixMsgTable);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout(5, 5));
        cp.add(splitPane);

        pack();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                splitPane.setDividerLocation(0.5);
            }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("ESCAPE"), "CloseWindow");
        getRootPane().getActionMap().put("CloseWindow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    public void display(JComponent parent) {
        setLocationRelativeTo(parent);
        setVisible(true);
    }

}
