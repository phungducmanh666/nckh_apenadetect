#ifndef MY_TIME_H
#define MY_TIME_H

#include "app_config.h"


namespace my_time{

  struct tm timeinfo;


  void Setup(){
    Serial.println("[TIME]::: setup current time....");

    configTime(7 * 3600, 0, "pool.ntp.org", "time.nist.gov");
    while(!getLocalTime(&timeinfo)){
      Serial.println("\tfailed to get current time, trying...");
    }
    Serial.println("[TIME]::: finish setup current time....");
  }
}


#endif