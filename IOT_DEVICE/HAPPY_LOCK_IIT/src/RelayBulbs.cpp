
#include <Arduino.h>

#include "Display.h"
#include "FirebaseDatabase.h"
#include "RelayBulbs.h"

// #define RELAY_1 14
// #define RELAY_2 12
#define RELAY_1 3
#define RELAY_2 1
#define LDR A0

RelayBulbs::RelayBulbs() {}

void RelayBulbs::SETUP() {
  pinMode(RELAY_1, OUTPUT);
  pinMode(RELAY_2, OUTPUT);
  pinMode(LDR, INPUT);

  onOff(1, 0);
  onOff(2, 0);
}

/**
    [RelayBulbs::On/Off bulbs from relay.]
    @param bulbNumber [Relay number 1,2,3...]
    @param action     [1=on, 0=off]
*/
void RelayBulbs::onOff(int bulbNumber, bool action) {
  if (bulbNumber == 1 && action == true) {
    digitalWrite(RELAY_1, HIGH);
    display.bulbOff(bulbNumber);
  } else if (bulbNumber == 1 && action == false) {
    digitalWrite(RELAY_1, LOW);
    display.bulbOn(bulbNumber);
  } else if (bulbNumber == 2 && action == true) {
    digitalWrite(RELAY_2, HIGH);
    display.bulbOff(bulbNumber);
  } else if (bulbNumber == 2 && action == false) {
    digitalWrite(RELAY_2, LOW);
    display.bulbOn(bulbNumber);
  }
}

void RelayBulbs::onOffBySensor(int bulbNumber, int ldrSensative) {
  unsigned int ldrRead = analogRead(LDR);
  if (ldrRead < ldrSensative) {
    onOff(bulbNumber, true);
    firebaseDatabase.bulbOnOff(bulbNumber, 0);
  } else {
    onOff(bulbNumber, false);
    firebaseDatabase.bulbOnOff(bulbNumber, 1);
  }
}

bool RelayBulbs::isBulbOn(int bulbNumber) {
  if (bulbNumber == 1) {
    return digitalRead(RELAY_1);
  } else if (bulbNumber == 2) {
    return digitalRead(RELAY_2);
  }
}

RelayBulbs relayBulbs = RelayBulbs();
