package com.example.apenadetect.models;

public class BreathRate {
    float value;
    long millis;

    public BreathRate(){}
    public BreathRate(float value, long millis){
        this.value = value;
        this.millis = millis;
    }

    public  float getValue(){
        return  this.value;
    }
}
