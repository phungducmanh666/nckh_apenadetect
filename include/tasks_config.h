#ifndef RTOS_H
#define RTOS_H

#include <Arduino.h>
#include "app_config.h"
#include "utils.h"
#include "firebase_config.h"
#include "time_config.h"

namespace my_rtos {
  QueueHandle_t MessagesQueue;
  const int QueueElementSize = 1;

  // SemaphoreHandle_t xSemaphore = NULL;

  void BreathRateTask(void *pvParameters) {
    // Message message;
    double predictValue;
    while (true) {
      if (xQueueReceive(my_rtos::MessagesQueue, &predictValue, portMAX_DELAY) == pdPASS) {
        float breathRate = 0.0f;
        if(predictValue < 0.5){
          float maxFrequency = my_utils::GetMaxFrequency(TimeSignalArr, RAW_SIZE);
          // copy
          breathRate = maxFrequency * 60;
        }
        // Serial.println("[RTOS]::: "  +String(ArrMax(TimeSignalArr, RAW_SIZE)));
        // Serial.println("_____________________________________");

        if(getLocalTime(&my_time::timeinfo)){
          long newTime = (long)(mktime(&my_time::timeinfo));
          Serial.println(newTime);
          String CurrentMillis = String(newTime) + "000";
          Serial.println("[RTOS]::: ");
          Serial.print("\tpredict value: ");
          Serial.println(predictValue);
          Serial.print("\tbreath rate: ");
          Serial.println(breathRate);
          Serial.print("\tmillis: ");
          Serial.println(CurrentMillis);

          my_firebase_config::SendContent(breathRate, CurrentMillis);

        }
      }
      vTaskDelay(1 / portTICK_PERIOD_MS);
    }
  }

  void Setup() {
    Serial.println("[RTOS]::: setup rtos...");

    Serial.println("\tsetup queue...");
    MessagesQueue = xQueueCreate(QueueElementSize, sizeof(double));

    Serial.println("\tsetup task...");
    xTaskCreatePinnedToCore(BreathRateTask, "Breath Rate Task", 10240, NULL, 1, NULL, 0);  // Ưu tiên 1

    Serial.println("[RTOS]::: finish setup rtos...");
  }
}

#endif
