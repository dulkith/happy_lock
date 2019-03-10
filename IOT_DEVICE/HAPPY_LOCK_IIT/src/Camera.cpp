
#include <Arduino.h>
#include <Servo.h>

#include "Camera.h"
#include "Ultrasonic.h"

#define CAMERA_SERVO 4

int cameraState = 0; // is the switch on = 1 or off = 0
Servo cameraServo;   // create servo object to control a servo
int cameraServoPos =
    0; // variable to store the servo position (0-Unlock, 1-Lock)

Camera::Camera() {}

void Camera::SETUP() {
  cameraServo.attach(CAMERA_SERVO);
  cameraServo.write(25);
}

void Camera::rotate() {
  if (ultrasonic.getStartDistance() != ultrasonic.getDistance()) {
  }
}

void Camera::autoRotate() {
  
}

Camera camera = Camera();
