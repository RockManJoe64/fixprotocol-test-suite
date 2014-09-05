package org.fixprotocol.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import org.fixprotocol.fix.DataDictionaries;
import org.fixprotocol.ui.icon.IconCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.ConfigError;

public class FIXClientMain {

	private static final Logger log = LoggerFactory.getLogger(FIXClientMain.class
			.getName());

	private JFrame jframe;
	private DataDictionaries dictionaries = new DataDictionaries();

	private FIXMessageAnalyzePanel fixMsgAnalyzePnl;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					log.error(e.getMessage(), e);
				}

				new FIXClientMain();

				log.info("Client started");
			}
		});
	}

	class ExitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ExitAction() {
			super("Exit");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ESCAPE"));
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			int option = JOptionPane.showConfirmDialog(jframe,
					"Are you sure you want to exit?", "Closing",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (option == JOptionPane.YES_OPTION) {
				jframe.dispose();
			}
		}
	}
	
	class SetDataDictionaryAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SetDataDictionaryAction() {
			super("Set DataDictionary");
			putValue(LARGE_ICON_KEY, IconCache.getInstance().getIcon("dictionary", 24));
			putValue(SMALL_ICON, IconCache.getInstance().getIcon("dictionary", 16));
			putValue(SHORT_DESCRIPTION, "Set the DataDictionary");
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Choose a DataDictionary file");
			chooser.setMultiSelectionEnabled(false);
			chooser.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return "QuickFIX DataDictionary (*.xml)";
				}
				
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
				}
			});
			int selected = chooser.showOpenDialog(jframe);
			if (selected == JFileChooser.APPROVE_OPTION) {
				String name = JOptionPane.showInputDialog(jframe, "What is the name for this data dictionary?");
				String absolutePath = chooser.getSelectedFile().getAbsolutePath();
				try {
					dictionaries.loadDictionary(name, new File(absolutePath));
					fixMsgAnalyzePnl.setDataDictionary(dictionaries.getDataDictionary(name));
				} catch (ConfigError | IOException e) {
					JOptionPane.showMessageDialog(jframe,
							"An error occurred while attempting to load the data dictionary: "
									+ e.getMessage(),
							"Unable To Load DataDictionary",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public FIXClientMain() {
		initUI();
	}

	private void initUI() {
		/*
		 * PANELS
		 */
		ImageIcon iconLogo = IconCache.getInstance().getImage(
				"fixprotocol_logo");
		JLabel lblLogo = new JLabel(iconLogo);
		if (Desktop.isDesktopSupported()) {
			lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
			lblLogo.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 0) {
						try {
							Desktop.getDesktop()
									.browse(new URI(
											"http://www.fixtradingcommunity.org/FIXimate/FIXimate3.0/"));
						} catch (IOException | URISyntaxException ex) {
							log.warn("Could not browse URL", ex);
						}
					}
				}
			});
		}

		JPanel logoPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		logoPnl.setBackground(Color.WHITE);
		logoPnl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		logoPnl.add(lblLogo);

		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		statusPanel.add(new JLabel("Ready"));

		fixMsgAnalyzePnl = new FIXMessageAnalyzePanel();

		ImageIcon iconBullet = IconCache.getInstance().getIcon("bullet_blue",
				16);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Analyze", iconBullet, fixMsgAnalyzePnl);
		
		/*
		 * JFRAME
		 */
		ImageIcon iconFrame = IconCache.getInstance().getIcon("fixprotocol", 16);
		List<Image> iconList = IconCache.getInstance().getIconImageList(
				"fixprotocol", 16, 24, 32, 48);

		jframe = new JFrame("FIX Protocol Client");
		jframe.setIconImage(iconFrame.getImage());
		jframe.setIconImages(iconList);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.addWindowListener(fixMsgAnalyzePnl);

		// MENU
		JMenuItem item = new JMenuItem(new ExitAction());
		JMenu menu = new JMenu("File");
		menu.add(item);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);
		jframe.setJMenuBar(menuBar);
		
		// TOOLBAR
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(new SetDataDictionaryAction());
		
		JPanel tabPanel = new JPanel(new BorderLayout());
		tabPanel.add(tabbedPane);
		tabPanel.add(toolBar, BorderLayout.NORTH);

		// MAIN CONTAINER
		Container cp = jframe.getContentPane();
		cp.setLayout(new BorderLayout(5, 5));
		cp.add(logoPnl, BorderLayout.NORTH);
		cp.add(tabPanel);
		cp.add(statusPanel, BorderLayout.SOUTH);

		// frame.pack();
		jframe.setSize(640, 480);
		jframe.setLocationByPlatform(true);
		jframe.setVisible(true);
	}

}
