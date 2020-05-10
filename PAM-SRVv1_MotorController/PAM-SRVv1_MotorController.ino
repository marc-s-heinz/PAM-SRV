/*
 *  Sketch for Arduino Due
 * 
 *  PAM-SRV v1.0 - MotorController, Voltage- + Current-Sensor
 *  
 *  TODO:
 *    - Drehzahlregler für Motordrehzahl wenn Encoder vorhanden
 *    - Stromsensor testen
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
 #include <MyCurrentSensor.h>
 #include <MyVoltageSensor.h>
 #include <MyMotorController.h>

/**********************
 *    PINBELEGUNG
 **********************/
//H-Bridge Motor 1+2
#define Motor2_BWD  8     //weiss     H-Bridge1 IN 1
#define Motor2_FWD  9     //gelb      H-Bridge1 IN 2
#define Motor1_FWD  10    //schwarz   H-Bridge1 IN 3
#define Motor1_BWD  11    //grün      H-Bridge1 IN 4
//H-Bridge Motor 3+4
#define Motor3_FWD  4     //weiss     H-Bridge2 IN 1
#define Motor3_BWD  5     //gelb      H-Bridge2 IN 2
#define Motor4_BWD  6     //schwarz   H-Bridge2 IN 3
#define Motor4_FWD  7     //grün      H-Bridge2 IN 4

#define pinVolt A1
#define pinAmp  A2

/*************************************
 *    SONSTIGE GLOBALE VARIABLEN
 *************************************/
 //Datenübertragung
 #define I2C_ADDRESS  0x37
 #define I2C_MASTER 0x42//RaspberryPi 4
 byte dataTransfer[4];
 byte dataIn[4];
 volatile boolean dataAvailableFlag = false;
 volatile short command;
 volatile short cmd_value;
 volatile short response;

 //Sensoren
 MyCurrentSensor amp(pinAmp);
 MyVoltageSensor volt(pinVolt);
 volatile short temp_voltage;
 volatile short temp_current;

 //Motoren
 MyMotorController mot1(Motor1_FWD, Motor1_BWD);//vorne rechts
 MyMotorController mot2(Motor2_FWD, Motor2_BWD);//vorne links
 MyMotorController mot3(Motor3_FWD, Motor3_BWD);//hinten rechts
 MyMotorController mot4(Motor4_FWD, Motor4_BWD);//hinten links
 volatile short cmd_movement;
 volatile short cmd_speed;

 //Allgemeiner Programmablauf
 volatile boolean startFlag = false;
 unsigned long previousMillis = 0;
 const long sensorRefreshInterval = 100;//in ms
 const boolean debugMode = false;


/*************************************
 *          PROGRAMM
 *************************************/
void setup() {
  debugSerialBegin(115200);

  //initialize i2c as slave
  Wire1.begin(I2C_ADDRESS);

  //define callbacks for i2c communication
  Wire1.onReceive(receiveData);
  Wire1.onRequest(sendData);
  
}//End: setup


void loop() {
  if (dataAvailableFlag == true) {
    command = (dataIn[1] << 8) + dataIn[0];
    cmd_value = (dataIn[3] << 8) + dataIn[2];
    dataAvailableFlag = false;
    handleCommand();
  }
  debugPrintln(command + "\t\t" + cmd_value);
 
  if (startFlag == true) {
    unsigned long currentMillis = millis();

    if (currentMillis - previousMillis >= sensorRefreshInterval) {
      previousMillis = currentMillis;//letzte abfragezeitpunkt wird gemerkt
      temp_current = (amp.getCurrent()*100);
      temp_voltage = (volt.getVoltage()*100);
      dataTransfer[0] = lowByte(temp_current);
      dataTransfer[1] = highByte(temp_current);
      dataTransfer[2] = lowByte(temp_voltage);
      dataTransfer[3] = highByte(temp_voltage);
    }
  }//End: if startFlag

}//End: loop



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
 Wire1.write(dataTransfer, 4);
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
  //Motoren
  if (command >= 100 && command < 200) {
    cmd_movement = (command - 100);
    cmd_speed = cmd_value;
    movement(cmd_movement, cmd_speed);
    command = -1;
    cmd_value = -1;
  }
}//End: handleCommand

