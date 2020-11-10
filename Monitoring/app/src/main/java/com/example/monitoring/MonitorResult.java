package com.example.monitoring;

public class MonitorResult {
    public String Temperature;
    public String PH;
    public String Salinitas;
    public long Update;

    public MonitorResult(String ph, String salinitas, String temperature, long update){
        ph = PH;
        salinitas = Salinitas;
        temperature = Temperature;
        update = Update;
    }

    public MonitorResult(){

    }

}
