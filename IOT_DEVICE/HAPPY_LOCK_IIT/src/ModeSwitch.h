
#ifndef MODE_SWITCH_H
#define MODE_SWITCH_H

class ModeSwitch {
public:
  ModeSwitch();
  void SETUP();
  void modControl();
  void changeMode(int modeId);
  int getCurrentMode();
  void setMode(int modeId);
};

extern ModeSwitch modeSwitch;

#endif
