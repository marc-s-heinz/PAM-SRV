package client_PAMSRV;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/******************************************************************
 * 		Enthält alle Daten
 * 
 ******************************************************************/
public class PAMSRV_model {
	//Program
	private volatile boolean debugMode = true;
	private volatile ArrayList<String> logEntry;
	private volatile boolean imuStarted;
	private volatile boolean motorStarted;
	private volatile boolean lidarStarted;
	//Bite test
	private volatile boolean bitePassed;
	private volatile boolean biteFailed;
	private volatile String biteResult;
	private volatile boolean imuCalStarted = false;
	//Wifi client
	private final String default_ip = "192.168.178.50";
	private final int default_port = 5555;
	private String serverip = null;
	private int serverport = 0;
	private volatile boolean serverStatus;
	private volatile ArrayList<String> outgoingMsg;
	//movement
	private int targetAngle;
	private int targetDistance;
	private volatile int targetSpeedValue = 100;
	private volatile double rpmMotorA;
	private volatile double rpmMotorB;
	private volatile double rpmMotorC;
	private volatile double rpmMotorD;
	//robot system
	private volatile double batteryVoltage;
	private volatile double batteryCurrent;
	//imu
	private volatile int imuSysCalState = -1;
	private volatile int imuMagCalState = -1;
	private volatile int imuGyroCalState = -1;
	private volatile int imuAccelCalState = -1;
	private volatile double imuHeading;
	private volatile double imuRoll;
	private volatile double imuPitch;
	private volatile int imuTemp;
	private volatile boolean imuCalibrated = false;
	
	//lidar
	private ConcurrentHashMap<Integer, Integer> map;
	private Iterator<Integer> iterator;
	
	public PAMSRV_model() {
		map = new ConcurrentHashMap<>();
		ConcurrentHashMap.KeySetView<Integer, Integer> keySetView = map.keySet();
		iterator = keySetView.iterator();
		logEntry = new ArrayList<String>();
		outgoingMsg = new ArrayList<String>();
	}//End: Konstruktor
	
	public void debugPrint(String s) {
		if (debugMode) {
			System.out.println(s);
		}
	}
	public void debugErrorPrint(Exception e) {
		if (debugMode) {
			e.printStackTrace();
		}
	}
	public String getCurrentTime() {
		LocalDateTime now = LocalDateTime.now();
		String pattern = "yyyy-MM-dd HH:mm:ss";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
		String output = dtf.format(now);
		return output;
	}
	
	/******************************
	 * 	GETTER / SETTER
	 ******************************/
		/********************************
		 * 		General
		 ********************************/
		public boolean getDebugState() {
			return this.debugMode;
		}
		public void setDebugState(boolean state) {
			this.debugMode = state;
		}
		/********************************
		 * 		Program
		 ********************************/
		public boolean isIMUStarted() {
			return this.imuStarted;
		}
		public void setIMUStarted(boolean state) {
			this.imuStarted = state;
		}
		public boolean isMotorStarted() {
			return this.motorStarted;
		}
		public void setMotorStarted(boolean state) {
			this.motorStarted = state;
		}
		public boolean isLidarStarted() {
			return this.lidarStarted;
		}
		public void setLidarStarted(boolean state) {
			this.lidarStarted = state;
		}
		/********************************
		 * 		bite test
		 ********************************/
		public void setBitePassed() {
			this.bitePassed = true;
			this.biteFailed = false;
		}
		public boolean wasBitePassed() {
			if (bitePassed && !biteFailed) {
				return true;
			} else {
				return false;
			}
		}
		public boolean wasBitePerformed() {
			if (bitePassed || biteFailed) {
				return true;
			} else {
				return false;
			}
		}
		public void setBiteFailed() {
			this.biteFailed = true;
			this.bitePassed = false;
		}
		public void setBiteResult(String result) {
			this.biteResult = result;
		}
		public String getBiteResult() {
			return this.biteResult;
		}
		public void setIMUCalibrationRunning(boolean state) {
			this.imuCalStarted = state;
		}
		public boolean isIMUCalibrationRunning() {
			return this.imuCalStarted;
		}
		

		/********************************
		 * 		View
		 ********************************/
		public boolean log_addNewLogEntry(String log) {
			String temp = getCurrentTime() + "\t" + log + "\n";
			return logEntry.add(temp);
		}
		public String log_getOldestLogEntry() {
			String temp = logEntry.get(0);
			logEntry.remove(0);
			return temp;
		}
		public int log_getSize() {
			return logEntry.size();
		}
		
