/*
*   creates a controller object that handles all of the program logic
*/
package server_PAM_SRV;

import server_PAM_SRV.PAMSRV_serversocket.IncomingStream;
import server_PAM_SRV.PAMSRV_serversocket.OutgoingStream;
import java.io.IOException;

public class PAMSRV_controller {
    private PAMSRV_model bm;
    private PAMSRV_serversocket server;
    private PAMSRV_I2C_controller i2cDev;
    private OutgoingStream os;
    private IncomingStream is;
    private static BNO055 imu;
    
    
    public PAMSRV_controller(PAMSRV_model model) {
        this.bm = model;
        bm.debugPrint("Starte Bot_Controller...");

        server = new PAMSRV_serversocket(this, bm);
        server.start();

        i2cDev = new PAMSRV_I2C_controller(this, bm);

        bm.setControllerRunning(true);
        bm.debugPrint("Bot_Controller gestartet.");
        this.imu = i2cDev.getIMU();
    }//End: Konstruktor
    
    public void setIncomingStream(IncomingStream is) {
        this.is = is;
        this.is.start();
    }
    public void setOutgoingStream(OutgoingStream os) {
        this.os = os;
        this.os.start();
    }
    
    public boolean sendToClient(String s) {
        try {
            os.bw.write(s);
            os.bw.newLine();
            os.bw.flush();
            return true;
        } catch (IOException ex) {
            bm.debugPrint("ERROR: unable to send String to Client");
            bm.debugErrorPrint(ex);
            return false;
        }
    }//End: sendToClient()
    
    public void startIMU() {
        if (!bm.isIMUPollingActive()) {
            bm.setIMUPollingActive(true);
            i2cDev.startPollingIMU();
            HandleIMUData hi = new HandleIMUData();
            hi.start();
            bm.debugPrint("IMU data polling started.");
        } else {
            bm.debugPrint("ERROR: IMU data polling already running.");
        }
    }
    public void stopIMU() {
        if (bm.isIMUPollingActive()) {
            bm.setIMUPollingActive(false);
            i2cDev.stopPollingIMU();
            bm.debugPrint("IMU data polling stopped.");
        } else {
            bm.debugPrint("ERROR: IMU data polling already stopped.");
        }
    }
    public void startMotor() {
        if (!bm.isMotorInitialized()) {
            bm.debugPrint("Warning: Motorcontroller is not initialized. An Error might occur.");
        }
        if (!bm.isMotorPollingActive()) {
            bm.setMotorPollingActive(true);
            i2cDev.startPollingMotorcontroller();
            HandleMotorData hm = new HandleMotorData();
            hm.start();
            bm.debugPrint("Motorcontroller data polling started.");
        } else {
            bm.debugPrint("ERROR: Motorcontroller data polling already running.");
        }
    }
    public void stopMotor() {
        if (bm.isMotorPollingActive()) {
            bm.setMotorPollingActive(false);
            i2cDev.stopPollingMotorcontroller();
            bm.debugPrint("Motorcontroller data polling stopped.");
        } else {
            bm.debugPrint("ERROR: Motorcontroller data polling already stopped.");
        }
    }
    public void startLidar() {
        if (!bm.isLidarInitialized()) {
            bm.debugPrint("Warning: Lidarcontroller is not initialized. An Error might occur.");
        }
        if (!bm.isLidarPollingActive()) {
            bm.setLidarPollingActive(true);
            i2cDev.startPollingLidar();
            HandleLidarData hl = new HandleLidarData();
            hl.start();
            bm.debugPrint("Lidar data polling started.");
        } else {
            bm.debugPrint("ERROR: Lidar data polling already running.");
        }
    }
    public void stopLidar() {
        if (bm.isLidarPollingActive()) {
            bm.setLidarPollingActive(false);
            i2cDev.stopPollingLidar();
            bm.debugPrint("Lidar data polling stopped.");
        } else {
            bm.debugPrint("ERROR: Lidar data polling already stopped.");
        }
    }

    public void handleClient(String msg) {
            //Format example:   msg = "move:forward:255"
            if (msg.contains("program")) {
                handleClient_program(msg);
            } else {
                if (msg.contains("move")) {
                    handleClient_move(msg);
                } else {
                    if (msg.contains("bite")) {
                        handleClient_bite(msg);
                    }
                }
            }
        }//End: handleClient
    
