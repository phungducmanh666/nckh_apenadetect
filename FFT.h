#ifndef FFT_H
#define FFT_H

/*
* @float[] timeSignalArr ::: mảng tín hiệu đầu vào trên miền thời gian
* @int timeSignalArrSize ::: độ dài mảng đầu vào
* @float[] frequencySignalArr ::: mảng tần số đầu ra sau khi biến đổi đầu vào
*/
void dft(float[] timeSignalArr, int timeSignalArrSize, float[] frequencySignalArr){

  float realPart[timeSignalArrSize];
  float imagPart[timeSignalArrSize];

  for (int k = 0; k < timeSignalArrSize/2; k++) {
    realPart[k] = 0;
    imagPart[k] = 0;

    for (int n = 0; n < timeSignalArrSize; n++) {
      realPart[k] += timeSignalArr[n] * cos(2 * PI * n * k / timeSignalArrSize);
      imagPart[k] -= timeSignalArr[n] * sin(2 * PI * n * k / timeSignalArrSize);
    }
  }
  for (int k = 0; k < timeSignalArrSize/2; k++) {
    frequencySignalArr[k] = sqrt(realPart[k] * realPart[k] + imagPart[k] * imagPart[k]);
  }
}

/*
* @float arr[] mảng tín hiệu được out ra từ hàm ở trên
* @int arrSize độ dài mảng 
*/
float getMaxFrequency(float frequencySignalArr[], int arrSize){
  int frequencyMax = 0;
  float valueOfFrequency = frequencySignalArr[0];
  for (int k = 0; k <= arrSize; k++) {
     if(valueOfFrequency < frequencySignalArr[k]){
        frequencyMax = k;
        valueOfFrequency = frequencySignalArr[k];
     }
  }
  return frequencyMax * 0.2;
}



#endif