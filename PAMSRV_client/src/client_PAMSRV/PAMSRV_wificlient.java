package client_PAMSRV;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/******************************************************************
 * 		Erstellt eine wifi Verbindung zum Server
 * 
 ******************************************************************/
public class PAMSRV_wificlient {
	private PAMSRV_controller controller;
	private PAMSRV_model model;
	private String hostip;
	private int hostport;
	private Socket socket;
	private InputStream socketinstr;
	private InputStreamReader isr;
	private BufferedReader br;
	private OutputStream socketoutstr;
	private OutputStreamWriter osw;
	private BufferedWriter bw;
	private OutgoingStream os;
	private IncomingStream is;
	
	public PAMSRV_wificlient(PAMSRV_controller controller, PAMSRV_model model) {
		this.controller = controller;
		this.model = model;
		
		if (model.wifi_getServerIP() == null) {
			this.hostip = model.wifi_getDefaultIP();
		} else {
			this.hostip = model.wifi_getServerIP();
		}
		if (model.wifi_getServerPort() == 0) {
			this.hostport = model.wifi_getDefaultPort();
		} else {
			this.hostport = model.wifi_getServerPort();
		}
		
		try {
			model.debugPrint("Versuche Verbindung zum Server " + hostip + ":" + hostport + " herzustellen...");
			socket = new Socket(hostip, hostport);
			model.debugPrint("Verbindung hergestellt");
			
			model.debugPrint("Versuche Inputstream zu oeffnen...");
			socketinstr = socket.getInputStream();
			isr = new InputStreamReader(socketinstr);
			br = new BufferedReader(isr);
			model.debugPrint("Inputstream geoeffnet");
			
			model.debugPrint("Versuche Outputstream zu oeffnen...");
			socketoutstr = socket.getOutputStream();
			osw = new OutputStreamWriter(socketoutstr);
			bw = new BufferedWriter(osw);
			model.debugPrint("Outputstream geoeffnet");
			
			model.debugPrint("Versuche Streamreader/ -writer an Thread zu uebergeben...");
			os = new OutgoingStream(socket, bw);
			is = new IncomingStream(socket, br);
			
			model.debugPrint("Mit Server verbunden.");
			if (socket.isConnected()) {
				model.wifi_setConnectionStatus(true);
				model.log_addNewLogEntry("Verbindung zu Server " + hostip + ":" + hostport + " hergestellt");
			}
		} catch (IOException e) {
			model.debugPrint("ERROR: Fehler beim Verbinden zum Server...");
			model.debugErrorPrint(e);
		}
	}//End: Konstruktor
	
	public OutgoingStream getOutgoingStream() {
		return os;
	}
	public IncomingStream getIncomingStream() {
		return is;
	}

	
	class OutgoingStream extends Thread {
		Socket socket;
		BufferedWriter bw;
		public OutgoingStream(Socket socket, BufferedWriter bw) {
			this.socket = socket;
			this.bw = bw;
		}
		
		@Override
		public void run() {
			model.debugPrint("Outgoingstream gestartet.");
			while (true) {
					if (model.wifi_isOutgoingMsgAvailable()) {
						try {
							bw.write(model.wifi_getOldestMessage());
							bw.newLine();
							bw.flush();
						} catch (IOException e) {
							model.debugPrint("ERROR: Fehler beim Schreiben des Outgoing Streams...");
							model.debugErrorPrint(e);
						}
					}
			}
		}//End: run()
	}//End: INNER class outgoingStream
	
	
	class IncomingStream extends Thread {
		Socket socket;
		BufferedReader br;
		public IncomingStream(Socket socket, BufferedReader br) {
			this.socket = socket;
			this.br = br;
		}//End: Konstruktor
		
		@Override
		public void run() {
			model.debugPrint("Incomingstream gestartet.");
			while (true) {
					try {
						if (br.ready()) {
							String in = br.readLine();
							IncomingStreamHandler ish = new IncomingStreamHandler(in);
							ish.start();
						}
					} catch (IOException e) {
						model.debugPrint("ERROR: Fehler beim Lesen des Incoming Streams...");
						model.debugErrorPrint(e);
					}
			}
		}//End: run()
	}//End: INNER class incomingStream
	
