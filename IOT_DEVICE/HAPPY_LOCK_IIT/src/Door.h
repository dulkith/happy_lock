
#ifndef DOOR_H
#define DOOR_H

class Door {
public:
  Door();
  void SETUP();
  void lock();
  void unlock();
  bool isDoorLock();
  void doorLockControl();
};

extern Door door;

#endif
