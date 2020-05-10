package client_PAMSRV;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/******************************************************************
 * 		Erstellt ein JPanel zur Navigation mit Buttons etc
 * 
 ******************************************************************/
public class PAMSRV_view_navpanel extends JPanel {
	private PAMSRV_model model;
	private Timer t;
	
	private JPanel buttonPanel;
	private JButton forwardBtn;
	private JButton backwardBtn;
	private JButton turnLeftBtn;
	private JButton turnRightBtn;
	private JButton striveLeftBtn;
	private JButton striveRightBtn;
	private JButton stopBtn;
	private JSlider speedSlider;
	private JPanel imuPanel;
	private JPanel vectorPanel;
	private JLabel headingLbl;
	private JTextField headingTxt;
	private JLabel rollLbl;
	private JTextField rollTxt;
	private JLabel pitchLbl;
	private JTextField pitchTxt;
	
	public PAMSRV_view_navpanel(PAMSRV_model model) {
		this.model = model;
		
		this.setBorder(new TitledBorder(new EtchedBorder(), "Navigation"));
		this.setLayout(new GridLayout(3, 1));
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 3));
		ButtonListener bl = new ButtonListener();
		forwardBtn = new JButton("Forward");
		forwardBtn.addActionListener(bl);
		backwardBtn = new JButton("Backward");
		backwardBtn.addActionListener(bl);
		turnLeftBtn = new JButton("Leftturn");
		turnLeftBtn.addActionListener(bl);
		turnRightBtn = new JButton("Rightturn");
		turnRightBtn.addActionListener(bl);
		striveLeftBtn = new JButton("Strive Left");
		striveLeftBtn.addActionListener(bl);
		striveRightBtn = new JButton("Strive Right");
		striveRightBtn.addActionListener(bl);
		stopBtn = new JButton("Stop");
		stopBtn.addActionListener(bl);
		buttonPanel.add(turnLeftBtn);
		buttonPanel.add(forwardBtn);
		buttonPanel.add(turnRightBtn);
		buttonPanel.add(striveLeftBtn);
		buttonPanel.add(stopBtn);
		buttonPanel.add(striveRightBtn);
		buttonPanel.add(new DummyPanel(null, null));
		buttonPanel.add(backwardBtn);
		buttonPanel.add(new DummyPanel(null, null));
		this.add(buttonPanel);
		
		speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 100);
		speedSlider.setMajorTickSpacing(25);
		speedSlider.setMinorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);
		speedSlider.setBorder(new TitledBorder(new EtchedBorder(), "Speed"));
		SliderListener sl = new SliderListener();
		speedSlider.addChangeListener(sl);
		this.add(speedSlider);
		
		imuPanel = new JPanel();
		imuPanel.setBorder(new TitledBorder(new EtchedBorder(), "Inertial Measurement Unit"));
		imuPanel.setLayout(new GridLayout(1, 3));
		vectorPanel = new JPanel();
		vectorPanel.setLayout(new GridLayout(3, 2));
		headingLbl = new JLabel("Heading");
		headingLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		headingTxt = new JTextField(10);
		headingTxt.setEditable(false);
		headingTxt.setHorizontalAlignment(SwingConstants.RIGHT);
		headingTxt.setBackground(Color.WHITE);
		rollLbl = new JLabel("Roll");
		rollLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		rollTxt = new JTextField(10);
		rollTxt.setEditable(false);
		rollTxt.setHorizontalAlignment(SwingConstants.RIGHT);
		rollTxt.setBackground(Color.WHITE);
		pitchLbl = new JLabel("Pitch");
		pitchLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		pitchTxt = new JTextField(10);
		pitchTxt.setEditable(false);
		pitchTxt.setHorizontalAlignment(SwingConstants.RIGHT);
		pitchTxt.setBackground(Color.WHITE);
		vectorPanel.add(headingLbl);
		vectorPanel.add(headingTxt);
		vectorPanel.add(rollLbl);
		vectorPanel.add(rollTxt);
		vectorPanel.add(pitchLbl);
		vectorPanel.add(pitchTxt);
		imuPanel.add(vectorPanel);
		this.add(imuPanel);
		
		t = new Timer(200, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PAMSRV_view_navpanel.this.repaint();
			}
		});
		t.start();
		
		model.debugPrint("SystemPanel gestartet");
		
		model.debugPrint("NavPanel gestartet.");
	}//End: Konstruktor
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		updateVector();
	}//End: paintComponent
	
	private void updateVector() {
		if (model.isIMUStarted()) {
			headingTxt.setText(String.valueOf(model.imu_getHeading()));
			rollTxt.setText(String.valueOf(model.imu_getRoll()));
			pitchTxt.setText(String.valueOf(model.imu_getPitch()));
		} else {
			headingTxt.setText("N/A");
			rollTxt.setText("N/A");
			pitchTxt.setText("N/A");
		}
	}
	
	class SliderListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent evt) {
			JSlider source = (JSlider)evt.getSource();
			if (!source.getValueIsAdjusting()) {
				model.nav_setSpeedValue(source.getValue());
			}
		}//End: stateCHenged()
	}//End: INNER class SliderListener
	
	class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			String s = "";
			if (evt.getSource()==stopBtn) {
				s = "move:stop";
			}
			if (evt.getSource()==forwardBtn) {
				s = "move:forward:";
			}
			if (evt.getSource()==backwardBtn) {
				s = "move:backward:";
			}
			if (evt.getSource()==striveLeftBtn) {
				s = "move:left:";
			}
			if (evt.getSource()==striveRightBtn) {
				s = "move:right:";
			}
			if (evt.getSource()==turnLeftBtn) {
				s = "move:ccwrotationaboutmasscenter:";
			}
			if (evt.getSource()==turnRightBtn) {
				s = "move:cwrotationaboutmasscenter:";
			}
			s = s + model.nav_getSpeedValue();
		}//End: ActionPerformed
	}//End: INNER class ButtonListener

}//End: class NavPanel
