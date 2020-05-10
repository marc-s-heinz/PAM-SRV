#include "Arduino.h"
#include "MyCurrentSensor.h"

MyCurrentSensor::MyCurrentSensor(int pin)
{
	_pin = pin;
}

float MyCurrentSensor::getCurrent()
{
	_valADC = analogRead(_pin);//reads 10 Bit value
	_valADC = _valADC * (5000/1024);//converts Bit value in mV
	_valADC = _valADC - 2500;//offset (value at 0A is 2500mV)
	_valADC = _valADC * (3.3/5);//correction for 3.3V Microcontroller
	_current = _valADC / 100;//mV per Amp = 100
	if (_current < 0) {
		_current = 0.0;
	}
	return _current;
}