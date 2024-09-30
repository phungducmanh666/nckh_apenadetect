#ifndef FIREBASE_CONFIG_H
#define FIREBASE_CONFIG_H

#include <Firebase_ESP_Client.h>

namespace my_firebase_config{
    const char* API_KEY = "AIzaSyBhT35_enfb1YlkFsWlozdkmu5yoG8AclI";
    const char* FIREBASE_PROJECT_ID = "esp32-fib-a264a";
    const char* USER_EMAIL = "phungducmanh666@gmail.com";
    const char* USER_PASSWORD = "phungducmanh"; 

    FirebaseData fbdo;
    FirebaseAuth auth;
    FirebaseConfig config;

    String DocumentPath = "breath_rates";

    void Setup(){
    Serial.println("[FIRE_BASE]::: setup firebase");

    config.api_key = API_KEY;
    auth.user.email = USER_EMAIL;
    auth.user.password = USER_PASSWORD;

    Firebase.reconnectNetwork(true);
    fbdo.setBSSLBufferSize(2048 /* Rx buffer size in bytes from 512 - 16384 */, 1024 /* Tx buffer size in bytes from 512 - 16384 */);
    fbdo.setResponseSize(2048);
    Firebase.begin(&config, &auth);

    Serial.println("[FIRE_BASE]::: finish setup firebase");
  }

  void SendContent(float breathRate, String currentMillis){
    String rawData = "{\"fields\":{\"value\":{\"doubleValue\":" + String(breathRate) + "},\"millis\":{\"integerValue\":\"" + currentMillis + "\"}}}";
    // Serial.println(rawData);
    if (Firebase.Firestore.createDocument(&fbdo, FIREBASE_PROJECT_ID, "" /* databaseId can be (default) or empty */, DocumentPath, rawData)){
      Serial.println("[FIRE_BASE] ::: send success");
    }
    else{
      Serial.println("[FIRE_BASE] ::: send failed");
    }
  }

}

#endif