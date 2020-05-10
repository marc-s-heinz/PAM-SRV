#include "Arduino.h"
#include "MyVoltageSensor.h"

MyVoltageSensor::MyVoltageSensor(int pin)
{
	_pin = pin;
}

float MyVoltageSensor::getVoltage()
{
	_valADC = analogRead(_pin);//reads 10 Bit value
	_voltage = (_valADC/4.092);
	_voltage = (_voltage/10);
	_voltage = _voltage * (3.3/5);
	return _voltage;
}