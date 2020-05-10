/*
 *  Sketch for Arduino Due
 *  
 *  PAM_SRV v1.0 - LidarController for RPLidar A1M8 by Slamtec
 *  
 *  TODO: - Test
 *  
 *   MIT License
 *
 *   Copyright (c) 2020 marc-s-heinz
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *   
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *   
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 *   
 */
#include <Wire.h>
#include <RPLidar.h>

#define SLAVE_ADDRESS 0x38
#define RPLIDAR_MOTOR 3

const boolean debugMode = false;

byte dataIn[4];
byte dataOut[4];

const int minimumShowRadius = 150;//in mm
const int minimumQuality = 4;
volatile boolean dataAvailableFlag = false;
volatile short command;
volatile short cmd_value;
volatile boolean startFlag = false;

volatile short errorflag = 0;
volatile short errorcode = 0;
/*    Errorflag & Errorcode
 *     0    - 0  : no start command yet
 *     11   - 11 : waitpoint error
 *     12   - 12 : waitpoint error, device info ok
 *     13   - 13 : waitpoint & device info error
 */

RPLidar lidar;

void setup() {
   debugSerialBegin(115200);
   Serial1.begin(115200);
   lidar.begin(Serial1);

   pinMode(RPLIDAR_MOTOR, OUTPUT);

   // initialize i2c as slave
   Wire1.begin(SLAVE_ADDRESS);
 
   // define callbacks for i2c communication
   Wire1.onReceive(receiveData);
   Wire1.onRequest(sendData);

   dataOut[0] = (errorflag >> 8) & 0xFF;
   dataOut[1] = errorflag & 0xFF;
   dataOut[2] = (errorcode >> 8) & 0xFF;
   dataOut[3] = errorcode & 0xFF;
}//End: setup

void loop() {
  if (dataAvailableFlag == true) {
    command = (dataIn[1] << 8) + dataIn[0];
    cmd_value = (dataIn[3] << 8) + dataIn[2];
    dataAvailableFlag = false;
    handleCommand();
  }
  debugPrint("Incoming command: ");
  debugPrintln(command + "\t\t" + cmd_value);

  if (startFlag == true) {
    analogWrite(RPLIDAR_MOTOR, 255);

    if (IS_OK(lidar.waitPoint())) {
      float distance  = lidar.getCurrentPoint().distance;
      float angle     = lidar.getCurrentPoint().angle;
      byte quality    = lidar.getCurrentPoint().quality;

      if (quality >= minimumQuality && distance >= minimumShowRadius) {
        int ang = (int) (angle * 10);
        int dist = (int) (distance);
    
        dataOut[0] = (ang >> 8) & 0xFF;
        dataOut[1] = ang & 0xFF;
        dataOut[2] = (dist >> 8) & 0xFF;
        dataOut[3] = dist & 0xFF;
    }
  } else {//if waitpoint NOT ok
    setError(11, 11);
    debugPrint("Error: ");
    debugPrintln(errorflag + "\t\t" + errorcode);
    rplidar_response_device_info_t info;
    if (IS_OK(lidar.getDeviceInfo(info, 100))) {
      setError(12, 12);
      debugPrint("Error: ");
      debugPrintln(errorflag + "\t\t" + errorcode);
      analogWrite(RPLIDAR_MOTOR, 255);
      delay(1000);
      lidar.startScan();
    } else {
      setError(13, 13);
      debugPrint("Error: ");
      debugPrintln(errorflag + "\t\t" + errorcode);
      analogWrite(RPLIDAR_MOTOR, 0);
    }
  }
 } else {
  analogWrite(RPLIDAR_MOTOR, 0);
  setError(0, 0);
  debugPrint("Error: ");
  debugPrintln(errorflag + "\t\t" + errorcode);
 }
  
}//End: loop

void setError(short error, short code) {
   errorflag = error;
   errorcode = code;
   //short err = error;
   //short cde = code;
   dataOut[0] = (errorflag >> 8) & 0xFF;
   dataOut[1] = errorflag & 0xFF;
   dataOut[2] = (errorcode >> 8) & 0xFF;
   dataOut[3] = errorcode & 0xFF;
}

// callback for received data
void receiveData(int byteCount){
  int cnt = 0;
   while(Wire1.available()) {
      dataIn[cnt] = Wire1.read();
      cnt++;
   }
   dataAvailableFlag = true;
}//End: receiveData

// callback for sending data
void sendData(){
 Wire1.write(dataOut, 4);
}//End: sendData

void handleCommand() {
  //Programmablauf starten
  if (command == 1 && cmd_value == 0) {
    startFlag = true;
  }
  //Programmablauf stoppen
  if (command == 0 && cmd_value == 1) {
    startFlag = false;
  }
}//End: handleCommand

void debugPrint(String s) {
  if (debugMode) {
    Serial.print(s);
  }
}
void debugPrintln(String s) {
  if (debugMode) {
    Serial.println(s);
  }
}
void debugSerialBegin(int baud) {
  if (debugMode) {
    Serial.begin(baud);
  }
}
