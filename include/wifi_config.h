#ifndef WIFI_CONFIG_H
#define WIFI_CONFIG_H

#include <Arduino.h>
#include <WiFi.h>

namespace my_wifi_config{
    const char* WIFI_SSID = "HOA SEN";
    const char* WIFI_PASSWORD = "55599888";

    void Setup(){
        Serial.println("[WIFI]::: setup wifi...");
        WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
        Serial.print("\tconnecting to wifi...");
        while (WiFi.status() != WL_CONNECTED)
        {
            Serial.print(".");
            delay(300);
        }
        Serial.println();
        Serial.print("\tconnected wifi with ip: ");
        Serial.println(WiFi.localIP());
        Serial.print("[WIFI]::: finish setup wifi...");
    }
}

#endif