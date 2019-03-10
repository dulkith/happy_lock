
/*------------------- Includes -------------------*/

#include <Arduino.h>

#include "ActionSwitch.h"
#include "Buzzer.h"
#include "Display.h"
#include "Door.h"
#include "FirebaseDatabase.h"
#include "ModeSwitch.h"
#include "RelayBulbs.h"
//#include "Ultrasonic.h"

/*-------------- Global Variables --------------*/

/*------------- Vertual Pin Values ------------*/

/*------------------- Setup -------------------*/

void setup() {
  // Debug console
  // Serial.begin(9600);
  //
  relayBulbs.SETUP();
  actionSwitch.SETUP();
  door.SETUP();
  buzzer.SETUP();
  display.SETUP();
  modeSwitch.SETUP();
  // ultrasonic.SETUP();
  firebaseDatabase.SETUP();
}

/*------------------- Loop -------------------*/

void loop() {
  firebaseDatabase.run();
  // actionSwitch.lightsControl();
  // actionSwitch.doorControl();
  modeSwitch.modControl();
}
