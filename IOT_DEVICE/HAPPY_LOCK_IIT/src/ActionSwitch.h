
#ifndef ACTION_SWITCH_H
#define ACTION_SWITCH_H

class ActionSwitch {
public:
  ActionSwitch();
  void SETUP();
  void lightsControl();
  void doorLockControl();
};

extern ActionSwitch actionSwitch;

#endif
