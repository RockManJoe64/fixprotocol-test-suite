package org.fixprotocol.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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

import org.fixprotocol.test.fix.ui.FIXMessageAnalyzePanel;
import org.fixprotocol.test.ui.icon.IconCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FIXClientMain {

	private static final Logger log = LoggerFactory.getLogger(FIXClientMain.class
			.getName());

	private JFrame jframe;

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

		FIXMessageAnalyzePanel fixMsgAnalyzePnl = new FIXMessageAnalyzePanel();

		ImageIcon iconBullet = IconCache.getInstance().getIcon("bullet_blue",
				16);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Analyze", iconBullet, fixMsgAnalyzePnl);

		/*
		 * JFRAME
		 */
		ImageIcon iconFrame = IconCache.getInstance().getIcon("message", 128);

		jframe = new JFrame("FIX Protocol Client");
		jframe.setIconImage(iconFrame.getImage());
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.addWindowListener(fixMsgAnalyzePnl);

		// MENU
		JMenuItem item = new JMenuItem(new ExitAction());
		JMenu menu = new JMenu("File");
		menu.add(item);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);
		jframe.setJMenuBar(menuBar);

		// MAIN CONTAINER
		Container cp = jframe.getContentPane();
		cp.setLayout(new BorderLayout(5, 5));
		cp.add(logoPnl, BorderLayout.NORTH);
		cp.add(tabbedPane);
		cp.add(statusPanel, BorderLayout.SOUTH);

		// frame.pack();
		jframe.setSize(640, 480);
		jframe.setLocationByPlatform(true);
		jframe.setVisible(true);
	}

}
