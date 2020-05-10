/*
*	Version 0.1
*	01.04.2020, 14:00
*	TODO:
*		-	Regler für Drehzahl wenn Encoder verbaut
*/


#include "MyMotorController.h"
#include "Arduino.h"

MyMotorController::MyMotorController(int pinFWD, int pinBWD)
{
	_pinFWD	= pinFWD;
	_pinBWD	= pinBWD;
	pinMode(_pinFWD, OUTPUT);
	pinMode(_pinBWD, OUTPUT);
	analogWrite(_pinFWD, 0);
	analogWrite(_pinBWD, 0);
}

/*
MyMotorController::MyMotorController (int pinFWD, int pinBWD, int pinEncoderA, int pinEncoderB)
{
				   this->pinFWD	= pinFWD;
				   this->pinBWD	= pinBWD;
				   this->pinEncoderA	= pinEncoderA;
				   this->pinEncoderB	= pinEncoderB;
}
*/

/*
void MyMotorController::setSpeed(int speed)
{
	_speed	= speed;
}
*/

void MyMotorController::stop()
{
	analogWrite(_pinFWD, 0);
	analogWrite(_pinBWD, 0);
}

void MyMotorController::drive(int direction, int speed)
{
	_direction	= direction;
	_speed		= speed;
	if (_direction == 1) {
		analogWrite(_pinFWD, _speed);
	}
	else if (_direction == 0) {
		analogWrite(_pinBWD, _speed);
	}
	else return;
}
	