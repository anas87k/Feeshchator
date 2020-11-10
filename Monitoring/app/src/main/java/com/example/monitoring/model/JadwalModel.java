package com.example.monitoring.model;

public class JadwalModel {
    private String nama;
    private String datetime;
    private String key;
    private String durasi;
    private String status;

    public JadwalModel(){

    }

    public JadwalModel(String nama, String datetime, String durasi, String status) {
        this.nama = nama;
        this.datetime = datetime;
        this.durasi = durasi;
        this.status = status;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDurasi() {
        return durasi;
    }

    public void setDurasi(String durasi) {
        this.durasi = durasi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