	class IncomingStreamHandler extends Thread {
		String msgIn;
		public IncomingStreamHandler(String msg) {
			this.msgIn = msg;
		}
		@Override
		public void run() {
			String[] temp = msgIn.split(":");
			switch (temp[0]) {
				case "motorcontroller":	handleMotorController(temp);
										break;
				case "lidarcontroller":	handleLidarController(temp);
										break;
				case "imu":				handleIMU(temp);
										break;
				case "log":				handleLog(temp);
										break;						
				case "bite":			handleBite(temp);
										break;
				default:				model.debugPrint("ERROR: unknown incoming format/command");
										for (int i=0; i<temp.length; i++) {
											model.debugPrint(temp[i]);
										}
										break;
			}
		}//End: run()
		
		public void handleMotorController(String[] data) {
			double voltage = (Double.parseDouble(data[1])/100);
			double current = (Double.parseDouble(data[2])/100);
			model.system_setBatteryVoltage(voltage);
			model.system_setBatteryCurrent(current);
			model.debugPrint("Spannung: " + voltage + "\t\tStrom: " + current);
		}//End: MotorController
		
		public void handleLidarController(String[] data) {
			int angle = Integer.parseInt(data[1]);
			int distance = Integer.parseInt(data[2]);
			if (angle != 0 || distance != 0) {
				int val = model.lidar_putIfAbsent(angle, distance);
				if(String.valueOf(val) == null) {
					model.lidar_replaceDistanceValue(angle, distance);				
				}
			}
		}//End: LidarController
		
		public void handleIMU(String[] data) {
			model.imu_setSysCalibrationState(Integer.parseInt(data[1]));
			model.imu_setAccelCalibrationState(Integer.parseInt(data[2]));
			model.imu_setMagCalibrationState(Integer.parseInt(data[3]));
			model.imu_setGyroCalibrationState(Integer.parseInt(data[4]));
			model.imu_setHeading(Double.parseDouble(data[5]));
			model.imu_setRoll(Double.parseDouble(data[6]));
			model.imu_setPitch(Double.parseDouble(data[7]));
			model.imu_setTemp(Integer.parseInt(data[8]));
			int state = 0;
			if (model.imu_getSysCalibrationState() >= 2) {
				state++;
			}
			if (model.imu_getMagCalibrationState() >= 2) {
				state++;
			}
			if (model.imu_getGyroCalibrationState() >= 2) {
				state++;
			}
			if (model.imu_getAccelCalibrationState() >= 2) {
				state++;
			}
			if (state >= 3) {
				model.imu_setCalibrated(true);
			}
		}//End: IMU
		
		public void handleLog(String[] data) {
			model.log_addNewLogEntry(data[1]);
		}//End: Log
		
		
		public void handleBite(String[] data) {
			switch(data[1]) {
				case "passed":		model.setBitePassed();
									model.setBiteResult(data[2]);
									model.log_addNewLogEntry("Bite Passed. Result: " + model.getBiteResult());
									model.debugPrint("Bite Passed. Result: " + model.getBiteResult());
									break;
				case "failed":		model.setBiteFailed();
									model.setBiteResult(data[2]);
									model.log_addNewLogEntry("Bite FAILED. Result: " + model.getBiteResult());
									model.debugPrint("Bite Failed. Result: " + model.getBiteResult());
									break;
				case "calibrate":	if (data[2] == "true") {
										model.imu_setCalibrated(true);
										model.debugPrint("IMU calibration successfull");
										model.log_addNewLogEntry("IMU calibration successfull");
									} else {
										model.debugPrint(data[2]);
									}
									String[] calData = data[2].split("-");
									model.imu_setSysCalibrationState(Integer.parseInt(calData[0]));
									model.imu_setMagCalibrationState(Integer.parseInt(calData[1]));
									model.imu_setGyroCalibrationState(Integer.parseInt(calData[2]));
									model.imu_setAccelCalibrationState(Integer.parseInt(calData[3]));
									break;
				default:			//TODO
									
			}
		}//End: Check
		
	}//End: INNER class IncomingStreamHandler
	
}//End: WifiClient
