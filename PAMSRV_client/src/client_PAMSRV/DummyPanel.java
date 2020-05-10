package client_PAMSRV;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/******************************************************************
 * 		Erstellt einen Platzhalter
 * 
 ******************************************************************/
public class DummyPanel extends JPanel {
	private JLabel label;
	private String name;
	
	public DummyPanel(String name, JLabel label) {
		this.name = name;
		this.label = label;
		if (this.name != null) {
			this.setBorder(new TitledBorder(new EtchedBorder(), this.name));
		}
		this.setLayout(new BorderLayout());
		if (this.label != null) {
			this.add(label, BorderLayout.CENTER);
		}
	}//End: Konstruktor
	
}//End: class DummyPanel
