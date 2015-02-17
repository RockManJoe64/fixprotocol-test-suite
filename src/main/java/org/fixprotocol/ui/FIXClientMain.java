package org.fixprotocol.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.fixprotocol.ui.icon.IconCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FIXClientMain {

	private static final Logger log = LoggerFactory.getLogger(FIXClientMain.class
			.getName());
	
	private static final String PREFS_WINDOW_LOCATION = "WindowLocation";

	private static final String PREFS_WINDOW_SIZE = "WindowSize";

	private JFrame jframe;

	private FIXMessageAnalyzePanel fixMsgAnalyzePnl;
	
	private Preferences preferences = Preferences.userNodeForPackage(getClass());

	private FIXMessageClientPanel fixMessageClientPanel;

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
	
	public FIXClientMain() {
		initUI();
	}

	private void initUI() {
		/*
		 * PANELS
		 */
		ImageIcon fixProtocolIcon = IconCache.getInstance().getImage(
				"fixprotocol_logo");
		JLabel fixprotocolLogo = new JLabel(fixProtocolIcon);
		if (Desktop.isDesktopSupported()) {
			fixprotocolLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
			fixprotocolLogo.addMouseListener(new MouseAdapter() {
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
		logoPnl.add(fixprotocolLogo);

		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		statusPanel.add(new JLabel("Ready"));

		fixMsgAnalyzePnl = new FIXMessageAnalyzePanel();
		fixMessageClientPanel = new FIXMessageClientPanel();

		ImageIcon iconBullet = IconCache.getInstance().getIcon("bullet_blue",
				16);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Analyze", iconBullet, fixMsgAnalyzePnl);
		tabbedPane.addTab("Client", this.fixMessageClientPanel);
		
		ImageIcon iconFrame = IconCache.getInstance().getIcon("fixprotocol", 16);
		List<Image> frameIcons = IconCache.getInstance().getIconImageList(
				"fixprotocol", 16, 24, 32, 48);
		
		/*
		 * JFRAME
		 */
		jframe = new JFrame("FIX Protocol Client");
		jframe.setIconImage(iconFrame.getImage());
		jframe.setIconImages(frameIcons);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.addWindowListener(fixMsgAnalyzePnl);
		jframe.addComponentListener(new ResizeWindowListener());
		
		// MENU
		JMenuItem item = new JMenuItem(new ExitAction());
		JMenu menu = new JMenu("File");
		menu.add(item);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);
		jframe.setJMenuBar(menuBar);
		
		JPanel tabPanel = new JPanel(new BorderLayout());
		tabPanel.add(tabbedPane);
		
		// MAIN CONTAINER
		Container cp = jframe.getContentPane();
		cp.setLayout(new BorderLayout(5, 5));
		cp.add(logoPnl, BorderLayout.NORTH);
		cp.add(tabPanel);
		cp.add(statusPanel, BorderLayout.SOUTH);

		initJFrameByPreferences();
		jframe.setVisible(true);
	}
	
	private void initJFrameByPreferences() {
		String sizeStr = preferences.get(PREFS_WINDOW_SIZE, null);
		if (sizeStr != null) {
			String[] tokens = sizeStr.split("\\,");
			jframe.setSize(Integer.valueOf(tokens[0]), Integer.valueOf(tokens[1]));
		} else {
			jframe.setSize(1000, 600);
		}
		String locStr = preferences.get(PREFS_WINDOW_LOCATION, null);
		if (locStr != null) {
			String[] tokens = locStr.split("\\,");
			jframe.setLocation(Integer.valueOf(tokens[0]), Integer.valueOf(tokens[1]));
		} else {
			jframe.setLocationByPlatform(true);
		}
	}

	private class ResizeWindowListener extends ComponentAdapter {
		
		@Override
		public void componentResized(ComponentEvent e) {
			Dimension size = jframe.getSize();
			String sizeStr = String.valueOf(size.width) + ","
					+ String.valueOf(size.height);
			preferences.put(PREFS_WINDOW_SIZE, sizeStr);
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			Point location = jframe.getLocation();
			String locStr = String.valueOf(location.x) + ","
					+ String.valueOf(location.y);
			preferences.put(PREFS_WINDOW_LOCATION, locStr);
		}
		
	}

}
