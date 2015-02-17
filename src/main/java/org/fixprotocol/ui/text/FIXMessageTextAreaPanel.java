package org.fixprotocol.ui.text;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
	private JComboBox<Delimiter> delimiterCBox;

	private enum Delimiter {
		SOH(ASCII.SOH),
		PIPE(ASCII.PIPE),
		NEWLINE(ASCII.EOL);

		private Character delimiter;

		private Delimiter(Character delimiter) {
			this.delimiter = delimiter;
		}

		public Character getDelimiter() {
			return delimiter;
		}
	}

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

		delimiterCBox = new JComboBox<Delimiter>(new Delimiter[] {
				Delimiter.SOH,
				Delimiter.PIPE,
				Delimiter.NEWLINE
				});
		delimiterCBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					denormalize();
				} else if (e.getStateChange() == ItemEvent.SELECTED) {
					normalize((Delimiter) delimiterCBox.getSelectedItem());
				}
			}
		});

		JTextField txtHighlightTags = new JTextField(32);

        JButton excelButton = new JButton(new ConvertToExcelAction());
        JButton aegisButton = new JButton(new ConvertToAegisAction());
        JButton addButton = new JButton(new UpdateAction());
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButton.add(delimiterCBox);
        pnlButton.add(excelButton);
        pnlButton.add(aegisButton);
        pnlButton.add(addButton);

        this.setLayout(new BorderLayout(5, 5));
        this.add(inputScrollPane);
        this.add(pnlButton, BorderLayout.SOUTH);
	}

	private void normalize(Delimiter del) {
		String text = fixMessageTextArea.getText();
		if (text != null && !"".equals(text)) {
			text = text.replace(ASCII.SOH, del.getDelimiter());
			fixMessageTextArea.setText(text);
		}
	}

	private void denormalize() {
		String text = fixMessageTextArea.getText();
		if (text == null || "".equals(text))
			return;
		if (text.contains(String.valueOf(ASCII.PIPE))) {
            text = text.replace(ASCII.PIPE, ASCII.SOH);
        } else if (text.contains(String.valueOf(ASCII.EOL))) {
            text = text.replace(ASCII.EOL, ASCII.SOH);
        }
        fixMessageTextArea.setText(text);
	}

	public void addFIXMessageListener(FIXMessageListener l) {
		this.listenerList.add(FIXMessageListener.class, l);
	}

	public void removeFIXMessageListener(FIXMessageListener l) {
		this.listenerList.remove(FIXMessageListener.class, l);
	}

	protected void fireFIXMessageUpdated(FIXMessage message) {
		FIXMessageListener[] listeners = listenerList
				.getListeners(FIXMessageListener.class);
		for (FIXMessageListener l : listeners) {
			l.fixMessageUpdated(new FIXMessageEvent(this, message));
		}
	}

	public void updateFIXMessage(FIXMessage message) {
		String text = message.toString();
		this.fixMessageTextArea.setText(text);
		normalize((Delimiter) delimiterCBox.getSelectedItem());
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