    public void handleClient_bite(String msg) {
        bm.debugPrint("handleClient has incoming message:" + msg);
        String[] s = msg.split(":");
        switch(s[1]) {
            case "perform":     bm.debugPrint("Bite test requested...");
                                String result = "bite:passed:" + String.join("-", "1", "test");
                                bm.addNewOutgoingMessage(result);
            case "calibrate":   if (!bm.getIMUCalStarted()) {
                                    bm.debugPrint("Calibration IMU requested...");
                                    CalibrateIMU cal = new CalibrateIMU();
                                    cal.start();
                                } else {
                                    bm.setIMUCalAbortFlag(true);
                                }
        }
    }//End: handleCLient_check
    
    class CalibrateIMU extends Thread {
        @Override
        public void run() {
            bm.setIMUCalStarted(true);
            boolean cal = false;
            while (!cal && !bm.getIMUCalAbortFlag()) {
                try {
                    cal = imu.isCalibrated();
                    int sys = imu.getCalibration().sys;
                    bm.setIMUSysCalibrationState(sys);
                    int mag = imu.getCalibration().mag;
                    bm.setIMUMagCalibrationState(mag);
                    int gyro = imu.getCalibration().gyro;
                    bm.setIMUGyroCalibrationState(gyro);
                    int accel = imu.getCalibration().accel;
                    bm.setIMUAccelCalibrationState(accel);
                    
                    String sysCal = /*"Sys:" +*/ String.valueOf(sys);
                    String magCal = /*"Mag" +*/ String.valueOf(mag);
                    String gyroCal = /*"Gyro" +*/ String.valueOf(gyro);
                    String accelCal = /*"Accel" +*/ String.valueOf(accel);
                    String calResult = "bite:calibrate:" + String.join("-", sysCal, magCal, gyroCal, accelCal);
                bm.addNewOutgoingMessage(calResult);
                bm.debugPrint(calResult);
                } catch (IOException ex) {
                    bm.debugPrint("ERROR: unable to retrieve calibration state from imu.");
                    bm.debugErrorPrint(ex);
                }
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ex) {
                    bm.debugPrint("ERROR: Unable to pause Thread...");
                    bm.debugErrorPrint(ex);
                }
            }
            bm.debugPrint("Calibration stopped.");
            try {
                if (imu.isCalibrated()) {
                    bm.addNewOutgoingMessage("bite:calibrate:true");
                    bm.setIMUCalibrated(true);
                }
            } catch (IOException ex) {
                bm.debugPrint("ERROR: unable to get calibration State.");
                bm.addNewOutgoingMessage("log:ERROR: unable to get calibration State.");
                bm.debugErrorPrint(ex);
            }
            bm.setIMUCalStarted(false);
            bm.setIMUCalAbortFlag(false);
        }//End: run()
    }//End: INNER class CalibrateIMU

    public void handleClient_program(String msg) {
        byte[] dataOut = new byte[4];
        String[] s = msg.split(":");
        switch(s[1]) {
            case "imu":     switch(s[2]) {
                                case "start":   startIMU();
                                                break;
                                case "stop":    stopIMU();
                                                break;
                                default:        bm.debugPrint("Unknown command: " + msg);
                            }
                            break;
            case "motor":   switch(s[2]) {
                                case "start":   startMotor();
                                                break;
                                case "stop":    stopMotor();
                                                break;
                                default:        bm.debugPrint("Unknown command: " + msg);
                            }
                            break;
            case "lidar":   switch(s[2]) {
                                case "start":   startLidar();
                                                break;
                                case "stop":    stopLidar();
                                                break;
                                default:        bm.debugPrint("Unknown command: " + msg);
                            }
                            break;
            default:        bm.debugPrint("Unknown command: " + msg);
        }
    }//End: handleClient_program()
    
    public void handleClient_move(String msg) {
        byte[] dataOut = new byte[4];
        String[] s = msg.split(":");
        short speed = regulateMotorVoltage((short) Integer.parseInt(s[2]), bm.getSystemVoltage());
        short cmd;
        switch (s[1]) {
            case "stop":                        cmd = 100;
                                                break;
            case "forward":                     cmd = 102;
                                                break;
            case "backward":                    cmd = 103;
                                                break;
            case "left":                        cmd = 104;
                                                break;
            case "right":                       cmd = 105;
                                                break;
            case "leftdiagonalforward":         cmd = 106;
                                                break;
            case "leftdiagonalreverse":         cmd = 107;
                                                break;
            case "rightdiagonalforward":        cmd = 108;
                                                break;
            case "rightdiagonalreverse":        cmd = 109;
                                                break;
            case "cwrotationaboutmasscenter":   cmd = 110;
                                                break;
            case "ccwrotationaboutmasscenter":  cmd = 111;
                                                break;
            case "cwrotationaboutmotor3":       cmd = 112;
                                                break;
            case "ccwrotationaboutmotor3":      cmd = 113;
                                                break;
            case "cwrotationaboutmotor4":       cmd = 114;
                                                break;
            case "ccwrotationaboutmotor4":      cmd = 115;
                                                break;
            case "cwrotationaboutrearaxis":     cmd = 116;
                                                break;
            case "ccwrotationaboutrearaxis":    cmd = 117;
                                                break;
            case "cwrotationaboutfrontaxis":    cmd = 118;
                                                break;
            case "ccwrotationaboutfrontaxis":   cmd = 119;
                                                break;
            default:                            cmd = 100;
                                                break;
        }
        dataOut = PAMSRV_I2C_command.convertTwoShortsTo4ByteArray(cmd, speed);
        i2cDev.sendI2C(bm.getI2CMotorControllerAddress(), dataOut);
    }//End: handleClient_move()
    
    
    
    class HandleIMUData extends Thread {
        @Override
        public void run() {
                while (bm.isIMUPollingActive()) {
                    if (bm.isIMUDataAvailable()) {
                        //imu:SysCal:AccelCal:MagCal:GyroCal:Heading:Roll:Pitch:Temp
                        String calState = bm.getIMUSysCalibrationState() +":"+ 
                                bm.getIMUAccelCalibrationState() +":"+ bm.getIMUMagCalibrationState() +":"+ bm.getIMUGyroCalibrationState();
                        String vector = bm.getIMUHeading() +":"+ bm.getIMURoll() +":"+ bm.getIMUPitch();
                        String temp = String.valueOf(bm.getIMUTemp());
                        String msg = "imu:" + calState + ":" + vector +":"+ temp;
                        bm.setIMUDataFlag(false);
                        bm.addNewOutgoingMessage(msg);
                    }
                }
        }//End: run()
    }//End: INNER class HandleIMUData
    
    class HandleMotorData extends Thread {
        @Override
        public void run() {
            while (true) {
                if (bm.isMotorPollingActive()) {
                    if (bm.isMotorDataAvailable()) {
                        byte[] temp = bm.getMotorData();
                        bm.setMotorData(null);
                        bm.setMotorDataFlag(false);
                        short voltage = PAMSRV_I2C_command.convertTwoBytesToShort(temp[0], temp[1]);
                        short current = PAMSRV_I2C_command.convertTwoBytesToShort(temp[2], temp[3]);
                        String msg = "motorcontroller:" + voltage + ":" + current;
                        bm.addNewOutgoingMessage(msg);
                        bm.debugPrint(msg);
                    }
                }
            }
        }//End: run()
    }//End: INNER class HandleMotorData
    
    class HandleLidarData extends Thread {
        @Override
        public void run() {
            while (true) {
                if (bm.isLidarPollingActive()) {
                    if (bm.isLidarDataAvailable()) {
                        byte[] temp = bm.getLidarData();
                        bm.setLidarData(null);
                        bm.setLidarDataFlag(false);
                        bm.setLidarAngle(PAMSRV_I2C_command.convertTwoBytesToShort(temp[0], temp[1]));
                        bm.setLidarDistance(PAMSRV_I2C_command.convertTwoBytesToShort(temp[2], temp[3]));
                        checkSafetyDistance(bm.getLidarDistance());
                        String msg = "lidarcontroller:" + bm.getLidarAngle() + ":" + bm.getLidarDistance();
                        bm.addNewOutgoingMessage(msg);
                        bm.debugPrint(msg);
                    }
                }
            }
        }//End: run()
        
        public void checkSafetyDistance(short distance) {
            if (distance <= bm.getSafetyDistance()) {
                byte[] data = new byte[4];
                if (!bm.wasSafetyDistanceNoticed()) {
                    data = PAMSRV_I2C_command.convertTwoShortsTo4ByteArray((short)100, (short)0);
                    i2cDev.sendI2C(bm.getI2CMotorControllerAddress(), data);
                    bm.setSafetyDistanceNoticed(true);
                }
            } else {
                bm.setSafetyDistanceNoticed(false);
            }
        }//End: checkSafetyDistance()
    }//End: INNER class HandleLidarData
           
    public short regulateMotorVoltage(short supposedspeed, short voltage) {
        short maxMotorVoltage = 1200;//in cV oder 10mV
        short val = (short) (supposedspeed * (maxMotorVoltage / voltage));
        if (val > 255) {
            return 255;
        } else {
            if (val < 0) {
                return 0;
            } else {
                return val;
            }
        }
    }//End: regulateMotorVoltage()
    
}//End: class PAMSRV_controller
