
#include <Arduino.h>
#include <Servo.h>

#include "Display.h"
#include "Door.h"
#include "FirebaseDatabase.h"

#define DOOR_SERVO 5

int doorState = 1;    // is the switch on = 1 or off = 0
Servo doorServo;      // create servo object to control a servo
int doorServoPos = 0; // variable to store the servo position (0-Unlock, 1-Lock)

int proDebug = 0; // TODO - debugging?
int geolocation;

Door::Door() {}

void Door::SETUP() {
  doorServo.attach(DOOR_SERVO);
  doorServo.write(55);
}

void Door::lock() {
  if (doorServo.read() != 53) {
    doorState = 1;
    display.doorLock();
    for (doorServoPos = 0; doorServoPos <= 53; doorServoPos++) {
      doorServo.write(doorServoPos);
      delay(10);
    }
  }
}

void Door::unlock() {
  if (doorServo.read() != 0) {
    firebaseDatabase.doorLockUnlock(0);
    doorState = 0;
    display.doorUnLock();
    for (doorServoPos = 53; doorServoPos >= 0; doorServoPos--) {
      doorServo.write(doorServoPos);
      delay(10);
    }
  }
}

bool Door::isDoorLock() {
  if (doorState == 1) {
    return true;
  } else {
    return false;
  }
}

void Door::doorLockControl() {
  if (isDoorLock()) {
    unlock();
    firebaseDatabase.doorLockUnlock(0);
    firebaseDatabase.doorLogWrite(0);
    delay(20);
  } else {
    lock();
    firebaseDatabase.doorLockUnlock(1);
    firebaseDatabase.doorLogWrite(1);
    delay(20);
  }
}

Door door = Door();
