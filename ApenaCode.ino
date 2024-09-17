// #include "esp_private/periph_ctrl.h"
// #include "soc/periph_defs.h"
// hỗ trợ tính toán
#include <cmath>
#include "Utils.h"

// mpu 6050
#include <Adafruit_MPU6050.h>
#include <Adafruit_Sensor.h>
#include <Wire.h>

Adafruit_MPU6050 mpu;
unsigned long nextMillis = 0;
unsigned int delayMillis = 25;
sensors_event_t a, g, temp;


// model ai
#include <TensorFlowLite_ESP32.h>
#include "tensorflow/lite/micro/all_ops_resolver.h"
#include "tensorflow/lite/micro/micro_error_reporter.h"
#include "tensorflow/lite/micro/micro_interpreter.h"
#include "tensorflow/lite/micro/system_setup.h"
#include "tensorflow/lite/schema/schema_generated.h"
#include "ModelAI.h"

namespace {
tflite::ErrorReporter* error_reporter = nullptr;
const tflite::Model* model = nullptr;
tflite::MicroInterpreter* interpreter = nullptr;
TfLiteTensor* model_input = nullptr;
TfLiteTensor* model_output = nullptr;

  constexpr int kTensorArenaSize = 16 * 1024;
  uint8_t tensor_arena[kTensorArenaSize];
} 

// get mpu data to this variables
const int rawArrSize = 200;
int rawIndex = 0;
float axArrRaw[rawArrSize];
float ayArrRaw[rawArrSize];
float azArrRaw[rawArrSize];
float gxArrRaw[rawArrSize];
float gyArrRaw[rawArrSize];
float gzArrRaw[rawArrSize];
// 6 truc moi truc 4 feature: min, max, mean, std
const int featureArrSize = 24;
float features[featureArrSize];
// bước nhảy data sau mỗi lần predict
const int jumpArrSize = 40;

void setup() {
  Serial.begin(115200);
  // setup mpu 6050
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
  // setup ai model
  static tflite::MicroErrorReporter micro_error_reporter;
  error_reporter = &micro_error_reporter;
  // load model_apena arr từ file ModelAI.h
  model = tflite::GetModel(model_apena);
  if (model->version() != TFLITE_SCHEMA_VERSION) {
    TF_LITE_REPORT_ERROR(error_reporter,
                         "Model provided is schema version %d not equal "
                         "to supported version %d.",
                         model->version(), TFLITE_SCHEMA_VERSION);
    return;
  }
  static tflite::AllOpsResolver resolver;
  static tflite::MicroInterpreter static_interpreter(
      model, resolver, tensor_arena, kTensorArenaSize, error_reporter);
  interpreter = &static_interpreter;
  TfLiteStatus allocate_status = interpreter->AllocateTensors();
  if (allocate_status != kTfLiteOk) {
    TF_LITE_REPORT_ERROR(error_reporter, "AllocateTensors() failed");
    return;
  }
  model_input = interpreter->input(0);
  model_output = interpreter->output(0);
  Serial.println("Finish setup model AI!");
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

  // get mpu data
  mpu.getEvent(&a, &g, &temp);

  // append data vào các mảng tương ứng
  axArrRaw[rawIndex] = a.acceleration.x;
  ayArrRaw[rawIndex] = a.acceleration.y;
  azArrRaw[rawIndex] = a.acceleration.z;
  gxArrRaw[rawIndex] = g.gyro.x;
  gyArrRaw[rawIndex] = g.gyro.y;
  gzArrRaw[rawIndex] = g.gyro.z;
  rawIndex++;
  // nếu raw index đã đầy
  if(rawIndex == 200){
    // tính toán feature của các trục
    // ax features
    float axArrMin = arrMin(axArrRaw, rawArrSize);
    float axArrMax = arrMax(axArrRaw, rawArrSize);
    float axArrMean = arrMean(axArrRaw, rawArrSize);
    float axArrStd = arrStd(axArrRaw, rawArrSize);
    // ay features
    float ayArrMin = arrMin(ayArrRaw, rawArrSize);
    float ayArrMax = arrMax(ayArrRaw, rawArrSize);
    float ayArrMean = arrMean(ayArrRaw, rawArrSize);
    float ayArrStd = arrStd(ayArrRaw, rawArrSize);
    // az features
    float azArrMin = arrMin(azArrRaw, rawArrSize);
    float azArrMax = arrMax(azArrRaw, rawArrSize);
    float azArrMean = arrMean(azArrRaw, rawArrSize);
    float azArrStd = arrStd(azArrRaw, rawArrSize);
    // gx features
    float gxArrMin = arrMin(gxArrRaw, rawArrSize);
    float gxArrMax = arrMax(gxArrRaw, rawArrSize);
    float gxArrMean = arrMean(gxArrRaw, rawArrSize);
    float gxArrStd = arrStd(gxArrRaw, rawArrSize);
    // gy features
    float gyArrMin = arrMin(gyArrRaw, rawArrSize);
    float gyArrMax = arrMax(gyArrRaw, rawArrSize);
    float gyArrMean = arrMean(gyArrRaw, rawArrSize);
    float gyArrStd = arrStd(gyArrRaw, rawArrSize);
    // gz features
    float gzArrMin = arrMin(gzArrRaw, rawArrSize);
    float gzArrMax = arrMax(gzArrRaw, rawArrSize);
    float gzArrMean = arrMean(gzArrRaw, rawArrSize);
    float gzArrStd = arrStd(gzArrRaw, rawArrSize);
    features[0] = axArrMin;
    features[1] = axArrMax;
    features[2] = axArrMean;
    features[3] = axArrStd;
    features[4] = ayArrMin;
    features[5] = ayArrMax;
    features[6] = ayArrMean;
    features[7] = ayArrStd;
    features[8] = azArrMin;
    features[9] = azArrMax;
    features[10] = azArrMean;
    features[11] = azArrStd;
    features[12] = gxArrMin;
    features[13] = gxArrMax;
    features[14] = gxArrMean;
    features[15] = gxArrStd;
    features[16] = gyArrMin;
    features[17] = gyArrMax;
    features[18] = gyArrMean;
    features[19] = gyArrStd;
    features[20] = gzArrMin;
    features[21] = gzArrMax;
    features[22] = gzArrMean;
    features[23] = gzArrStd;
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
    double predict = model_output->data.f[0];
    Serial.print("\tPredict value: ");
    Serial.println(predict);

    // reset dịch 1s cũ nhất
    size_t num = rawArrSize - jumpArrSize;
    for(size_t i = 0; i < num; i++){
      axArrRaw[i] = axArrRaw[i + jumpArrSize];
      ayArrRaw[i] = ayArrRaw[i + jumpArrSize];
      azArrRaw[i] = azArrRaw[i + jumpArrSize];
      gxArrRaw[i] = gxArrRaw[i + jumpArrSize];
      gyArrRaw[i] = gyArrRaw[i + jumpArrSize];
      gzArrRaw[i] = gzArrRaw[i + jumpArrSize];
    }
    // reset index
    rawIndex -= jumpArrSize;
  }

  // // print data
  // Serial.print(a.acceleration.x);
  // Serial.print(",");
  // Serial.print(a.acceleration.y);
  // Serial.print(",");
  // Serial.print(a.acceleration.z);
  // Serial.print(",");
  // Serial.print(g.gyro.x);
  // Serial.print(",");
  // Serial.print(g.gyro.y);
  // Serial.print(",");
  // Serial.print(g.gyro.z);
  // Serial.print("\n");
}
