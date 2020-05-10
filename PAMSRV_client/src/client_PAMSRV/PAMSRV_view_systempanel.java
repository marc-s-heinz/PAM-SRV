package client_PAMSRV;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/******************************************************************
 * 		Erstellt ein Panel mit den wichtigsten Daten
 * 
 ******************************************************************/
public class PAMSRV_view_systempanel extends JPanel {
	private PAMSRV_controller controller;
	private PAMSRV_model model;
	private Timer t;
	
	private JPanel wifiPanel;
	private JLabel wifiLabel;
	private JTextField wifiTextField;
	
	private JPanel sysBtnPanel;
	private JButton sBiteBtn;
	private JButton calibrateBtn;
	
	private JPanel imuCalibrationPanel;
	private JLabel sysCalLabel;
	private JTextField sysCalTextField;
	private JLabel magCalLabel;
	private JTextField magCalTextField;
	private JLabel gyroCalLabel;
	private JTextField gyroCalTextField;
	private JLabel accelCalLabel;
	private JTextField accelCalTextField;
	
	private JPanel batteryPanel;
	private JLabel batteryVoltageLabel;
	private JTextField voltageTextField;
	private JLabel batteryCurrentLabel;
	private JTextField currentTextField;

	private JPanel rpmMotorPanel;
	private JLabel rpmMotorALabel;
	private JTextField rpmMotorATextField;
	private JLabel rpmMotorBLabel;
	private JTextField rpmMotorBTextField;
	private JLabel rpmMotorCLabel;
	private JTextField rpmMotorCTextField;
	private JLabel rpmMotorDLabel;
	private JTextField rpmMotorDTextField;
	
