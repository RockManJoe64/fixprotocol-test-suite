package org.fixprotocol.ui.list;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionListener;

import org.fixprotocol.fix.FIXField;
import org.fixprotocol.fix.FIXMessage;
import org.fixprotocol.fix.FIXProtocol;
import org.fixprotocol.ui.DataDictionaryAware;
import org.fixprotocol.ui.event.FIXMessageEvent;
import org.fixprotocol.ui.event.FIXMessageListener;
import org.jdesktop.swingx.JXList;

import quickfix.ConfigError;
import quickfix.DataDictionary;

public class FIXMessageListPanel extends JPanel implements FIXMessageListener,
		DataDictionaryAware {

	private static final long serialVersionUID = 1L;

	private static final String RECENT_DIRECTORY = "RecentDirectory";

	private JXList jList;

	private DefaultListModel<FIXMessageListItem> dataModel;

	private DataDictionary dataDictionary;

	private Preferences preferences = Preferences
			.userNodeForPackage(getClass());

	private class ClearAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ClearAction() {
			super("Clear");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dataModel.clear();
		}
	}

	private class LoadMessageFileAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public LoadMessageFileAction() {
			super("Load");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String recentPath = preferences.get(RECENT_DIRECTORY,
					System.getProperty("user.home"));
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(recentPath));
			chooser.setDialogTitle("Choose a DataDictionary file");
			chooser.setMultiSelectionEnabled(false);
			int selected = chooser.showOpenDialog(FIXMessageListPanel.this);
			if (selected == JFileChooser.APPROVE_OPTION) {
				preferences.put(RECENT_DIRECTORY, chooser.getSelectedFile().getParent());
				File selectedFile = chooser.getSelectedFile();
				try (BufferedReader reader = new BufferedReader(new FileReader(
						selectedFile))) {
					String messageString = reader.readLine();
					while (messageString != null) {
						messageString = FIXProtocol.normalize(messageString);
						try {
							List<FIXField> fixFields = FIXProtocol
									.getInstance().getFields(messageString,
											dataDictionary);
							FIXMessage message = new FIXMessage(fixFields);
							addElement(new FIXMessageListItem(message,
									dataDictionary));
						} catch (ConfigError | IOException ex) {
							ex.printStackTrace();
						}
						messageString = reader.readLine();
					}
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(FIXMessageListPanel.this,
							"An error occurred while attempting to load the message file: "
									+ ex.getMessage(),
							"Unable To Load Message File",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public FIXMessageListPanel() {
		initUI();
	}

	private void initUI() {
		dataModel = new DefaultListModel<FIXMessageListItem>();
		jList = new JXList(dataModel);
		jList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isPopupTrigger()) {
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(jList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JToolBar toolBar = new JToolBar();
		toolBar.add(new ClearAction());
		toolBar.add(new LoadMessageFileAction());
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
