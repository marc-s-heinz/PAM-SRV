#ifndef MyMotorController_h
#define MyMotorController_h

	#include <Arduino.h>
	
	#define FWD	1
	#define BWD	0
	
	class MyMotorController
	{
		public:
			MyMotorController (int pinFWD, int pinBWD);
			//MyMotorController (int pinFWD, int pinBWD, int pinEncoderA, int pinEncoderB);
			//void setSpeed(int speed);
			void stop();
			void drive(int direction, int speed);
		private:
			int _pinFWD;
			int _pinBWD;
			//int pinEncoderA;
			//int pinEncoderB;
			int _speed;
			int _direction;
	}; 
	
#endif