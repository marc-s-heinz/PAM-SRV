# PAM-SRV

PAM-SRV ist eine Abkürzung für
"Partial Automated Meccanum-wheel Surveilance Robot Vehicle"


![PAMSRV1](/pictures/pam1.png)


![PAMSRV2](/pictures/pam2.png)


Als Hauptrechner und WifiServer dient ein Raspberry Pi 4 der mit Java programiert wurde.
Daran sind mittels I²C zwei Arduino Due und die Inertial Measurement Unit angeschlossen.
Die Arduinos sind zuständig für die Ansteuerung der Motoren und Auslesen der Sensoren und des Lidars.
Über ein ebenfalls mit Java geschreiebenes Clientprogramm kann man sich mit dem Roboter verbinden und ihn steuern.

Derzeitige Probleme (TO-DO):
- Gesamtsystem noch nicht getestet
- Probleme mit I²C: wahrscheinlich aufgrund der Pull-Up Widerstände insbesondere beim LidarController oder des Clock-Stretchings bei den   Software-I²C-Slaves
- Kein geregeltes beenden der client-Server Verbindung

Zukümftige Verbesserungen:
- Raspberry Pi Camera zufügen
- Anderes Chassis/ Gehäuse
- Neue Motoren mit funktionierenden Encodern, neue Motorcontroller mit PID Regelung
- Erweiterung der IMU Implementierung
- Status LEDs oder Display
- Verwendung von LoRa Technologie anstatt des Wifi Servers mit geregeltem Beenden der Verbindung
- Implementierung OpenCV
- Implementierung ROS
- SLAM Funktionalität


Der Roboter besteht aus (BOM):
- 1x  Raspberry Pi 4 Modell B 4 GB (https://www.amazon.de/dp/B07TC2BK1X/ref=cm_sw_em_r_mt_dp_U_kcuTEb4A7DN07)
- 2x  Arceli Due 2012 R3 (Arduino Due Klon) (https://www.amazon.de/dp/B07MPYQ4BH/ref=cm_sw_em_r_mt_dp_U_eiuTEbXPE1EW8)
- 1x  RPLidar A1M8 360-Grad-Laserscanner (https://www.amazon.de/dp/B07T6D48RL/ref=cm_sw_em_r_mt_dp_U_duuTEbQXMZ7WZ)
- 1x  Adafruit BNO055 9-DOF Absolute Orientation IMU (https://www.amazon.de/dp/B017PEIGIG/ref=cm_sw_em_r_mt_dp_U_iCuTEb5FE4TMV)
- 2x  AZDelivery TXS0108E 8 Channel Logic Level Converter (https://www.amazon.de/dp/B07V1FY9W5/ref=cm_sw_em_r_mt_)
- 1x  Arceli Spannungssensor DC 0-25V (https://www.amazon.de/dp/B07RFJYSM4/ref=cm_sw_em_r_mt_dp_U_sguTEbASWX6CT)
- 1x  AZDelivery Stromsensor ACS712 20A (https://www.amazon.de/dp/B07CMZXSHB/ref=cm_sw_em_r_mt_dp_U_LyuTEbSJWVVQ7)
- 1x  Arceli AMS1117-3.3 Spannungsregler (https://www.amazon.de/dp/B07MY2NMQ6/ref=cm_sw_em_r_mt_dp_U_YouTEb7QS44FP)
- 3x  MissBirdler Dual 5V USB Spannungswandler (https://www.amazon.de/dp/B071FSWGJS/ref=cm_sw_em_r_mt_dp_U_.puTEbTKZM28H)
- 2x  Arceli L298N Dual H Brücke (https://www.amazon.de/dp/B07MY33PC9/ref=cm_sw_em_r_mt_dp_U_IsuTEbJJQVS2X)
- 1x  Toogoo 4wd Smart Robot Car Chassis (https://www.amazon.de/dp/B07VSLZ4CJ/ref=cm_sw_em_r_mt_dp_U_8suTEbX4SYN94)

Geplant:
- 1x  KeeYees 4 Kanal IIC Logik Level Konverter (https://www.amazon.de/dp/B07LG6RK7L/ref=cm_sw_em_r_mt_dp_U_FjuTEbAKEKRJ1)
- 4x  CQRobot DC Geared Motor /w Encoder (12V, 251rpm, 18kg.cm) (https://www.amazon.de/dp/B07D943BKF/ref=cm_sw_em_r_mt_dp_U_.luTEbRET34P3)
