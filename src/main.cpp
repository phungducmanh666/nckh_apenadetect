#include <Arduino.h>
// #include <Wire.h>

#include "firebase_config.h"
#include "wifi_config.h"
#include "model_config.h"
#include "mpu_config.h"
#include "app_config.h"
#include "utils.h"
#include "tasks_config.h"
#include "time_config.h"

void setup() {
  Serial.begin(9600);
  my_wifi_config::Setup();
  my_firebase_config::Setup();
  my_mpu_config::Setup();
  my_model_config::Setup();
  my_time::Setup();
  my_rtos::Setup();
}
void loop(){
  // delay 1s
  if (millis() - NextMillis < Cycle) {
    return;
  }

  NextMillis += Cycle;

  // update mpu
  my_mpu_config::Update();

  // get mpu data
  AyRawArr[RawIndex++] = my_mpu_config::a.acceleration.y;

  if(RawIndex == RAW_SIZE){
    // data đầy
    // tính độ lệch chuẩn
    float ayStd = ArrStd(AyRawArr, RAW_SIZE);
    DataFeatures[0] = ayStd;
    // input model
    for(size_t i = 0; i < FEATURE_SIZE; i++){
      my_model_config::model_input->data.f[i] = DataFeatures[i];
    }
    // detect
    TfLiteStatus invoke_status = my_model_config::interpreter->Invoke();
    if (invoke_status != kTfLiteOk) {
      my_model_config::error_reporter->Report("Invoke failed on input: %f\n");
    }
    double predictValue = my_model_config::model_output->data.f[0];

    Serial.println(predictValue);

    // // Message message;
    // // tính nhịp thở
    // // float maxFrequency = my_utils::GetMaxFrequency(AyRawArr, RAW_SIZE);
    // // message.predict = predictValue;
    // // std::copy(AyRawArr, AyRawArr + RAW_SIZE, message.arr);

    std::copy(AyRawArr, AyRawArr + RAW_SIZE, TimeSignalArr);

    // // Serial.println("[MAIN]::: " + String(ArrMax(AyRawArr, RAW_SIZE)));

    if(xQueueSend(my_rtos::MessagesQueue, &predictValue, portMAX_DELAY) == pdPASS){
      // send success
    }
    else{
      Serial.println("[MAIN]::: failed to send predict value to queue!");
    }

    // update raw index
    size_t num = RAW_SIZE - JUMP_SIZE;
    for(size_t i = 0; i < num; i++){
      AyRawArr[i] = AyRawArr[i + JUMP_SIZE];
    }
    RawIndex -= JUMP_SIZE;

  }

  // my_firebase_config::SendContent(1.76f, "1234567890");
  //  // Tổng dung lượng RAM có thể sử dụng
  //   size_t total_heap = heap_caps_get_total_size(MALLOC_CAP_8BIT);
    
  //   // RAM còn trống hiện tại
  //   size_t free_heap = esp_get_free_heap_size();
    
  //   // Mức RAM trống thấp nhất từng có (cao nhất mức sử dụng RAM)
  //   size_t min_free_heap = esp_get_minimum_free_heap_size();
    
  //   printf("Tổng RAM khả dụng: %d bytes\n", total_heap);
  //   printf("RAM còn trống hiện tại: %d bytes\n", free_heap);
  //   printf("Mức RAM trống thấp nhất từng có: %d bytes\n", min_free_heap);
}
