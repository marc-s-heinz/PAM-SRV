#ifndef MyCurrentSensor_h
#define MyCurrentSensor_h

#include <Arduino.h>
	
	class MyCurrentSensor
	{
		public:
			MyCurrentSensor(int pin);
			float	getCurrent();
		private:
			int _pin;
			int _valADC;
			float _current;
	};
	
#endif