package com.example.monitoring.model;

public class TipsModel {
    private int no_tips;
    private String text_tips;

    //Konstruktor kosong, untuk data snapshot pada Firebase Realtime Database
    public TipsModel() {}

    public TipsModel(int no_tips, String text_tips) {
        this.no_tips = no_tips;
        this.text_tips = text_tips;
    }

    public int getNo_tips() {
        return no_tips;
    }

    public String getText_tips() {
        return text_tips;
    }
}
