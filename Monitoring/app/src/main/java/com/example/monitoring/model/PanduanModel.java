package com.example.monitoring.model;

public class PanduanModel {
    private int no_panduan;
    private String text_panduan, image_url;

    //Konstruktor kosong, untuk data snapshot pada Firebase Realtime Database
    public PanduanModel() {}

    public PanduanModel(int no_panduan, String text_panduan, String image_url) {
        this.no_panduan = no_panduan;
        this.text_panduan = text_panduan;
        this.image_url = image_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getText_panduan() {
        return text_panduan;
    }

    public int getNo_panduan() {
        return no_panduan;
    }
}
