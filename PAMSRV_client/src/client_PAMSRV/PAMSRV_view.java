package client_PAMSRV;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


/******************************************************************
 * 		Enth‰lt weder Daten noch Logik, nur Darstellung
 ******************************************************************/
public class PAMSRV_view extends JFrame {
	private PAMSRV_controller controller;
	private PAMSRV_model model;
	private String title;
	
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem closeMenuItem;
	private JMenu connectionMenu;
	private JMenuItem connectMenuItem;
	private JMenuItem disconnectMenuItem;
	private JMenu settingsMenu;
	private JCheckBoxMenuItem debugModeMenuItem;
	
	private JPanel topContainer;
	private PAMSRV_view_lidarpanel lidarPanel;
	private PAMSRV_view_logpanel logPanel;
	private DummyPanel cameraPanel;
	private PAMSRV_view_navpanel navPanel;
	private JPanel centerPanel;
	private PAMSRV_view_systempanel sysPanel;
	
	/**********************************
	 * 		Konstruktor
	 **********************************/
	public PAMSRV_view(PAMSRV_controller controller, PAMSRV_model model, String name) {
		this.controller = controller;
		this.model = model;
		this.title = name;
	}//End: Konstruktor
	
	//erzeugt das Fenster
	public void init() {
		/**********************************
		 * 		MENUBAR
		 **********************************/
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		openMenuItem = new JMenuItem("Open");
		saveMenuItem = new JMenuItem("Save");
		closeMenuItem = new JMenuItem("Close");
		connectionMenu = new JMenu("Connection");
		connectMenuItem = new JMenuItem("Connect");
		disconnectMenuItem = new JMenuItem("Disconnect");
		settingsMenu = new JMenu("Settings");
		debugModeMenuItem = new JCheckBoxMenuItem("Debug Mode", model.getDebugState());
		
		SettingsMenuListener sml = new SettingsMenuListener();
		debugModeMenuItem.addActionListener(sml);
		
		FileMenuListener ml = new FileMenuListener();
		openMenuItem.addActionListener(ml);
		saveMenuItem.addActionListener(ml);
		closeMenuItem.addActionListener(ml);
		
		ConnectionMenuListener cml = new ConnectionMenuListener();
		connectMenuItem.addActionListener(cml);
		disconnectMenuItem.addActionListener(cml);
		
		menuBar.add(fileMenu);
		fileMenu.add(openMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(closeMenuItem);

		menuBar.add(connectionMenu);
		connectionMenu.add(connectMenuItem);
		connectionMenu.add(disconnectMenuItem);
		
		menuBar.add(settingsMenu);
		settingsMenu.add(debugModeMenuItem);
		
		this.setJMenuBar(menuBar);
		/**********************************
		 * 		WINDOW
		 **********************************/
		topContainer = new JPanel();
		topContainer.setLayout(new BorderLayout());
		
		logPanel = new PAMSRV_view_logpanel(model);
		topContainer.add(logPanel, BorderLayout.SOUTH);
		
		centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(1, 3));
		
		cameraPanel = new DummyPanel("Camera", new JLabel("<html><body>Raspberry Pi Camera<br>mit Motion oder OpenCV</body></html>", SwingConstants.CENTER));
		centerPanel.add(cameraPanel);
		
		navPanel = new PAMSRV_view_navpanel(model);
		centerPanel.add(navPanel);
		
		lidarPanel = new PAMSRV_view_lidarpanel(model);
		centerPanel.add(lidarPanel);
		topContainer.add(centerPanel, BorderLayout.CENTER);
		
		sysPanel = new PAMSRV_view_systempanel(controller, model);
		topContainer.add(sysPanel, BorderLayout.NORTH);
		
		this.add(topContainer);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setTitle(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		//logPanel.addLogEntry("GUI geladen");
	}//End: init()
	
	/**********************************
	 * 		INNER CLASSES
	 **********************************/
	class SettingsMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			model.setDebugState(debugModeMenuItem.getState());
			if (model.getDebugState()==true) {
				model.log_addNewLogEntry("Debug Modus aktiviert");
			} else {
				model.log_addNewLogEntry("Debug Modus deaktiviert");
			}
		}//End: actionPerformed
	}//End: INNER class SettingsMenuListener
	
	class FileMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (evt.getSource()==closeMenuItem) {
				int input = JOptionPane.showConfirmDialog(null, "Wirklich beenden?", "Beenden", JOptionPane.YES_NO_OPTION);
				// input:	0 = ja;   1 = nein;
				if (input==0) {
					System.exit(0);
				}
				else {
					//Do nothing
				}
			}
			else {
				if (evt.getSource()==openMenuItem) {
					String filename = "";
					ArrayList<String> settings = new ArrayList<String>();
					FileReader fr = null;
					Scanner in = null;
					JFileChooser fc = new JFileChooser();
					int result = fc.showOpenDialog(menuBar);
					if (result == JFileChooser.APPROVE_OPTION) {
						filename = fc.getSelectedFile().getPath();
						try {
							fr = new FileReader(filename);
							in = new Scanner(fr);
							while(in.hasNextLine()) {
								settings.add(in.nextLine());
							}
							//	TODO	//////////////////////////////////////////////////////////////////////////////////
							//			ArrayList an Fenster, Model und log ¸bergeben
							//			Fenster neu Laden
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Die Datei konnte nicht geˆffnet werden!", "Fehler", JOptionPane.QUESTION_MESSAGE);
							model.debugPrint("Datei konnte nicht geoeffnet werden");
						}
					}
				}
				else {
					String filename = "";
					PrintWriter pw = null;
					JFileChooser fc = new JFileChooser();
					int result = fc.showSaveDialog(menuBar);
					if (result == JFileChooser.APPROVE_OPTION) {
						filename = fc.getSelectedFile().getPath();
						try {
							pw = new PrintWriter(filename);
							// TODO		//////////////////////////////////////////////////////////////////////////////////
							//			String saveFile = (Wert1) + ";" + (Wert2) + ";" + ...
							//			pw.print(saveFile);
							pw.close();
						} catch (FileNotFoundException e) {
							JOptionPane.showMessageDialog(null, "Die Datei wurde nicht gefunden!", "Fehler", JOptionPane.QUESTION_MESSAGE);
						}
					}
				}
			}
		}//End actionPerformed
	}//End INNER class MenuListener
	
	class ConnectionMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent cme) {
			if (cme.getSource()==connectMenuItem) {
				if (model.wifi_getConnectionStatus()==false) {
					String address = JOptionPane.showInputDialog(null, "Bitte Server IP-Addresse eingeben!", 
			                "IP Addresse", JOptionPane.INFORMATION_MESSAGE);
					if (address != null && address.length()>0) {
						
						if ( address.contains(":") ) {
							String[] addr = new String[2];
							addr = address.split(":");
							model.wifi_setServerIP(addr[0]);
							model.wifi_setServerPort(Integer.parseInt(addr[1]));
						} else {
							model.wifi_setServerIP(address);
							//Default Port: 5555
						}
					}
					controller.connectToServer();
				} else {
					logPanel.addLogEntry("Verbidung bereits vorhanden!");
				} 
			} else {//Source = Disconnect
				//TODO Verbindung schlieﬂen
			}
		}//End: actionPerformed
	}//End: INNER class ConnectionMenuListener
	
	
}
