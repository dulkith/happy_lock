
#ifndef MEMORY_H
#define MEMORY_H

class Memory {
public:
  Memory();
  void SETUP();
  float readDoorLocationLat();
  float readDoorLocationLng();
  void writeDoorLocation(float doorLocationLat, float doorLocationLng);
  int EEPROMAnythingWrite(int pos, char *zeichen, int lenge);
  int EEPROMAnythingRead(int pos, char *zeichen, int lenge);
};

extern Memory memory;

#endif
