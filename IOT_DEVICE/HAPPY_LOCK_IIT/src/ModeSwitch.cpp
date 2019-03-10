
#include <Arduino.h>

#include "ActionSwitch.h"
#include "Buzzer.h"
#include "Display.h"
#include "Door.h"
#include "ModeSwitch.h"
#include "RelayBulbs.h"
#include "FirebaseDatabase.h"

#define MOD_CHANGE_SWITCH 15

int modChangeSwitchState = 0, modChangeAppState = 0;
int mod = 0; // 0 = ManualDoor, 1 = Manualights, 2 = FullyAuto,
// 3 = AutoDoor/Manualights, 4 = Autoights/ManualDoor

bool modChangeByApp = false;

ModeSwitch::ModeSwitch() {}

void ModeSwitch::SETUP() { pinMode(MOD_CHANGE_SWITCH, INPUT); }

void ModeSwitch::modControl() {
  modChangeSwitchState = digitalRead(MOD_CHANGE_SWITCH);
  if (modChangeSwitchState == 1 || modChangeAppState == 1) // catch change
  {
    if (mod == 4) {
      mod = 0;
      if (modChangeByApp)
        mod = 4;

    } else {
      mod++;
      if (modChangeByApp)
        mod--;
    }
    firebaseDatabase.changeMode(mod);
    buzzer.beep(50, 3);
    delay(100);
    display.loading();
    modChangeAppState = 0;
    modChangeByApp = false;
  }
  display.printHome();

  switch (mod) {
  case 0:
    display.print(2, 1, "Manual Door");
    display.print(-1, 2, "Lock/Unlock");
    actionSwitch.doorLockControl();
    break;
  case 1:
    display.print(2, 1, "Manual Lights");
    display.print(0, 2, "On / Off");
    actionSwitch.lightsControl();
    break;
  case 2:
    // Auto lights.
    display.print(3, 1, "Fully Auto");
    display.print(-3, 2, "System Enable");
    // relayBulbs.onOffBySensor(1, virtualLight1Sensativ);
    // relayBulbs.onOffBySensor(2, virtualLight2Sensativ);
    relayBulbs.onOffBySensor(1, 400);
    relayBulbs.onOffBySensor(2, 1000);
    // autoDoor();
    break;
  case 3:
    display.print(3, 1, "Auto Door");
    display.print(-2, 2, "Manual Lights");
    //  autoDoor();
    actionSwitch.lightsControl();
    break;
  case 4:
    display.print(2, 1, "Auto Lights");
    display.print(-1, 2, "Manual Door");
    relayBulbs.onOffBySensor(1, 400);
    relayBulbs.onOffBySensor(2, 1000);
    actionSwitch.doorLockControl();
    break;
  }
}

void ModeSwitch::changeMode(int modeId) {
  modChangeAppState = 1;
  mod = modeId;
  modChangeByApp = true;
}

void ModeSwitch::setMode(int modeId) { mod = modeId; }

int ModeSwitch::getCurrentMode() { return mod; }

ModeSwitch modeSwitch = ModeSwitch();
