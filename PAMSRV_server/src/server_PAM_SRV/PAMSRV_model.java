/*
 * Creates an object to store and deliver data
 */
package server_PAM_SRV;

import java.util.ArrayList;


public class PAMSRV_model {
    //set "false" für kürzere Laufzeit oder wenn keine Ausgabe vorhanden
    private final boolean debugMode = true;
    
    private boolean modelrunning = false;
    private boolean controllerrunning = false;
    private boolean i2crunning = false;
    private boolean serverrunning = false;
    
    private volatile ArrayList<String> outgoingMsg;
    
    private final int motor_I2C_Address = 0x37;
    private final int lidar_I2C_Address = 0x38;
    private final int imu_I2C_Address = 0x28;
    private volatile boolean motorInitialized = false;
    private volatile boolean lidarInitialized = false;
    private volatile byte[] motorData;
    private volatile byte[] lidarData;
    private volatile boolean motorDataAvailable = false;
    private volatile boolean lidarDataAvailable = false;
    private volatile boolean imuDataAvailable = false;
    private final long motorControllerPollingInterval = 150;
    private final long lidarControllerPollingInterval = 20;
    private final long imuPollingInterval = 100;
    private volatile boolean imuPolling = false;
    private volatile boolean motorPolling = false;
    private volatile boolean lidarPolling = false;
    private volatile boolean stopIMUPolling = false;
    
    private final short safetydistance = 400;//in mm
    private volatile boolean safetyDistanceNoticed = false;
    private volatile short voltage;
    private volatile short current;
    private volatile short lidarDistance;
    private volatile short lidarAngle;
    
    private volatile boolean imuPresent = false;
    private volatile boolean imuInitialised = false;
    private volatile boolean imuCalibrated = false;
    private volatile int imuSysCalibrationState;
    private volatile int imuGyroCalibrationState;
    private volatile int imuAccelCalibrationState;
    private volatile int imuMagCalibrationState;
    private volatile int imuTemp;
    private volatile double imuHeading;
    private volatile double imuRoll;
    private volatile double imuPitch;
    private volatile double imuContHeading;
    private volatile boolean calAbortFlag;
    private volatile boolean calStarted;
    
    
    public PAMSRV_model() {
        motorData = new byte[4];
        lidarData = new byte[4];
        outgoingMsg = new ArrayList<>();
        debugPrint("Model gestartet");
        setModelRunning(true);
    }//End: Konstruktor
    
    /************************************
    *   SETTER / GETTER
    *************************************/
    public boolean addNewOutgoingMessage(String msg) {
	return outgoingMsg.add(msg);
    }
    public String getOldestMessage() {
	String temp = outgoingMsg.get(0);
	outgoingMsg.remove(0);
	return temp;
    }
    public boolean isOutgoingMsgAvailable() {
	return !outgoingMsg.isEmpty();
    }
    public int getOutgoingMsgSize() {
	return outgoingMsg.size();
    }
    
    //  Programmvariablen
    public short getSafetyDistance() {
        return safetydistance;
    }
    public boolean wasSafetyDistanceNoticed() {
        return safetyDistanceNoticed;
    }
    public void setSafetyDistanceNoticed(boolean state) {
        this.safetyDistanceNoticed = state;
    }
    public void setSystemVoltage(short volt) {// in cV oder 10mV
        voltage = volt;
    }
    public short getSystemVoltage() {
        return voltage;
    }
    public void setSystemCurrent(short amp) {//in cA oder 10mA
        current = amp;
    }
    public short getSystemCurrent() {
        return current;
    }
    public void setLidarDistance(short distance) {
        lidarDistance = distance;
    }
    public short getLidarDistance() {
        return lidarDistance;
    }
    public void setLidarAngle(short angle) {
        lidarAngle = angle;
    }
    public short getLidarAngle() {
        return lidarAngle;
    }
    
