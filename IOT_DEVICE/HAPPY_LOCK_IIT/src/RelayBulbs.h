
#ifndef RELAY_BULBS_H
#define RELAY_BULBS_H

class RelayBulbs {
public:
  RelayBulbs();
  void SETUP();
  void onOff(int bulbNumber, bool action);
  void onOffBySensor(int bulbNumber, int ldrSensative);
  bool isBulbOn(int bulbNumber);
};

extern RelayBulbs relayBulbs;

#endif
