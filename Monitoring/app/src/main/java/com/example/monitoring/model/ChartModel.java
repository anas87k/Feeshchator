package com.example.monitoring.model;

public class ChartModel {
    private float ph;
    private float salinitas;
    private float temperature;
    private long waktu;

    public ChartModel(){

    }

    public ChartModel(float ph, float salinitas, float temperature, long waktu){
        this.ph = ph;
        this.salinitas = salinitas;
        this.temperature = temperature;
        this.waktu = waktu;
    }

    public float getPh() {
        return ph;
    }

    public float getSalinitas() {
        return salinitas;
    }

    public float getTemperature() {
        return temperature;
    }

    public long getWaktu() {
        return waktu;
    }
}
