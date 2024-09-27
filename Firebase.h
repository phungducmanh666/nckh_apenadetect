#ifndef FIREBASE_H
#define FIREBASE_H

#include <Firebase_ESP_Client.h>
#include <addons/TokenHelper.h>

#define API_KEY "AIzaSyBhT35_enfb1YlkFsWlozdkmu5yoG8AclI"
#define FIREBASE_PROJECT_ID "esp32-fib-a264a"

#define USER_EMAIL "phungducmanh666@gmail.com"
#define USER_PASSWORD "phungducmanh"

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

void setupFirebaseClient(){

  Serial.println("Setup firebase client...");

  config.api_key = API_KEY;
  config.token_status_callback = tokenStatusCallback;

  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;

  Firebase.reconnectNetwork(true);
  fbdo.setBSSLBufferSize(4096 /* Rx buffer size in bytes from 512 - 16384 */, 1024 /* Tx buffer size in bytes from 512 - 16384 */);
  fbdo.setResponseSize(2048);
  Firebase.begin(&config, &auth);

  Serial.println("Finish firebase client...");
}


#endif