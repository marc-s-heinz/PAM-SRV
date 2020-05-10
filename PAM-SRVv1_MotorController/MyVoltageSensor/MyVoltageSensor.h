#ifndef MyVoltageSensor_h
#define MyVoltageSensor_h

#include <Arduino.h>
	
	class MyVoltageSensor
	{
		public:
			MyVoltageSensor(int pin);
			float	getVoltage();
		private:
			int _pin;
			int _valADC;
			float _voltage;
	};
	
#endif