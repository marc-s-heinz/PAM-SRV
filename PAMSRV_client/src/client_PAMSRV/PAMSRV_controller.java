package client_PAMSRV;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import client_PAMSRV.PAMSRV_wificlient.IncomingStream;
import client_PAMSRV.PAMSRV_wificlient.OutgoingStream;

/******************************************************************
 * 		Vermittler zwischen GUI und Daten, enthält die Logik
 * 		muss zugriff auf beides haben
 ******************************************************************/
public class PAMSRV_controller {
	private PAMSRV_view view;
	private PAMSRV_model model;
	private PAMSRV_wificlient wificontroller;
	
	private IncomingStream is;
	private OutgoingStream os;
	
	public PAMSRV_controller() {
		model = new PAMSRV_model();
		view = new PAMSRV_view(this, model, "GUI v1.0");
		
		view.init();
		model.log_addNewLogEntry("Bot RemoteClient gestartet.");
	}
	
	
	public boolean connectToServer() {
		PAMSRV_wificlient wificontroller = new PAMSRV_wificlient(this, model);
		os = wificontroller.getOutgoingStream();
		is = wificontroller.getIncomingStream();
		os.start();
		is.start();
		return model.wifi_getConnectionStatus();
	}
	
	/*********************************************
	 * 		Not (completely) implemented yet
	 *********************************************/	
	public void biteTest() {
		if(model.wifi_getConnectionStatus()) {
			model.log_addNewLogEntry("BiteTest requested...");
			try {
				os.bw.write("bite:perform:1");
				os.bw.newLine();
				os.bw.flush();
			} catch (IOException e) {
				model.debugPrint("ERROR: failed to send bite command");
				model.debugErrorPrint(e);
			}
		} else {
			model.debugPrint("Test nicht möglich: Nicht mit Server verbunden...");
			model.log_addNewLogEntry("Test nicht möglich: Nicht mit Server verbunden...");
		}
	}//End: biteTest
		
	
	public void calibrateIMU() {
		if(model.wifi_getConnectionStatus()) {
			if (!model.isIMUCalibrationRunning()) {
				if (!model.imu_isCalibrated()) {
					try {
						os.bw.write("bite:calibrate");
						os.bw.newLine();
						os.bw.flush();
						model.log_addNewLogEntry("IMU Kalibrierung gestartet...");
						//TODO Anweisungen zur Kalibrierung im Log oder als PopUp
						model.setIMUCalibrationRunning(true);
					} catch (IOException e) {
						model.debugPrint("ERROR: failed to send calibrate command");
						model.debugErrorPrint(e);
					}
				} else {
					model.log_addNewLogEntry("IMU bereits Kalibriert.");
				}
			} else {
				model.wifi_addNewOutgoingMessage("bite:calibrate");
				model.log_addNewLogEntry("IMU Kalibrierung gestoppt...");
				model.setIMUCalibrationRunning(false);
			}
		} else {
			model.debugPrint("Kalibrierung nicht möglich: Nicht mit Server verbunden...");
			model.log_addNewLogEntry("Kalibrierung nicht möglich: Nicht mit Server verbunden...");
		}
	}
	
	
}//End: class controller