		/********************************
		 * 		Inertial Measurement Unit
		 ********************************/
		public int imu_getSysCalibrationState() {
			return this.imuSysCalState;
		}
		public void imu_setSysCalibrationState(int state) {
			this.imuSysCalState = state;
		}
		public int imu_getMagCalibrationState() {
			return this.imuMagCalState;
		}
		public void imu_setMagCalibrationState(int state) {
			this.imuMagCalState = state;
		}
		public int imu_getGyroCalibrationState() {
			return this.imuGyroCalState;
		}
		public void imu_setGyroCalibrationState(int state) {
			this.imuGyroCalState = state;
		}
		public int imu_getAccelCalibrationState() {
			return this.imuAccelCalState;
		}
		public void imu_setAccelCalibrationState(int state) {
			this.imuAccelCalState = state;
		}
		public double imu_getHeading() {
			return this.imuHeading;
		}
		public void imu_setHeading(double heading) {
			this.imuHeading = heading;
		}
		public double imu_getRoll() {
			return this.imuRoll;
		}
		public void imu_setRoll(double roll) {
			this.imuRoll = roll;
		}
		public double imu_getPitch() {
			return this.imuPitch;
		}
		public void imu_setPitch(double pitch) {
			this.imuPitch = pitch;
		}
		public int imu_getTemp() {
			return this.imuTemp;
		}
		public void imu_setTemp(int temp) {
			this.imuTemp = temp;
		}
		public void imu_setCalibrated(boolean state) {
			this.imuCalibrated = state;
		}
		public boolean imu_isCalibrated() {
			return this.imuCalibrated;
		}
		/********************************
		 * 		ROBOT SYSTEM
		 ********************************/
		public double system_getBatteryVoltage() {
			return batteryVoltage;
		}
		public void system_setBatteryVoltage(double voltage) {
			this.batteryVoltage = voltage;
		}
		public double system_getBatteryCurrent() {
			return batteryCurrent;
		}
		public void system_setBatteryCurrent(double current) {
			this.batteryCurrent = current;
		}
		/********************************
		 * 		MOVEMENT
		 ********************************/
		public void move_setTargetAngle(int target_angle) {
			this.targetAngle = target_angle;
		}
		public int move_getTargetAngle() {
			return targetAngle;
		}
		public void move_setTargetDistance(int target_distance) {
			this.targetDistance = target_distance;
		}
		public int move_getTargetDistance() {
			return targetDistance;
		}
		public int nav_getSpeedValue() {
			return this.targetSpeedValue;
		}
		public void nav_setSpeedValue(int speed) {
			this.targetSpeedValue = speed;
		}
		public void move_setMotorRPM_A(double rpm) {
			this.rpmMotorA = rpm;
		}
		public double move_getMotorRPM_A() {
			return this.rpmMotorA;
		}
		public void move_setMotorRPM_B(double rpm) {
			this.rpmMotorB = rpm;
		}
		public double move_getMotorRPM_B() {
			return this.rpmMotorB;
		}
		public void move_setMotorRPM_C(double rpm) {
			this.rpmMotorC = rpm;
		}
		public double move_getMotorRPM_C() {
			return this.rpmMotorC;
		}
		public void move_setMotorRPM_D(double rpm) {
			this.rpmMotorD = rpm;
		}
		public double move_getMotorRPM_D() {
			return this.rpmMotorD;
		}
		/********************************
		 * 		WIFI CLIENT
		 ********************************/
		public String wifi_getDefaultIP() {
			return default_ip;
		}
		public int wifi_getDefaultPort() {
			return default_port;
		}
		public String wifi_getServerIP() {
			return serverip;
		}
		public void wifi_setServerIP(String serverIP) {
			this.serverip = serverIP;
		}
		public int wifi_getServerPort() {
			return serverport;
		}
		public void wifi_setServerPort(int serverPort) {
			this.serverport = serverPort;
		}
		public boolean wifi_getConnectionStatus() {
			return serverStatus;
		}
		public void wifi_setConnectionStatus(boolean status) {
			this.serverStatus = status;
		}
		public boolean wifi_addNewOutgoingMessage(String msg) {
			return outgoingMsg.add(msg);
		}
		public String wifi_getOldestMessage() {
			String temp = outgoingMsg.get(0);
			outgoingMsg.remove(0);
			return temp;
		}
		public boolean wifi_isOutgoingMsgAvailable() {
			return !outgoingMsg.isEmpty();
		}
		public int wifi_getOutgoingMsgSize() {
			return outgoingMsg.size();
		}
		/********************************
		 * 		LIDAR
		 ********************************/
		public void lidar_clear() {
			map.clear();
		}
		public boolean lidar_hasAngle(int key_angle) {
			return map.containsKey(key_angle);
		}
		public boolean lidar_hasNextAngle() {
			return iterator.hasNext();
		}
		public int lidar_nextAngle() {
			return iterator.next();
		}
		public int lidar_getDistance(int key_angle) {
			return map.get(key_angle);
		}
		public int lidar_putIfAbsent(int key_angle, int val_distance) {
			int returnvalue;
			//returns null wenn key noch nicht vorhanden
			returnvalue = map.putIfAbsent(key_angle, val_distance);
			return returnvalue;
		}
		public void lidar_replaceDistanceValue(int key_angle, int val_distance) {
			map.replace(key_angle, val_distance);
		}
		public int lidar_getLength() {
			return map.size();
		}
}//End: class model
