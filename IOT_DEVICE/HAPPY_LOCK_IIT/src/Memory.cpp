
#include <Arduino.h>
#include <EEPROM.h>

#include "Memory.h"

Memory::Memory() {}

void Memory::SETUP() { EEPROM.begin(255); }

float Memory::readDoorLocationLat() {
  float doorLocationLat = 0.0;
  EEPROMAnythingRead(0, reinterpret_cast<char*>(&doorLocationLat), sizeof(doorLocationLat));
  return doorLocationLat;
}

float Memory::readDoorLocationLng() {
  float doorLocationLng = 0.0;
  EEPROMAnythingRead(0, reinterpret_cast<char*>(&doorLocationLng), sizeof(doorLocationLng));
  return doorLocationLng;
}

void Memory::writeDoorLocation(float doorLocationLat,float doorLocationLng) {
  // Float to EEPROM
  EEPROMAnythingWrite(0, reinterpret_cast<char *>(&doorLocationLat),
                      sizeof(doorLocationLat));

  EEPROMAnythingWrite(1, reinterpret_cast<char *>(&doorLocationLng),
                      sizeof(doorLocationLng));

  EEPROM.commit();
}

////////....
////////....

// Write any data structure or variable to EEPROM
int Memory::EEPROMAnythingWrite(int pos, char *zeichen, int lenge) {
  for (int i = 0; i < lenge; i++) {
    EEPROM.write(pos + i, *zeichen);
    zeichen++;
  }
  return pos + lenge;
}

// Read any data structure or variable from EEPROM
int Memory::EEPROMAnythingRead(int pos, char *zeichen, int lenge) {
  for (int i = 0; i < lenge; i++) {
    *zeichen = EEPROM.read(pos + i);
    zeichen++;
  }
  return pos + lenge;
}

Memory memory = Memory();
