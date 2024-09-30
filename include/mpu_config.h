#ifndef MPU_CONFIG_H
#define MPU_CONFIG_H

#include <Adafruit_MPU6050.h>
#include <Adafruit_Sensor.h>

namespace my_mpu_config{

  Adafruit_MPU6050 mpu;
  sensors_event_t a, g, temp;

  void Setup(){
    Serial.println("[MPU6050]::: setup mpu 6050...");

    Wire.begin(0,16);
    if(!mpu.begin()){
      Serial.println("\tfailed to find mpu6050 chip, restarting...");
      delay(1000);
      // restart esp32
      esp_restart();
    }
    mpu.setAccelerometerRange(MPU6050_RANGE_16_G);
    mpu.setGyroRange(MPU6050_RANGE_250_DEG);
    mpu.setFilterBandwidth(MPU6050_BAND_21_HZ);

    Serial.println("[MPU6050]::: finish setup mpu 6050.");
  }

  void Update(){
    mpu.getEvent(&a, &g, &temp);
  }
}

#endif