package org.fixprotocol.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.fixprotocol.fix.LogParser;

public class LoadLogPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JTextField txtDelim;
	private JTextField txtFieldNum;
	
	private File selectedFile;
	
	private final Observable observable = new Observable();
	
	class OKAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (selectedFile != null) {
				LogParser parser = new LogParser(selectedFile);
				parser.parse();
				observable.notifyObservers();				
			}
		}
	}
	
	class SelectFileAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SelectFileAction() {
			super("Select File");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Choose a FIX message log");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setMultiSelectionEnabled(false);
			int value = chooser.showOpenDialog(LoadLogPanel.this);
			if (value == JFileChooser.APPROVE_OPTION) {
				selectedFile = chooser.getSelectedFile();
			}
		}
	}

	public LoadLogPanel() {
		initUI();
	}

	private void initUI() {
		new JButton(new SelectFileAction());
		new JButton(new OKAction());
		txtDelim = new JTextField(" ", 2);
		txtFieldNum = new JTextField(3);
		setLayout(new FlowLayout());
	}
	
	public void addObserver(Observer o) {
		observable.addObserver(o);
	}

	public void deleteObserver(Observer o) {
		observable.deleteObserver(o);
	}
	
}