void movement(int cmd_movement, int cmd_speed) {
  int cmd = cmd_movement;
  int spd = cmd_speed;
/*        Anordnung der Motoren
 * 
 *        Motor 2       Motor 1
 *        
 *        Motor 4       Motor 3
 * 
 */
  switch (cmd) {
    case 0:                             //stop
          mot1.stop();
          mot2.stop();
          mot3.stop();
          mot4.stop();
          break;
    case 2:                             //forward
          mot1.drive(FWD, spd);
          mot2.drive(FWD, spd);
          mot3.drive(FWD, spd);
          mot4.drive(FWD, spd);
          break;
    case 3:                             //backward
          mot1.drive(BWD, spd);
          mot2.drive(BWD, spd);
          mot3.drive(BWD, spd);
          mot4.drive(BWD, spd);
          break;
    case 4:                             //left
          mot1.drive(FWD, spd);
          mot2.drive(BWD, spd);
          mot3.drive(BWD, spd);
          mot4.drive(FWD, spd);
          break;
    case 5:                             //right
          mot1.drive(BWD, spd);
          mot2.drive(FWD, spd);
          mot3.drive(FWD, spd);
          mot4.drive(BWD, spd);
          break;
    case 6:                             //left diagonal forward
          mot1.drive(FWD, spd);
          mot2.drive(FWD, 0);
          mot3.drive(FWD, 0);
          mot4.drive(FWD, spd);
          break;
    case 7:                             //left diagonal reverse
          mot1.drive(FWD, 0);
          mot2.drive(BWD, spd);
          mot3.drive(BWD, spd);
          mot4.drive(FWD, 0);
          break;
    case 8:                             //right diagonal forward
          mot1.drive(FWD, 0);
          mot2.drive(FWD, spd);
          mot3.drive(FWD, spd);
          mot4.drive(FWD, 0);
          break;
    case 9:                             //right diagonal reverse
          mot1.drive(BWD, spd);
          mot2.drive(FWD, 0);
          mot3.drive(FWD, 0);
          mot4.drive(BWD, spd);
          break;
    case 10:                            //CW rotation about mass center
          mot1.drive(FWD, spd);
          mot2.drive(FWD, 0);
          mot3.drive(FWD, 0);
          mot4.drive(BWD, spd);
          break;
    case 11:                            //CCW rotation about mass center
          mot1.drive(FWD, 0);
          mot2.drive(FWD, spd);
          mot3.drive(BWD, spd);
          mot4.drive(FWD, 0);
          break;
          //not tested ->
    case 12:                            //CW rotation about motor3
          mot1.drive(FWD, 0);
          mot2.drive(FWD, spd);
          mot3.drive(FWD, 0);
          mot4.drive(FWD, spd);
          break;
    case 13:                            //CCW rotation about motor3
          mot1.drive(FWD, 0);
          mot2.drive(BWD, spd);
          mot3.drive(FWD, 0);
          mot4.drive(BWD, spd);
          break;
    case 14:                            //CW rotation about motor4
          mot1.drive(FWD, spd);
          mot2.drive(FWD, 0);
          mot3.drive(FWD, spd);
          mot4.drive(FWD, 0);
          break;
    case 15:                            //CCW rotation about motor4
          mot1.drive(BWD, spd);
          mot2.drive(FWD, 0);
          mot3.drive(BWD, spd);
          mot4.drive(FWD, 0);
          break;
    case 16:                            //CW rotation about rear axis
          mot1.drive(BWD, spd);
          mot2.drive(FWD, spd);
          mot3.drive(FWD, 0);
          mot4.drive(FWD, 0);
          break;
    case 17:                            //CCW rotation about rear axis
          mot1.drive(FWD, spd);
          mot2.drive(BWD, spd);
          mot3.drive(FWD, 0);
          mot4.drive(FWD, 0);
          break;
    case 18:                            //CW rotation about front axis
          mot1.drive(BWD, 0);
          mot2.drive(FWD, 0);
          mot3.drive(BWD, spd);
          mot4.drive(FWD, spd);
          break;
    case 19:                            //CCW rotation about front axis
          mot1.drive(BWD, 0);
          mot2.drive(FWD, 0);
          mot3.drive(FWD, spd);
          mot4.drive(BWD, spd);
          break;
    default:
          mot1.stop();
          mot2.stop();
          mot3.stop();
          mot4.stop();
          break;
  }
}//End: movement

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
