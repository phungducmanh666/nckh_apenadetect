#ifndef VARIABLE_H
#define VARIABLE_H

#include <Arduino.h>

/*
* raw data
*/
/*========================================================================================================*/
const int RAW_SIZE = 200;
// const int RAW_SIZE = 256;
const int FEATURE_SIZE = 1;
int RawIndex = 0;
// float AxRawArr[RAW_SIZE];
float AyRawArr[RAW_SIZE];
// float AzRawArr[RAW_SIZE];
float DataFeatures[FEATURE_SIZE];

/* time */
const int DELAY_SECOND = 1;
// tần số lấy mẫu (số mẫu lấy được trong 1s)
const int SAMPLE_FREQUENCY = 40;
const int JUMP_SIZE = DELAY_SECOND * SAMPLE_FREQUENCY;

unsigned long NextMillis = 0;
// chu kì lấy mẫu (thời gian lấy 1 mẫu)
unsigned int Cycle = (int)(1000 / SAMPLE_FREQUENCY);

/*
* dft
*/
/*========================================================================================================*/
float TimeSignalArr[RAW_SIZE];
float FrequencySignalArr[RAW_SIZE];
float RealPart[RAW_SIZE / 2];
float ImagPart[RAW_SIZE / 2];
// tần số tối đa sẽ lấy (trong một giây thời 0.6 lần thì trong 1 phút thở 0.6 * 60 = 36 lần)
const float MAX_FREQUENCY = 0.6;
// bước tần số trong biến đổi dft
const float FREQUENCY_STEP = (float)SAMPLE_FREQUENCY / (float)RAW_SIZE;
// vị trí của tần số max trong dft
const float MAX_FREQUENCY_POS = MAX_FREQUENCY / FREQUENCY_STEP;


#endif