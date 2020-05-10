/*
*    creates i2c bus and devices and ensures I2C communication
*/
package server_PAM_SRV;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PAMSRV_I2C_controller {
    
    private PAMSRV_model bm;
    private PAMSRV_controller controller;
    private I2CBus bus1;
    private I2CDevice motor;
    private I2CDevice lidar;
    private static BNO055 imu;
    
    private Timer pollingIMU;
    private Timer pollingMotor;
    private Timer pollingLidar;
    
    
    public PAMSRV_I2C_controller(PAMSRV_controller controller, PAMSRV_model bm) {
        this.bm = bm;
        this.controller = controller;
        bm.debugPrint("Trying to start I2C...");
        try {
            bus1 = I2CFactory.getInstance(I2CBus.BUS_1);
            bm.setI2CRunning(true);
            bm.debugPrint("I2C bus creation successfull.");
        } catch (I2CFactory.UnsupportedBusNumberException ex) {
            bm.debugPrint("ERROR: No such I2C-bus number...");
            bm.debugErrorPrint(ex);
        } catch (IOException ex) {
            bm.debugPrint("ERROR: unable to open i2c bus...");
            bm.debugErrorPrint(ex);
        }
        
        imuStartUp();
        lidarStartUp();
        motorStartUp();
    }//End: Konstruktor
    
    public BNO055 getIMU() {
        return imu;
    }
    
    private void imuStartUp() {
        bm.debugPrint("Trying to create IMU I2C Device...");
        try {
            imu = BNO055.getInstance(BNO055.opmode_t.OPERATION_MODE_IMUPLUS, BNO055.vector_type_t.VECTOR_EULER, I2CBus.BUS_1);
        } catch (I2CFactory.UnsupportedBusNumberException ex) {
            bm.debugPrint("ERROR: Unsupported Bus Number while creating IMU Device.");
            bm.debugErrorPrint(ex);
        } catch (IOException ex) {
            bm.debugPrint("ERROR: unable to create IMU Device.");
            bm.debugErrorPrint(ex);
        }
        while(!imu.isSensorPresent()) {}//warten auf imu
        bm.setIMUPresent(true);
        bm.debugPrint("BNO055 IMU created and recognized.");
    }
    private void lidarStartUp() {
        bm.debugPrint("Trying to create Lidar I2C Device...");
        try {
            lidar = bus1.getDevice(bm.getI2CLidarControllerAddress());
        } catch (IOException ex) {
            bm.debugPrint("ERROR: unable to create Lidar I2C Device.");
            bm.debugErrorPrint(ex);
        }
        bm.debugPrint("Trying to send start command to Lidar...");
        boolean res;
        res = sendI2C(bm.getI2CLidarControllerAddress(), PAMSRV_I2C_command.getStartCmd());
        if (res) {
            bm.debugPrint("Sending successfull.");
            bm.setLidarInitialized(res);
        } else {
            bm.debugPrint("ERROR: unable to send start command.");
        }
    }
    
    private void motorStartUp() {
        bm.debugPrint("Trying to create Motor I2C Device...");
        try {
            motor = bus1.getDevice(bm.getI2CMotorControllerAddress());
        } catch (IOException ex) {
            bm.debugPrint("ERROR: unable to create Motor I2C Device.");
            bm.debugErrorPrint(ex);
        }
        bm.debugPrint("Trying to send start command to Motor...");
        try {
            //boolean res;
            //res = sendI2C(bm.getI2CMotorControllerAddress(), PAMSRV_I2C_command.getStartCmd());
            motor.write(PAMSRV_I2C_command.getStartCmd(), 0, 4);
            bm.debugPrint("Sending successfull.");
            bm.setMotorInitialized(true);
        } catch (IOException ex) {
            bm.debugPrint("ERROR: unable to send motorcontroller start command.");
            bm.debugErrorPrint(ex);
        }
        /*
        if (res) {
            bm.debugPrint("Sending successfull.");
            bm.setMotorInitialized(res);
        } else {
            bm.debugPrint("ERROR: unable to send start command.");
        }
        */
    }
    
    public void startPollingIMU() {
        pollingIMU = new Timer();
        pollingIMU.schedule(new PollInertialMeasurementUnit(), 0, bm.getIMUPollingInterval());
    }
    public void stopPollingIMU() {
        pollingIMU.cancel();
    }
    public void startPollingMotorcontroller() {
        pollingMotor = new Timer();
        pollingMotor.schedule(new PollMotorController(), 0, bm.getMotorControllerPollingInterval());
    }
    public void stopPollingMotorcontroller() {
        pollingMotor.cancel();
    }
    public void startPollingLidar() {
        pollingLidar = new Timer();
        pollingLidar.schedule(new PollLidarController(), 0, bm.getLidarControllerPollingInterval());
    }
    public void stopPollingLidar() {
        pollingLidar.cancel();
    }
    
    public boolean sendI2C(int device, byte[] data) {
        try {
            switch (device) {
                case 0x37:  {
                            motor.write(data, 0, 4);
                            break;
                            }
                case 0x38:  {
                            lidar.write(data, 0, 4);
                            break;
                            }
                default:    {
                            bm.debugPrint("ERROR: No such device...");
                            return false;
                            }
            }
            return true;
        } catch (IOException ex) {
            bm.debugPrint("ERROR: Unable to write to device " + Integer.toHexString(device) +"...");
            bm.debugErrorPrint(ex);
            return false;
        }
    }//End: sendI2C()
    
    public void broadcastI2C(byte[] data) {
        try {
            motor.write(data, 0, 4);
            lidar.write(data, 0, 4);
        } catch (IOException ex) {
            bm.debugPrint("ERROR: Unable to send broadcast");
            bm.debugErrorPrint(ex);
        }
    }//End: broadcastI2C
    
    public int getIMUStatus() {
        int stat = 0;
        boolean calibrated = false;
        if (imu.isSensorPresent()) {
            stat++;
            if (imu.isInitialized()) {
                stat++;
                try {
                    calibrated = imu.isCalibrated();
                } catch (IOException ex) {
                    return (stat*(-1));
                }
                if (calibrated) {
                    stat++;
                }
            }
        }
        /*      falls sensor erkannt:                                   1
        *       falls sensor initialisiert (und voriger fall):          2
        *       falls sensor calibriert (und vorige fälle):             3
        *
        *       falls auf calibration nicht zugegriffen werden kann:    wie oben nur * -1
        */
        return stat;
    }//End: getIMUStatus
    
    class PollInertialMeasurementUnit extends TimerTask {
        @Override
        public void run() {
                try {
                    bm.setIMUSysCalibrationState(imu.getCalibration().sys);
                    bm.setIMUAccelCalibrationState(imu.getCalibration().accel);
                    bm.setIMUGyroCalibrationState(imu.getCalibration().gyro);
                    bm.setIMUMagCalibrationState(imu.getCalibration().mag);
                    bm.setIMUTemp(imu.getTemp());
                    double[] temp = new double[3];
                    temp = imu.getVector();
                    bm.setIMUHeading(temp[0]);
                    bm.setIMURoll(temp[1]);
                    bm.setIMUPitch(temp[2]);
                    bm.setIMUContHeading(imu.getHeading());
                    bm.setIMUDataFlag(true);
                } catch (IOException ex) {
                    bm.debugPrint("ERROR: Unable to fetch IMU data...");
                    bm.debugErrorPrint(ex);
                }
        }//End: run()
    }//End: INNER class PollInertialMeasurementUnit
    
    class PollMotorController extends TimerTask {
        @Override
        public void run() {
            byte[] temp = new byte[4];
            try {
                motor.read(temp, 0, 4);
                bm.setMotorData(temp);
                bm.setMotorDataFlag(true);
            } catch (IOException ex) {
                bm.debugPrint("ERROR: Unable to read from MotorController...");
                bm.debugErrorPrint(ex);
            }
        }//End: run()
    }//End: INNER class pollMotorController
    
    class PollLidarController extends TimerTask {
        @Override
        public void run() {
            byte[] temp = new byte[4];
            try {
                lidar.read(temp, 0, 4);
                bm.setLidarData(temp);
                bm.setLidarDataFlag(true);
            } catch (IOException ex) {
                bm.debugPrint("ERROR: Unable to read from LidarController...");
                bm.debugErrorPrint(ex);
            }
        }
    }//End: INNER class pollLidarController
    
}//End: clsss PAMSRV_I2C_controller