	private JPanel devicePanel;
	private JLabel imuLabel;
	private ToggleSwitch imuSwitch;
	private JLabel motorLabel;
	private ToggleSwitch motorSwitch;
	private JLabel lidarLabel;
	private ToggleSwitch lidarSwitch;
	
	
	public PAMSRV_view_systempanel(PAMSRV_controller controller, PAMSRV_model model) {
		this.controller = controller;
		this.model = model;
		
		this.setBorder(new TitledBorder(new EtchedBorder(), "System"));
		this.setLayout(new FlowLayout());
		
		wifiPanel = new JPanel();
		wifiPanel.setBorder(new TitledBorder(new EtchedBorder(), "Wifi"));
		wifiPanel.setLayout(new GridLayout(2,2));
		wifiLabel = new JLabel("Wifi Status");
		wifiTextField = new JTextField(15);
		wifiTextField.setEditable(false);
		wifiTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		wifiTextField.setBackground(Color.WHITE);
		wifiPanel.add(wifiLabel);
		wifiPanel.add(wifiTextField);
		this.add(wifiPanel);
		
		sysBtnPanel = new JPanel();
		sysBtnPanel.setLayout(new GridLayout(2, 1));
		sBiteBtn = new JButton("S-Bite Test");
		calibrateBtn = new JButton("Calibrate IMU");
		SystemButtonListener sbl = new SystemButtonListener();
		sBiteBtn.addActionListener(sbl);
		calibrateBtn.addActionListener(sbl);
		sysBtnPanel.add(sBiteBtn);
		sysBtnPanel.add(calibrateBtn);
		this.add(sysBtnPanel);
		
		imuCalibrationPanel = new JPanel();
		imuCalibrationPanel.setBorder(new TitledBorder(new EtchedBorder(), "IMU"));
		imuCalibrationPanel.setLayout(new GridLayout(4,2));
		sysCalLabel = new JLabel("Sys CalState");
		sysCalTextField = new JTextField(3);
		sysCalTextField.setEditable(false);
		sysCalTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		sysCalTextField.setBackground(Color.WHITE);
		magCalLabel = new JLabel("Mag CalState");
		magCalTextField = new JTextField(3);
		magCalTextField.setEditable(false);
		magCalTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		magCalTextField.setBackground(Color.WHITE);
		gyroCalLabel = new JLabel("Gyro CalState");
		gyroCalTextField = new JTextField(3);
		gyroCalTextField.setEditable(false);
		gyroCalTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		gyroCalTextField.setBackground(Color.WHITE);
		accelCalLabel = new JLabel("Accel CalState");
		accelCalTextField = new JTextField(3);
		accelCalTextField.setEditable(false);
		accelCalTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		accelCalTextField.setBackground(Color.WHITE);
		imuCalibrationPanel.add(sysCalLabel);
		imuCalibrationPanel.add(sysCalTextField);
		imuCalibrationPanel.add(magCalLabel);
		imuCalibrationPanel.add(magCalTextField);
		imuCalibrationPanel.add(gyroCalLabel);
		imuCalibrationPanel.add(gyroCalTextField);
		imuCalibrationPanel.add(accelCalLabel);
		imuCalibrationPanel.add(accelCalTextField);
		this.add(imuCalibrationPanel);
		
		batteryPanel = new JPanel();
		batteryPanel.setBorder(new TitledBorder(new EtchedBorder(), "Battery"));
		batteryPanel.setLayout(new GridLayout(2, 2));
		batteryVoltageLabel = new JLabel("Battery Voltage");
		batteryVoltageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		voltageTextField = new JTextField(15);
		voltageTextField.setEditable(false);
		voltageTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		batteryCurrentLabel = new JLabel("Battery Current");
		batteryCurrentLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		currentTextField = new JTextField(15);
		currentTextField.setEditable(false);
		currentTextField.setBackground(Color.WHITE);
		currentTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		batteryPanel.add(batteryVoltageLabel);
		batteryPanel.add(voltageTextField);
		batteryPanel.add(batteryCurrentLabel);
		batteryPanel.add(currentTextField);
		this.add(batteryPanel);
		
		rpmMotorPanel = new JPanel();
		rpmMotorPanel.setBorder(new TitledBorder(new EtchedBorder(), "Motor RPM"));
		rpmMotorPanel.setLayout(new GridLayout(4, 4));
		rpmMotorALabel = new JLabel("Motor A");
		rpmMotorATextField = new JTextField(10);
		rpmMotorATextField.setEditable(false);
		rpmMotorATextField.setBackground(Color.WHITE);
		rpmMotorATextField.setHorizontalAlignment(SwingConstants.RIGHT);
		rpmMotorBLabel = new JLabel("Motor B");
		rpmMotorBTextField = new JTextField(10);
		rpmMotorBTextField.setEditable(false);
		rpmMotorBTextField.setBackground(Color.WHITE);
		rpmMotorBTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		rpmMotorCLabel = new JLabel("Motor C");
		rpmMotorCTextField = new JTextField(10);
		rpmMotorCTextField.setEditable(false);
		rpmMotorCTextField.setBackground(Color.WHITE);
		rpmMotorCTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		rpmMotorDLabel = new JLabel("Motor D");
		rpmMotorDTextField = new JTextField(10);
		rpmMotorDTextField.setEditable(false);
		rpmMotorDTextField.setBackground(Color.WHITE);
		rpmMotorDTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		rpmMotorPanel.add(rpmMotorALabel);
		rpmMotorPanel.add(rpmMotorATextField);
		rpmMotorPanel.add(rpmMotorBLabel);
		rpmMotorPanel.add(rpmMotorBTextField);
		rpmMotorPanel.add(rpmMotorCLabel);
		rpmMotorPanel.add(rpmMotorCTextField);
		rpmMotorPanel.add(rpmMotorDLabel);
		rpmMotorPanel.add(rpmMotorDTextField);
		this.add(rpmMotorPanel);
		
		devicePanel = new JPanel();
		devicePanel.setBorder(new TitledBorder(new EtchedBorder(), "Devices"));
		devicePanel.setLayout(new GridLayout(3, 2));
		imuLabel = new JLabel("IMU");
		imuSwitch = new ToggleSwitch();
		imuSwitch.setPreferredSize(new Dimension(80, 35));
		motorLabel = new JLabel("Motor");
		motorSwitch = new ToggleSwitch();
		motorSwitch.setPreferredSize(new Dimension(80, 35));
		lidarLabel = new JLabel("Lidar");
		lidarSwitch = new ToggleSwitch();
		lidarSwitch.setPreferredSize(new Dimension(80, 35));
		devicePanel.add(imuLabel);
		devicePanel.add(imuSwitch);
		devicePanel.add(motorLabel);
		devicePanel.add(motorSwitch);
		devicePanel.add(lidarLabel);
		devicePanel.add(lidarSwitch);
		this.add(devicePanel);
		
		t = new Timer(200, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PAMSRV_view_systempanel.this.repaint();
			}
		});
		t.start();
		
		model.debugPrint("SystemPanel gestartet");
	}//End: Konstruktor
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setWifiStatus(model.wifi_getConnectionStatus());
		setBiteStatus();
		setCalibrationStatus_Button();
		setSysCalStatus(model.imu_getSysCalibrationState());
		setMagCalStatus(model.imu_getMagCalibrationState());
		setGyroCalStatus(model.imu_getGyroCalibrationState());
		setAccelCalStatus(model.imu_getAccelCalibrationState());
		setBatteryVoltage(model.system_getBatteryVoltage());
		setBatteryCurrent(model.system_getBatteryCurrent());
		setRPM_MotorA(model.move_getMotorRPM_A());
		setRPM_MotorB(model.move_getMotorRPM_B());
		setRPM_MotorC(model.move_getMotorRPM_C());
		setRPM_MotorD(model.move_getMotorRPM_D());
		checkDeviceSwitch();
	}//End: paintComponent
	
	private void checkDeviceSwitch() {
		if (imuSwitch.isActivated() && !model.isIMUStarted()) {
			model.wifi_addNewOutgoingMessage("program:imu:start");
			model.log_addNewLogEntry("Inertial Measurement Unit gestartet");
			model.debugPrint("Inertial Measurement Unit gestartet");
			model.setIMUStarted(true);
		} else {
			if (!imuSwitch.isActivated() && model.isIMUStarted()) {
				model.wifi_addNewOutgoingMessage("program:imu:stop");
				model.log_addNewLogEntry("Inertial Measurement Unit gestoppt");
				model.debugPrint("Inertial Measurement Unit gestoppt");
				model.setIMUStarted(false);
			}
		}
		if (motorSwitch.isActivated() && !model.isMotorStarted()) {
			model.wifi_addNewOutgoingMessage("program:motor:start");
			model.log_addNewLogEntry("Motorcontroller gestartet");
			model.debugPrint("Motorcontroller gestartet");
			model.setMotorStarted(true);
		} else {
			if (!motorSwitch.isActivated() && model.isMotorStarted()) {
				model.wifi_addNewOutgoingMessage("program:motor:stop");
				model.log_addNewLogEntry("Motorcontroller gestoppt");
				model.debugPrint("Motorcontroller gestoppt");
				model.setMotorStarted(false);
			}
		}
		if (lidarSwitch.isActivated() && !model.isLidarStarted()) {
			model.wifi_addNewOutgoingMessage("program:lidar:start");
			model.log_addNewLogEntry("Lidarcontroller gestartet");
			model.debugPrint("Lidarcontroller gestartet");
			model.setLidarStarted(true);
		} else {
			if (!lidarSwitch.isActivated() && model.isLidarStarted()) {
				model.wifi_addNewOutgoingMessage("program:lidar:stop");
				model.log_addNewLogEntry("Lidarcontroller gestoppt");
				model.debugPrint("Lidarcontroller gestoppt");
				model.setLidarStarted(false);
			}
		}
	}//End: checkDeviceSwitch
	
	private void setSysCalStatus(int status) {
		switch (status) {
			case 0:		this.sysCalTextField.setBackground(Color.RED);
						break;
			case 1:		this.sysCalTextField.setBackground(Color.YELLOW);
			case 2: 	this.sysCalTextField.setBackground(Color.YELLOW);
						break;
			case 3:		this.sysCalTextField.setBackground(Color.GREEN);
						break;
			default:	this.sysCalTextField.setBackground(Color.WHITE);
						break;
		}
		if (status > 0) {
			this.sysCalTextField.setText(String.valueOf(status));
		} else {
			this.sysCalTextField.setText("0");
		}
	}//End: setSysCalStatus()
	private void setMagCalStatus(int status) {
		switch (status) {
			case 0:		this.magCalTextField.setBackground(Color.RED);
						break;
			case 1:		this.magCalTextField.setBackground(Color.YELLOW);
			case 2: 	this.magCalTextField.setBackground(Color.YELLOW);
						break;
			case 3:		this.magCalTextField.setBackground(Color.GREEN);
						break;
			default:	this.magCalTextField.setBackground(Color.WHITE);
						break;
		}
		if (status > 0) {
			this.magCalTextField.setText(String.valueOf(status));
		} else {
			this.magCalTextField.setText("0");
		}
	}//End: setMagCalStatus()
	private void setGyroCalStatus(int status) {
		switch (status) {
			case 0:		this.gyroCalTextField.setBackground(Color.RED);
						break;
			case 1:		this.gyroCalTextField.setBackground(Color.YELLOW);
			case 2: 	this.gyroCalTextField.setBackground(Color.YELLOW);
						break;
			case 3:		this.gyroCalTextField.setBackground(Color.GREEN);
						break;
			default:	this.gyroCalTextField.setBackground(Color.WHITE);
						break;
		}
		if (status > 0) {
			this.gyroCalTextField.setText(String.valueOf(status));
		} else {
			this.gyroCalTextField.setText("0");
		}
	}//End: setGyroCalStatus()
	private void setAccelCalStatus(int status) {
		switch (status) {
			case 0:		this.accelCalTextField.setBackground(Color.RED);
						break;
			case 1:		this.accelCalTextField.setBackground(Color.YELLOW);
			case 2: 	this.accelCalTextField.setBackground(Color.YELLOW);
						break;
			case 3:		this.accelCalTextField.setBackground(Color.GREEN);
						break;
			default:	this.accelCalTextField.setBackground(Color.WHITE);
						break;
		}
		if (status > 0) {
			this.accelCalTextField.setText(String.valueOf(status));
		} else {
			this.accelCalTextField.setText("0");
		}
	}//End: setAccelCalStatus()
	
	private void setRPM_MotorA(double rpm_MotorA) {
		this.rpmMotorATextField.setText(rpm_MotorA + " rpm");
	}
	private void setRPM_MotorB(double rpm_MotorB) {
		this.rpmMotorBTextField.setText(rpm_MotorB + " rpm");
	}
	private void setRPM_MotorC(double rpm_MotorC) {
		this.rpmMotorCTextField.setText(rpm_MotorC + " rpm");
	}
	private void setRPM_MotorD(double rpm_MotorD) {
		this.rpmMotorDTextField.setText(rpm_MotorD + " rpm");
	}
	
	private void setBatteryVoltage(double battery_voltage) {
		this.voltageTextField.setText(battery_voltage + " V");
		
		if (battery_voltage <= 11.0) {
			if (battery_voltage == 0) {
				this.voltageTextField.setBackground(Color.WHITE);
			} else {
				this.voltageTextField.setBackground(Color.RED);
			}
		} else {
			this.voltageTextField.setBackground(Color.GREEN);
		}
	}
	private void setBatteryCurrent(double battery_current) {
		this.currentTextField.setText(battery_current + " A");
	}
	
	private void setWifiStatus(boolean wifi_status) {
		if (wifi_status) {
			this.wifiTextField.setText("Connected");
			this.wifiTextField.setBackground(Color.GREEN);
		} else {
			this.wifiTextField.setText("Not Connected");
			this.wifiTextField.setBackground(Color.RED);
		}
	}
	
	private void setBiteStatus() {
		if (model.wasBitePerformed()) {
			if (model.wasBitePassed()) {
				this.sBiteBtn.setBackground(Color.GREEN);
			} else {
				this.sBiteBtn.setBackground(Color.RED);
			}
		}
	}
	
	private void setCalibrationStatus_Button() {
		if (model.imu_isCalibrated()) {
			this.calibrateBtn.setBackground(Color.GREEN);
		}
	}
	
	class SystemButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (evt.getSource() == sBiteBtn) {
				controller.biteTest();
			} else {
				if (evt.getSource() == calibrateBtn) {
					controller.calibrateIMU();
				}
			}
		}//End: ActionPerformed()
	}//End: INNER class SystemButtonListener
	
	
}//End: class SystemPanel
