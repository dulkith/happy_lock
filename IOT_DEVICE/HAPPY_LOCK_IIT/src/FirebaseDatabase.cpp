
#include <Arduino.h>
#include <ESP8266HTTPClient.h>
#include <FirebaseArduino.h>
#include <WifiLocation.h>

#include "Display.h"
#include "Door.h"
#include "FirebaseDatabase.h"
#include "ModeSwitch.h"
#include "RelayBulbs.h"

// Set these to run.
#define FIREBASE_HOST "happy-lock-1527825258557.firebaseio.com"
#define FIREBASE_AUTH "b9RWtSlDVHm5RLIL2USM4pP7FjqkinCP9g8jlLPK"
#define GOOGLE_API_KEY "AIzaSyBc36qu2_1pIQRjmCHvj6vDLnsCV_1uS5g"
#define WIFI_SSID "Dialog 4G"
#define WIFI_PASSWORD "Q6Q8JBA2A19"
//#define WIFI_SSID "Dialog 4G 1A9"
//#define WIFI_PASSWORD "Jd59HE6q"

float doorLocationLat;
float doorLocationLng;
String doorAccuracy = "0";

int doorStaus = 0;
int bulb1Status = 0;
int bulb2Status = 0;
int systemMode = 0;
int onlineCheck = 0;

WifiLocation location(GOOGLE_API_KEY);

FirebaseDatabase::FirebaseDatabase() {}

void FirebaseDatabase::SETUP() {

  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  display.print(0, 0, "CONNECTING.....");
  display.print(0, 1, "WIFI NETWORK.");
  delay(2000);
  // Set a static IP (optional)
  IPAddress ip(192, 168, 8, 55);
  IPAddress gateway(192, 168, 8, 1);
  IPAddress subnet(255, 255, 255, 0);
  WiFi.config(ip, gateway, subnet);

  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  display.clear();
  // Serial.print("Search GEO location.");

  if (Firebase.getInt("SAVE_LOCATION") == 1) {
    display.print(4, 0, "GET SAVE ");
    display.print(2, 1, "GEO LOCATION");
    display.print(-1, 2, "FROM CLOUD");
    doorAccuracy = Firebase.getString("DOOR_LOCATION_ACCURACY");
    doorLocationLat = Firebase.getFloat("DOOR_LOCATION_LATITUDE");
    doorLocationLng = Firebase.getFloat("DOOR_LOCATION_LONGITUDE");
    delay(2000);
  } else {
    display.print(0, 0, "SEARCH");
    display.print(0, 1, "GEO LOCATION...");
    location_t loc = location.getGeoFromWiFi();
    int trySearch = 1;
    while (loc.accuracy > 100) {
      loc = location.getGeoFromWiFi();
      display.print(2, 2, "TRY: " + String(trySearch));
      display.print(-3, 3, "Accuracy: " + String(loc.accuracy) + "      ");
      delay(300);
      trySearch++;
      //  Serial.print(".");
    }
    doorAccuracy = String(loc.accuracy);
    doorLocationLat = loc.lat;
    doorLocationLng = loc.lon;

    Firebase.setFloat("DOOR_LOCATION_LATITUDE", doorLocationLat);
    Firebase.setFloat("DOOR_LOCATION_LONGITUDE", doorLocationLng);
    Firebase.setString("DOOR_LOCATION_ACCURACY", doorAccuracy);
  }

  display.clear();
  display.print(0, 0, "   LOCATION INFO ");
  display.print(1, 1, "Accuracy: " + doorAccuracy);
  display.print(-3, 2, "Lat: " + String(doorLocationLat, 7));
  display.print(-3, 3, "Log:" + String(doorLocationLng, 7));

  delay(3000);
  // display.printHomeFirstTime();
  //
  systemMode = Firebase.getInt("MODE");
  modeSwitch.setMode(systemMode);
  display.clear();
  //  Firebase.set("LED_STATUS", 0);
}

void FirebaseDatabase::doorLockUnlock(int status) {
  if (status == 1)
    Firebase.set("DOOR_STATUS", 1);
  else if (status == 0)
    Firebase.set("DOOR_STATUS", 0);
}

void FirebaseDatabase::bulbOnOff(int bulbNumber, int status) {
  if (bulbNumber == 1) {
    if (status == 1)
      Firebase.set("BULB_1_STATUS", 1);
    else if (status == 0)
      Firebase.set("BULB_1_STATUS", 0);
  } else if (bulbNumber == 2) {
    if (status == 1)
      Firebase.set("BULB_2_STATUS", 1);
    else if (status == 0)
      Firebase.set("BULB_2_STATUS", 0);
  }
}

void FirebaseDatabase::changeMode(int mode) { Firebase.set("MODE", mode); }

void FirebaseDatabase::doorLogWrite(int status) {
  DynamicJsonBuffer jsonBuffer;
  // Push to Firebase
  JsonObject& logObject = jsonBuffer.createObject();
  JsonObject& tempTime = logObject.createNestedObject("timestamp");
  if (status == 1)
    logObject["status"] = "LOCKED";
  else
    logObject["status"] = "UNLOCKED";
  tempTime[".sv"] = "timestamp";
  Firebase.push("/DOOR_ACTION_HISTORY", logObject);
}

void FirebaseDatabase::run() {
  // set value
  doorStaus = Firebase.getInt("DOOR_STATUS");
  bulb1Status = Firebase.getInt("BULB_1_STATUS");
  bulb2Status = Firebase.getInt("BULB_2_STATUS");
  systemMode = Firebase.getInt("MODE");
  onlineCheck = Firebase.getInt("ONLINE");

  // handle error
  if (Firebase.failed()) {
    // Serial.print("setting /number failed:");
    // Serial.println(Firebase.error());
    return;
  }
  delay(10);
  // Door
  if (doorStaus == 1) {
    if (!door.isDoorLock()) {
      door.lock();
      doorLogWrite(1);
      delay(500);
    }
  } else {
    if (door.isDoorLock()) {
      door.unlock();
      doorLogWrite(0);
      delay(500);
    }
  }
  // pass auto mode.
  if (modeSwitch.getCurrentMode() != 2 && modeSwitch.getCurrentMode() != 4) {
    // Bulb 1
    if (bulb1Status == 0) {
      if (!relayBulbs.isBulbOn(1)) {
        relayBulbs.onOff(1, 1);
      }
    } else {
      if (relayBulbs.isBulbOn(1)) {
        relayBulbs.onOff(1, 0);
      }
    }
    // Bulb 2
    if (bulb2Status == 0) {
      if (!relayBulbs.isBulbOn(2)) {
        relayBulbs.onOff(2, 1);
      }
    } else {
      if (relayBulbs.isBulbOn(2)) {
        relayBulbs.onOff(2, 0);
      }
    }
  }
  // Mode
  if (systemMode != modeSwitch.getCurrentMode()) {
    modeSwitch.changeMode(systemMode);
  }
  // Online
  if (onlineCheck == 0) {
    display.printOnline();
    Firebase.set("ONLINE", 1);
  }
}

FirebaseDatabase firebaseDatabase = FirebaseDatabase();
