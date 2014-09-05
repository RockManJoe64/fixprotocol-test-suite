package org.fixprotocol.ui.text;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.fixprotocol.fix.ASCII;
import org.fixprotocol.fix.FIXField;
import org.fixprotocol.fix.FIXMessage;
import org.fixprotocol.fix.FIXProtocol;
import org.fixprotocol.ui.DataDictionaryAware;
import org.fixprotocol.ui.event.FIXMessageEvent;
import org.fixprotocol.ui.event.FIXMessageListener;

import quickfix.ConfigError;
import quickfix.DataDictionary;

/**
 * @author chavez.j
 * Sep 4, 2014
 */
public class FIXMessageTextAreaPanel extends JPanel implements DataDictionaryAware {

	private static final long serialVersionUID = 1L;
	
	private JTextArea fixMessageTextArea;
	private DataDictionary dataDictionary;
	
	private class UpdateAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public UpdateAction() {
            super("Update");
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            String messageString = fixMessageTextArea.getText();
            messageString = FIXProtocol.normalize(messageString);
            try {
                List<FIXField> fixFields = FIXProtocol.getInstance().getFields(
                        messageString, dataDictionary);
                FIXMessage message = new FIXMessage(fixFields);
                fireFIXMessageUpdated(message);
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
            String text = fixMessageTextArea.getText();
            if (text.contains(String.valueOf(ASCII.SOH))) {
                text = text.replace(ASCII.SOH, ASCII.EOL);
            } else if (text.contains("|")) {
                text = text.replace('|', ASCII.EOL);
            }

            fixMessageTextArea.setText(text);
            fixMessageTextArea.selectAll();
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
            String text = fixMessageTextArea.getText();
            if (text.contains(String.valueOf(ASCII.SOH))) {
                text = text.replace(ASCII.SOH, ASCII.EOL);
            } else if (text.contains("|")) {
                text = text.replace('|', ASCII.EOL);
            }

            text = text.replace('=', '\t');

            fixMessageTextArea.setText(text);
            fixMessageTextArea.selectAll();
        }
    }
    
    public FIXMessageTextAreaPanel() {
    	initUI();
	}

	private void initUI() {
		fixMessageTextArea = new JTextArea();
        fixMessageTextArea.setLineWrap(true);
        fixMessageTextArea.setWrapStyleWord(false);

        JScrollPane inputScrollPane = new JScrollPane(fixMessageTextArea);

        JButton excelButton = new JButton(new ConvertToExcelAction());
        JButton aegisButton = new JButton(new ConvertToAegisAction());
        JButton addButton = new JButton(new UpdateAction());
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButton.add(excelButton);
        pnlButton.add(aegisButton);
        pnlButton.add(addButton);

        this.setLayout(new BorderLayout(5, 5));
        this.add(inputScrollPane);
        this.add(pnlButton, BorderLayout.SOUTH);
	}
	
	public void addFIXMessageListener(FIXMessageListener l) {
		this.listenerList.add(FIXMessageListener.class, l);
	}
	
	protected void fireFIXMessageUpdated(FIXMessage message) {
		FIXMessageListener[] listeners = listenerList
				.getListeners(FIXMessageListener.class);
		for (FIXMessageListener l : listeners) {
			l.fixMessageUpdated(new FIXMessageEvent(this, message));
		}
	}
	
	/**
	 * @return the fixMessageTextArea
	 */
	public JTextArea getFixMessageTextArea() {
		return fixMessageTextArea;
	}

	@Override
	public DataDictionary getDataDictionary() {
		return this.dataDictionary;
	}

	@Override
	public void setDataDictionary(DataDictionary dd) {
		this.dataDictionary = dd;
	}
	
}
