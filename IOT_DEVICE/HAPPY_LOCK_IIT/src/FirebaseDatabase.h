
#ifndef FIREBASE_DATABSE_H
#define FIREBASE_DATABSE_H

class FirebaseDatabase {
public:
  FirebaseDatabase();
  void SETUP();
  void bulbOnOff(int bulbNumber, int status);
  void doorLockUnlock(int status);
  void changeMode(int mode);
  void doorLogWrite(int status);
  void run();
};

extern FirebaseDatabase firebaseDatabase;

#endif
