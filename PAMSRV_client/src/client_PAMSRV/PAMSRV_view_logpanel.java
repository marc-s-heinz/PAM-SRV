package client_PAMSRV;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/******************************************************************
 * 		erstellt ein panel das systemnachrichten des clients anzeigt
 * 
 ******************************************************************/
public class PAMSRV_view_logpanel extends JPanel {
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private PAMSRV_model model;
	
	public PAMSRV_view_logpanel(PAMSRV_model model) {
		this.model = model;
		this.setLayout(new GridLayout(1, 1));
		this.setBorder(new TitledBorder(new EtchedBorder(), "Log"));
		
		textArea = new JTextArea(10, 25);
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);
		this.add(scrollPane);
		
		LogThread lt = new LogThread();
		lt.start();
	}//End: Konstruktor
	
	public void addLogEntry(String logtxt) {
		textArea.append(model.getCurrentTime() + "\t" + logtxt + "\n");
	}//End: addLogEntry()
	
	class LogThread extends Thread {
		public void run() {
			model.debugPrint("Log Thread gestartet.");
			while (true) {
				if (model.log_getSize() != 0) {
					textArea.append(model.log_getOldestLogEntry());
				}
			}
		}//End: run()
	}//End: INNER class LogThread

}//End: class LogPanel
