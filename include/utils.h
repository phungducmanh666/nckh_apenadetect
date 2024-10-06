#ifndef UTILS_H
#define UTILS_H

#include <Arduino.h>
#include <app_config.h>

void printDeviceRam()
{
  Serial.print("FREE RAM: ");
  Serial.print(ESP.getFreeHeap() / 1024);
  Serial.print("/");
  Serial.print(ESP.getHeapSize() / 1024);
  Serial.println("_KB");
}

/* get arr min */
float ArrMin(float arr[], int size)
{
  float min = arr[0];
  for (size_t i = 0; i < size; i++)
  {
    if (arr[i] < min)
    {
      min = arr[i];
    }
  }
  return min;
}

/* get arr max */
float ArrMax(float arr[], int size)
{
  float max = arr[0];
  for (size_t i = 0; i < size; i++)
  {
    if (arr[i] > max)
    {
      max = arr[i];
    }
  }
  return max;
}

/* get arr mean */
float ArrMean(float arr[], int size)
{
  float sum = 0;
  for (size_t i = 0; i < size; i++)
  {
    sum += arr[i];
  }
  return (sum / size);
}

/* get arr std */
float ArrStd(float arr[], int size)
{
  float _arrMean = ArrMean(arr, size);
  float sum = 0;
  for (size_t i = 0; i < size; i++)
  {
    float _val = arr[i] - _arrMean;
    sum += (_val * _val);
  }
  return sqrt(sum / size);
}

namespace my_utils
{
  /* fourier dft
   * float timeSignalArr[] : tín hiệu trong miền thời gian
   * int timeSignalArrSize: kích thước mảng tín hiệu trong miền thời gian
   * float frequencySignalArr[]: tín hiệu trong miền tần số (sau khi biến đổi)
   */
  void DFT(float arr[], int size)
  {

    for (int k = 0; k < size / 2; k++)
    {
      RealPart[k] = 0;
      ImagPart[k] = 0;

      for (int n = 0; n < size; n++)
      {
        RealPart[k] += arr[n] * cos(2 * PI * n * k / size);
        ImagPart[k] -= arr[n] * sin(2 * PI * n * k / size);
      }
    }
    for (int k = 0; k < size / 2; k++)
    {
      FrequencySignalArr[k] = sqrt(RealPart[k] * RealPart[k] + ImagPart[k] * ImagPart[k]);
    }
  }

  /*
   * get max frequency
   */
  float GetMaxFrequency(float arr[], int size)
  {
    // GetFrequencies();
    DFT(arr, size);
    int maxFPos = 1;
    float maxF = FrequencySignalArr[maxFPos];
    // Serial.println("CCC:::" + String(FrequencySignalArr[0]));
    // Serial.println("CCC:::" + String(FrequencySignalArr[1]));
    // Serial.println("CCC:::" + String(FrequencySignalArr[2]));
    for (size_t k = 1; k <= (int)MAX_FREQUENCY_POS; k++)
    {
      if (maxF < FrequencySignalArr[k])
      {
        maxFPos = k;
        maxF = FrequencySignalArr[k];
      }
    }
    float result = (float)(maxFPos * FREQUENCY_STEP);
    // Serial.println(result);
    return result;
  }
}

#endif