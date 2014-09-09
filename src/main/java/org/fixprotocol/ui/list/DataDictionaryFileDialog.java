package org.fixprotocol.ui.list;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;

import org.fixprotocol.fix.DataDictionaries;
import org.fixprotocol.ui.event.DataDictionaryEvent;
import org.fixprotocol.ui.event.DataDictionaryListener;
import org.fixprotocol.ui.icon.IconCache;
import org.fixprotocol.ui.model.DataDictionaryFile;

import quickfix.ConfigError;
import quickfix.DataDictionary;

/**
 * @author Jose Chavez <chavez.j@eoxlive.com>
 * @since Sep 9, 2014
 */
public class DataDictionaryFileDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private static final int MAX_PATH_SIZE = 52;
	
	private static final String FILE_LIST = "FileList";

	private static final String RECENT_DIRECTORY = "RecentDirectory";
	
	private DataDictionaries dictionaries = new DataDictionaries();

	private Preferences preferences = Preferences.userNodeForPackage(getClass());

	private DictionaryListModel listModel;
	
	private EventListenerList listenerList = new EventListenerList();
	
	private DataDictionaryFile selectedDictionary;
	
	private class AddDataDictionaryAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddDataDictionaryAction() {
			super("Add");
			putValue(SHORT_DESCRIPTION, "Add a DataDictionary");
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			String recentPath = preferences.get(RECENT_DIRECTORY,
					System.getProperty("user.home"));
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(recentPath));
			chooser.setDialogTitle("Choose a DataDictionary file");
			chooser.setMultiSelectionEnabled(false);
			chooser.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return "QuickFIX DataDictionary (*.xml)";
				}

				@Override
				public boolean accept(File f) {
					return f.isDirectory()
							|| f.getName().toLowerCase().endsWith(".xml");
				}
			});
			int selected = chooser.showOpenDialog(DataDictionaryFileDialog.this);
			if (selected == JFileChooser.APPROVE_OPTION) {
				String name = JOptionPane.showInputDialog(
						DataDictionaryFileDialog.this,
						"What is the name for this data dictionary?");
				String absolutePath = chooser.getSelectedFile()
						.getAbsolutePath();
				preferences.put(RECENT_DIRECTORY, chooser.getSelectedFile().getParent());
				try {
					File file = new File(absolutePath);
					dictionaries.loadDictionary(name, file);
					DataDictionary dd = dictionaries.getDataDictionary(name);
					listModel.addElement(new DataDictionaryFile(name,
							dd, file));
					listModel.persistDictionaries();
					fireDataDictionaryAdded(dd);
				} catch (ConfigError | IOException e) {
					JOptionPane.showMessageDialog(DataDictionaryFileDialog.this,
							"An error occurred while attempting to load the data dictionary: "
									+ e.getMessage(),
							"Unable To Load DataDictionary",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	
	
	public DataDictionaryFileDialog(Window owner, String title,
			ModalityType modalityType) {
		super(owner, title, modalityType);
		initUI();
	}

	private void initUI() {
		final ImageIcon fileIcon = IconCache.getInstance().getIcon("file", 20);
		final ImageIcon checkIcon = IconCache.getInstance().getIcon("square-check", 20);
		
		listModel = new DictionaryListModel();
		final JList<DataDictionaryFile> jlist = new JList<DataDictionaryFile>(
				listModel);
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlist.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index,
						isSelected, cellHasFocus);
				DataDictionaryFile item = (DataDictionaryFile) value;
				String name = item.getName();
				String path = item.getFile().getAbsolutePath();
				String filename = item.getFile().getName();
				if (path.length() > MAX_PATH_SIZE) {
					path = path.substring(0, MAX_PATH_SIZE) + ".." + System.getProperty("file.separator");
				}
				setText(name + " (" + path + filename + ")");
				setFont(getFont().deriveFont(14.0f));
				setToolTipText(item.getFile().getAbsolutePath());
				if (item.equals(selectedDictionary))
					setIcon(checkIcon);
				else 
					setIcon(fileIcon);
				return this;
			}
		});
		JButton addDictButton = new JButton(new AddDataDictionaryAction());
		JButton removeButton = new JButton(new AbstractAction("Remove") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				DataDictionaryFile value = jlist.getSelectedValue();
				if (value != null) {
					listModel.removeElement(value);
					fireDataDictionaryRemoved(value.getDataDictionary());
				}
			}
		});
		JButton selectButton = new JButton(new AbstractAction("Select") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				handleSelection(jlist);
			}
		});
		jlist.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					handleSelection(jlist);
				}
			}
		});
		
		JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		btnPnl.add(addDictButton);
		btnPnl.add(removeButton);
		btnPnl.add(selectButton);
		
		JScrollPane scrollPane = new JScrollPane(jlist);
		JPanel mainPnl = new JPanel();
		mainPnl.setLayout(new BorderLayout());
		mainPnl.add(scrollPane);
		mainPnl.add(btnPnl, BorderLayout.SOUTH);
		
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(mainPnl);
	}
	
	private void handleSelection(final JList<DataDictionaryFile> jlist) {
		DataDictionaryFile value = jlist.getSelectedValue();
		if (value != null) {
			setSelectedDictionary(value);
			fireDataDictionarySelected(value.getDataDictionary());
			jlist.repaint();
		}
	}

	public DataDictionary getSelectedDictionary() {
		return this.selectedDictionary.getDataDictionary();
	}
	
	private void setSelectedDictionary(DataDictionaryFile selectedDictionary) {
		this.selectedDictionary = selectedDictionary;
	}
	
	public void setDictionaries(DataDictionaries dictionaries) {
		this.dictionaries = dictionaries;
	}
	
	public void addDataDictionaryListener(DataDictionaryListener l) {
		this.listenerList.add(DataDictionaryListener.class, l);
	}
	
	public void removeDataDictionaryListener(DataDictionaryListener l) {
		this.listenerList.remove(DataDictionaryListener.class, l);
	}
	
	protected void fireDataDictionaryAdded(DataDictionary dd) {
		DataDictionaryListener[] listeners = this.listenerList.getListeners(DataDictionaryListener.class);
		for (DataDictionaryListener l : listeners) {
			l.dataDictionaryAdded(new DataDictionaryEvent(this, dd));
		}
	}
	
	protected void fireDataDictionaryRemoved(DataDictionary dd) {
		DataDictionaryListener[] listeners = this.listenerList.getListeners(DataDictionaryListener.class);
		for (DataDictionaryListener l : listeners) {
			l.dataDictionaryRemoved(new DataDictionaryEvent(this, dd));
		}
	}
	
	protected void fireDataDictionarySelected(DataDictionary dd) {
		DataDictionaryListener[] listeners = this.listenerList.getListeners(DataDictionaryListener.class);
		for (DataDictionaryListener l : listeners) {
			l.dataDictionarySelected(new DataDictionaryEvent(this, dd));
		}
	}
	
	private class DictionaryListModel extends DefaultListModel<DataDictionaryFile> {

		private static final long serialVersionUID = 1L;

		public DictionaryListModel() {
			super();
			loadDictionaries();
		}
		
		private void loadDictionaries() {
			String fileList = preferences.get(FILE_LIST, null);
			if (fileList == null || "".equals(fileList)) 
				return;
			String[] tokens = fileList.split("\\|");
			for (String string : tokens) {
				String[] kv = string.split("\\=");
				String name = kv[0];
				String filePath = kv[1];
				try {
					DataDictionaryFile item = DataDictionaryFile.load(name,
							new File(filePath));
					this.addElement(item);
				} catch (FileNotFoundException | ConfigError e) {
					System.err.println("Could not load dictionary: " + filePath);
				}
			}
		}
		
		public void persistDictionaries() {
			StringBuilder sb = new StringBuilder();
			for (int index = 0; index < this.getSize(); index++) {
				DataDictionaryFile dictionaryFile = this.getElementAt(index);
				sb.append(dictionaryFile.getName() + "="
						+ dictionaryFile.getFile().getAbsolutePath() + "|");
			}
			preferences.put(FILE_LIST, sb.toString());
		}
		
	}

}
