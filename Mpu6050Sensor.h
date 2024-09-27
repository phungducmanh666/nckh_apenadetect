#ifndef MPU6050_SENSOR_H
#define MPU6050_SENSOR_H

// mpu 6050
#include <Adafruit_MPU6050.h>
#include <Adafruit_Sensor.h>
#include <Wire.h>

// khai báo các biến trong sử dụng mpu 6050
Adafruit_MPU6050 mpu;
sensors_event_t a, g, temp;

void setupMpu6050Sensor(){
  Serial.println("Setup mpu 6050...");
  Wire.begin(0,16);
  if(!mpu.begin()){
    Serial.println("Failed to find mpu6050 chip!");
    while(1){
      delay(10);
    }
  }
  mpu.setAccelerometerRange(MPU6050_RANGE_16_G);
  mpu.setGyroRange(MPU6050_RANGE_250_DEG);
  mpu.setFilterBandwidth(MPU6050_BAND_21_HZ);
  Serial.println("Finish setup mpu6050.");
}

void updateMpu6050Sensor(){
    // get mpu data
  mpu.getEvent(&a, &g, &temp);
}

#endif