    //  Allgemein
    public boolean isModelRunning() {
        return modelrunning;
    }
    private void setModelRunning(boolean state) {
        modelrunning = state;
    }
    public boolean isControllerRunning() {
        return controllerrunning;
    }
    public void setControllerRunning(boolean state) {
        this.controllerrunning = state;
    }
    public boolean isI2CRunning() {
        return i2crunning;
    }
    public void setI2CRunning(boolean state) {
        i2crunning = state;
    }
    public boolean isServerRunning() {
        return serverrunning;
    }
    public void setServerRunning(boolean state) {
        serverrunning = state;
    }

    
    //  I2C
    public void setMotorInitialized(boolean state) {
        this.motorInitialized = state;
    }
    public void setLidarInitialized(boolean state) {
        this.lidarInitialized = state;
    }
    public boolean isMotorInitialized() {
        return this.motorInitialized;
    }
    public boolean isLidarInitialized() {
        return this.lidarInitialized;
    }
    public void setIMUPollingActive(boolean state) {
        this.imuPolling = state;
    }
    public boolean isIMUPollingActive() {
        return this.imuPolling;
    }
    public void setMotorPollingActive(boolean state) {
        this.motorPolling = state;
    }
    public boolean isMotorPollingActive() {
        return this.motorPolling;
    }
    public void setLidarPollingActive(boolean state) {
        this.lidarPolling = state;
    }
    public boolean isLidarPollingActive() {
        return this.lidarPolling;
    }
    public long getMotorControllerPollingInterval() {
        return motorControllerPollingInterval;
    }
    public long getLidarControllerPollingInterval() {
        return lidarControllerPollingInterval;
    }
    public long getIMUPollingInterval() {
        return imuPollingInterval;
    }
    public boolean isIMUDataAvailable() {
        return imuDataAvailable;
    }
    public void setIMUDataFlag(boolean state) {
        imuDataAvailable = state;
    }
    public boolean isLidarDataAvailable() {
        return lidarDataAvailable;
    }
    public void setLidarDataFlag(boolean state) {
        lidarDataAvailable = state;
    }
    public boolean isMotorDataAvailable() {
        return motorDataAvailable;
    }
    public void setMotorDataFlag(boolean state) {
        motorDataAvailable = state;
    }
    public byte[] getMotorData() {
        return motorData;
    }
    public void setMotorData(byte[] data) {
        motorData = data;
    }
    public byte[] getLidarData() {
        return lidarData;
    }
    public void setLidarData(byte[] data) {
        lidarData = data;
    }
    public int getI2CMotorControllerAddress() {
        return motor_I2C_Address;
    }
    public int getI2CLidarControllerAddress() {
        return lidar_I2C_Address;
    }
    public int getI2CIMUAddress() {
        return imu_I2C_Address;
    }
        //IMU
    public boolean isIMUPresent() {
        return imuPresent;
    }
    public void setIMUPresent(boolean state) {
        imuPresent = state;
    }
    public boolean isIMUInitialized() {
        return imuInitialised;
    }
    public void setIMUInitialised(boolean state) {
        imuInitialised = state;
    }
    public boolean isIMUCalibrated() {
        return imuCalibrated;
    }
    public void setIMUCalibrated(boolean state) {
        imuCalibrated = state;
    }
    public int getIMUSysCalibrationState() {
        return imuSysCalibrationState;
    }
    public void setIMUSysCalibrationState(int state) {
        imuSysCalibrationState = state;
    }
    public int getIMUGyroCalibrationState() {
        return imuGyroCalibrationState;
    }
    public void setIMUGyroCalibrationState(int state) {
        imuGyroCalibrationState = state;
    }
    public int getIMUAccelCalibrationState() {
        return imuAccelCalibrationState;
    }
    public void setIMUAccelCalibrationState(int state) {
        imuAccelCalibrationState = state;
    }
    public int getIMUMagCalibrationState() {
        return imuMagCalibrationState;
    }
    public void setIMUMagCalibrationState(int state) {
        imuMagCalibrationState = state;
    }
    public int getIMUTemp() {
        return imuTemp;
    }
    public void setIMUTemp(int temp) {
        imuTemp = temp;
    }
    public double getIMUHeading() {
        return imuHeading;
    }
    public void setIMUHeading(double heading) {
        imuHeading = heading;
    }
    public double getIMURoll() {
        return imuRoll;
    }
    public void setIMURoll(double roll) {
        imuRoll = roll;
    }
    public double getIMUPitch() {
        return imuPitch;
    }
    public void setIMUPitch(double pitch) {
        imuPitch = pitch;
    }
    public double getIMUContHeading() {
        return imuContHeading;
    }
    public void setIMUContHeading(double heading) {
        imuContHeading = heading;
    }
    public boolean getIMUCalAbortFlag() {
        return this.calAbortFlag;
    }
    public void setIMUCalAbortFlag(boolean state) {
        this.calAbortFlag = state;
    }
    public void setIMUCalStarted(boolean state) {
        this.calStarted = state;
    }
    public boolean getIMUCalStarted() {
        return this.calStarted;
    }
    
    
    public void debugPrint(String s) {
        if (debugMode) {
            System.out.println(s);
        }
    }//End: debugPrint
    public void debugErrorPrint(Exception e) {
        if (debugMode) {
            e.printStackTrace();
        }
    }//End: debugErrorPrint
}//End: class PAMSRV_model
