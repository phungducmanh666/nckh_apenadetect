// #include "esp_private/periph_ctrl.h"
// #include "soc/periph_defs.h"
// hỗ trợ tính toán
#include <cmath>
#include "Utils.h"
// mpu6050 and model ai detect
#include "Mpu6050Sensor.h"
#include "Detector.h"
// dft
#include "FFT.h"
// firebase
#include "firebase.h"
#include <time.h>

// khai báo biến để lưu trữ dữ liệu để model detect
// get mpu data to this variables
const int rawArrSize = 200;
int rawIndex = 0;
float ayArrRaw[rawArrSize];
// 6 truc moi truc 4 feature: min, max, mean, std
const int featureArrSize = 4;
float features[featureArrSize];
// bước nhảy data sau mỗi lần predict
const int jumpArrSize = 40;

// define time get data
unsigned long nextMillis = 0;
unsigned int delayMillis = 25;

// multiple task
void TaskFirebase(void *pvParameters);
// queue
QueueHandle_t MessagesQueue;
const int QueueElementSize = 10;

// time for send to firebase
String currentMillis;

// struct to send predict value and raw arr
struct MyMessage{
  double predictValue;
  float* rawData;

  int dataSize;

  MyMessage(int size) : dataSize(size) {
    rawData = new float[dataSize];
  }

  ~MyMessage() {
    delete[] rawData;
  }
};

void setup() {
  Serial.begin(115200);
  //---- setup mpu 6050
  setupMpu6050Sensor();

  //---- setup detector
  setupDetector();
  //---- setup queue handle
  MessagesQueue = xQueueCreate(QueueElementSize, sizeof(MyMessage));
  //---- setup RTOS
  xTaskCreatePinnedToCore(TaskFirebase, "Task firebase", 10000, NULL, 0, NULL, 0);

  //---- setup firebase
  setupFirebaseClient();
  //---- time config
  Serial.println("Get current time....");
    configTime(7 * 3600, 0, "pool.ntp.org", "time.nist.gov");
    struct tm timeinfo;
    while(!getLocalTime(&timeinfo)){
        Serial.println("Failed to get current time, trying...");
    }
    currentMillis = String((long)(mktime(&timeinfo))) + "000";
    Serial.println("Current time: " + currentMillis);
}

void loop() {
  // delay 25ms
  if (millis() < nextMillis) {
    return;
  }
  if (nextMillis == 0) {
    nextMillis = millis();
  }
  nextMillis += delayMillis;

  // update mpu6050
  updateMpu6050Sensor();

  // append data vào các mảng tương ứng
  ayArrRaw[rawIndex] = a.acceleration.y;
  rawIndex++;
  // nếu raw index đã đầy
  if(rawIndex == rawArrSize){
    // tính toán feature của trục ay
    // ay features
    float ayArrMin = arrMin(ayArrRaw, rawArrSize);
    float ayArrMax = arrMax(ayArrRaw, rawArrSize);
    float ayArrMean = arrMean(ayArrRaw, rawArrSize);
    float ayArrStd = arrStd(ayArrRaw, rawArrSize);
    
    features[0] = ayArrMin;
    features[1] = ayArrMax;
    features[2] = ayArrMean;
    features[3] = ayArrStd;
  
    // nạp features vào model
    for(size_t i = 0; i < featureArrSize; i++){
      model_input->data.f[i] = features[i];
    }

    // nhận dạng
    TfLiteStatus invoke_status = interpreter->Invoke();
    if (invoke_status != kTfLiteOk) {
      error_reporter->Report("Invoke failed on input: %f\n");
    }
    // lấy ra giá trị output: 1: ngưng thở, 0: bình thường
    double predictValue = model_output->data.f[0];
    Serial.print("\tPredict value: ");
    Serial.println(predictValue);

    /* 
    * send predict value to firebase task
    * pdPass if success
    */
    // --- create message
    MyMessage message(rawArrSize);

    for (int i = 0; i < message.dataSize; i++) {
        message.rawData[i] = ayArrRaw[i]; // Ví dụ gán giá trị
    }
    // --- send message
    if(xQueueSend(MessagesQueue, &message, portMAX_DELAY) == pdPass){
      Serial.println("Send predict value to queue success.");
    }
    else{
      Serial.println("Failed to send predict value to queue!");
    }

    // reset dịch 1s cũ nhất
    size_t num = rawArrSize - jumpArrSize;
    for(size_t i = 0; i < num; i++){
      ayArrRaw[i] = ayArrRaw[i + jumpArrSize];
    }
    // reset index
    rawIndex -= jumpArrSize;
  }
}

void TaskFirebase(void *pvParameters){
  for(;;){
    MyMessage message(rawArrSize);
    // receive success
    if(xQueueReceive(MessagesQueue, &message, portMAX_DELAY) == pdPASS){
        // caculator breath rate
        // send breath rate to firebase
        FirebaseJson content;
        String documentPath = "breath_rates";

        double predictValue = message.predictValue;
        float[] rawValues = message.rawData;
        float breathRate = 0.0;

        if(predictValue != 0){
          // transform time -> requency
            float[] frequencyArr = new float[rawArrSize / 2];
            dft(rawValues, rawArrSize, frequencyArr);
            // get max frequency
            float maxFrequency = getMaxFrequency(frequencyArr, rawArrSize/2);
            breathRate = maxFrequency * 60;
        }
        // print breath rate
        Serial.println("Breath rate: " + String(breathRate));
        // set firebase content
        content.set("fields/value/doubleValue", breathRate);
        content.set("fields/millis/integerValue", currentMillis);
        if (Firebase.Firestore.createDocument(&fbdo, FIREBASE_PROJECT_ID, "" /* databaseId can be (default) or empty */, documentPath.c_str(), content.raw())){
          Serial.println("Send breath rate to firebase success.");
        }
        else{
          Serial.println("Failed to send breath rete to firebase!");
        }
    }
  }
}
