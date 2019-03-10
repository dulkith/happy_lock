
#include <Arduino.h>

#include "ActionSwitch.h"
#include "Door.h"
#include "FirebaseDatabase.h"
#include "RelayBulbs.h"

#define ACTION_SWITCH 13

static int switchState = 0;
static int lightsMods = 0; // is the switch on = 1 or off = 0

ActionSwitch::ActionSwitch() {}

void ActionSwitch::SETUP() { pinMode(ACTION_SWITCH, INPUT); }

void ActionSwitch::lightsControl() {
  switchState = digitalRead(ACTION_SWITCH);
  if (switchState == 1) // catch change
  {
    switch (lightsMods) {
    case 0:
      relayBulbs.onOff(1, true);
      relayBulbs.onOff(2, false);
      firebaseDatabase.bulbOnOff(1, 1);
      firebaseDatabase.bulbOnOff(2, 0);
      lightsMods++;
      break;
    case 1:
      relayBulbs.onOff(1, false);
      relayBulbs.onOff(2, true);
      firebaseDatabase.bulbOnOff(1, 0);
      firebaseDatabase.bulbOnOff(2, 1);
      lightsMods++;
      break;
    case 2:
      relayBulbs.onOff(1, true);
      relayBulbs.onOff(2, true);
      firebaseDatabase.bulbOnOff(1, 1);
      firebaseDatabase.bulbOnOff(2, 1);
      lightsMods++;
      break;
    case 3:
      relayBulbs.onOff(1, false);
      relayBulbs.onOff(2, false);
      firebaseDatabase.bulbOnOff(1, 0);
      firebaseDatabase.bulbOnOff(2, 0);
      lightsMods = 0;
      break;
    }
  }
}

void ActionSwitch::doorLockControl() {
  switchState = digitalRead(ACTION_SWITCH);
  if (switchState == 1) // catch change
  {
    door.doorLockControl();
  }
}

ActionSwitch actionSwitch = ActionSwitch();
