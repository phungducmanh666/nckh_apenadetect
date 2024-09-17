#ifndef UTILS_H
#define UTILS_H

float arrMin(float arr[], int size){
  float min = arr[0];
  for(size_t i = 0; i < size; i++){
    if(arr[i] < min){
      min = arr[i];
    }
  }
  return min;
}

float arrMax(float arr[], int size){
  float max = arr[0];
  for(size_t i = 0; i < size; i++){
    if(arr[i] > max){
      max = arr[i];
    }
  }
  return max;
}

float arrMean(float arr[], int size){
    float sum = 0;
    for (size_t i = 0; i < size; i++) {
        sum += arr[i];
    }
    return (sum / size);
}

float arrStd(float arr[], int size){
  float _arrMean = arrMean(arr, size);
  float sum = 0;
  for (size_t i = 0; i < size; i++) {
      float _val = arr[i] - _arrMean;
        sum += (_val * _val);
    }
  return sqrt(sum / size);
}

#endif