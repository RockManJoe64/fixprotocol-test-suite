/**
 * Copyright (c) 2014, EOX Live LLC and/or its affiliates. All rights reserved.
 */
package org.fixprotocol.ui.table;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import org.fixprotocol.fix.FIXField;
import org.fixprotocol.fix.FIXMessage;
import org.fixprotocol.fix.FIXProtocol;
import org.fixprotocol.ui.DataDictionaryAware;
import org.fixprotocol.ui.UIColors;
import org.fixprotocol.ui.event.FIXMessageEvent;
import org.fixprotocol.ui.event.FIXMessageListener;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.field.BeginString;
import quickfix.field.SenderCompID;
import quickfix.field.SenderSubID;
import quickfix.field.TargetCompID;
import quickfix.field.TargetSubID;

/**
 * @author Jose Chavez <chavez.j@eoxlive.com>
 * @since Sep 11, 2014
 */
public class FIXMessageMetaTablePanel extends JPanel implements FIXMessageListener, DataDictionaryAware {

	private static final long serialVersionUID = 1L;
	
	private static final String RECENT_DIRECTORY = "RecentDirectory";
	
	private JXTable jTable;
	
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
			int option = JOptionPane.showConfirmDialog(FIXMessageMetaTablePanel.this,
					"Do you really want to clear all messages from the screen?", 
					"Message Clear",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				FIXMessageMetaTableModel model = (FIXMessageMetaTableModel) jTable.getModel();
				model.clear();				
			}
		}
	}
	
	private class DeleteAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public DeleteAction() {
			super("Delete");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedRow = jTable.getSelectedRow();
			if (selectedRow < 0) {
				JOptionPane.showMessageDialog(FIXMessageMetaTablePanel.this,
						"Please select a message to delete",
						"Message Selection", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			int option = JOptionPane.showConfirmDialog(FIXMessageMetaTablePanel.this,
					"Do you really want delete the selected message?", 
					"Message Delete",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				FIXMessageMetaTableModel model = (FIXMessageMetaTableModel) jTable.getModel();
				model.remove(jTable.convertRowIndexToModel(selectedRow));				
			}
		}
	}
	
	private class CreateSessionIDAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public CreateSessionIDAction() {
			super("Create SessionID");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			FIXMessage selectedValue = getSelectedValue();
			if (selectedValue == null) return;
			StringBuffer sb = new StringBuffer();
			sb.append(selectedValue.get(BeginString.FIELD).getValue().toString())
				.append(":")
				.append(selectedValue.get(SenderCompID.FIELD).getValue().toString());
			if (selectedValue.has(SenderSubID.FIELD))
				sb.append("/")
					.append(selectedValue.get(SenderSubID.FIELD).getValue().toString());
			sb.append("->")
				.append(selectedValue.get(TargetCompID.FIELD).getValue().toString());
			if (selectedValue.has(TargetSubID.FIELD))
				sb.append("/")
					.append(selectedValue.get(TargetSubID.FIELD).getValue().toString());
			JTextField field = new JTextField(sb.toString());
			JDialog dialog = new JDialog(
					SwingUtilities
							.getWindowAncestor(FIXMessageMetaTablePanel.this),
					"SessionID", ModalityType.APPLICATION_MODAL);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			Container contentPane = dialog.getContentPane();
			contentPane.setLayout(new GridLayout(1, 1));
			contentPane.add(field);
			dialog.pack();
			dialog.setLocationRelativeTo(FIXMessageMetaTablePanel.this);
			dialog.setVisible(true);
		}
	}
	
	private class SaveMessageFileAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SaveMessageFileAction() {
			super("Save");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String recentPath = preferences.get(RECENT_DIRECTORY,
					System.getProperty("user.home"));
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(recentPath));
			chooser.setDialogTitle("Save Messages to File");
			chooser.setMultiSelectionEnabled(false);
			int selected = chooser.showSaveDialog(FIXMessageMetaTablePanel.this);
			if (selected == JFileChooser.APPROVE_OPTION) {
				File selectedFile = chooser.getSelectedFile();
				preferences.put(RECENT_DIRECTORY, selectedFile.getParent());
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(
						selectedFile))) {
					FIXMessageMetaTableModel model = (FIXMessageMetaTableModel) jTable.getModel();
					int rowCount = model.getRowCount();
					for (int index = 0; index < rowCount; index++) {
						writer.write(model.getFIXMessageAt(index).toString());
						writer.newLine();
					}
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(FIXMessageMetaTablePanel.this,
							"An error occurred while attempting to save the message file: "
									+ ex.getMessage(),
							"Unable To Save Message File",
							JOptionPane.ERROR_MESSAGE);
				}
			}
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
			chooser.setDialogTitle("Load Messages from File");
			chooser.setMultiSelectionEnabled(false);
			int selected = chooser.showOpenDialog(FIXMessageMetaTablePanel.this);
			if (selected == JFileChooser.APPROVE_OPTION) {
				File selectedFile = chooser.getSelectedFile();
				preferences.put(RECENT_DIRECTORY, selectedFile.getParent());
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
							addFIXMessage(message);
						} catch (ConfigError | IOException ex) {
							ex.printStackTrace();
						}
						messageString = reader.readLine();
					}
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(FIXMessageMetaTablePanel.this,
							"An error occurred while attempting to load the message file: "
									+ ex.getMessage(),
							"Unable To Load Message File",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	public FIXMessageMetaTablePanel() {
		initUI();
	}

	private void initUI() {
        jTable = new JXTable(new FIXMessageMetaTableModel());
        jTable.setAutoscrolls(false);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable.setFont(new Font("Consolas", Font.PLAIN, 12));
        jTable.addHighlighter(HighlighterFactory.createSimpleStriping(UIColors.BEIGE));
		jTable.setSortOrderCycle(new SortOrder[] { 
				SortOrder.ASCENDING,
				SortOrder.DESCENDING, 
				SortOrder.UNSORTED });
		
		JMenuItem menuItem = new JMenuItem(new CreateSessionIDAction());
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(menuItem);
		jTable.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(jTable);
        
        JToolBar toolBar = new JToolBar();
        toolBar.add(new DeleteAction());
		toolBar.add(new ClearAction());
		toolBar.add(new JToolBar.Separator());
		toolBar.add(new LoadMessageFileAction());
		toolBar.add(new SaveMessageFileAction());
		toolBar.setFloatable(false);

		setLayout(new BorderLayout(5, 5));
		add(toolBar, BorderLayout.NORTH);
		add(scrollPane);
	}
	
	public void setData(List<FIXMessage> data) {
		FIXMessageMetaTableModel model = (FIXMessageMetaTableModel) jTable.getModel();
        model.update(data);
    }

    public void setModel(TableModel dataModel) {
        if (dataModel instanceof FIXMessageMetaTableModel) {
            jTable.setModel(dataModel);
        } else {
            throw new IllegalArgumentException("TableModel not of type FIXMessageMetaTableModel");
        }
    }
    
    public FIXMessage getSelectedValue() {
    	int selectedRow = jTable.getSelectedRow();
    	if (selectedRow < 0) return FIXMessage.UNDEFINED;
		return ((FIXMessageMetaTableModel) jTable.getModel())
				.getFIXMessageAt(jTable.convertRowIndexToModel(selectedRow));
    }
    
    @Override
	public DataDictionary getDataDictionary() {
		return dataDictionary;
	}

	@Override
	public void setDataDictionary(DataDictionary dd) {
		this.dataDictionary = dd;
	}
    
    public void addListSelectionListener(ListSelectionListener l) {
    	jTable.getSelectionModel().addListSelectionListener(l);
    }
    
    public void removeListSelectionListener(ListSelectionListener l) {
    	jTable.getSelectionModel().removeListSelectionListener(l);    	
    }
    
    protected void addFIXMessage(FIXMessage message) {
    	FIXMessageMetaTableModel model = (FIXMessageMetaTableModel) jTable.getModel();
		model.add(message);
    }
    
	@Override
	public void fixMessageAdded(FIXMessageEvent e) {
	}

	@Override
	public void fixMessageUpdated(FIXMessageEvent e) {
		addFIXMessage(e.getFIXMessage());
	}

	@Override
	public void fixMessageRemoved(FIXMessageEvent e) {
	}

}
