package com.example.monitoring.task;

import android.os.AsyncTask;

import com.example.monitoring.interfacee.MonitoringInterface;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MonitoringTask extends AsyncTask<String, String, String > {
    private MonitoringInterface monitoringInterface;

    public MonitoringTask(MonitoringInterface monitoringInterface){
        this.monitoringInterface = monitoringInterface;
    }
    @Override
    protected String doInBackground(String... strings) {
        String value;
        if (httpskoneksi("https://www.google.com")){
            value = "https";
            return value;
        }
        else if (httpkoneksi("http://10.0.1.1/")){
            value = "http";
            return value;
        }
        else return "o";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        monitoringInterface.MonitorKoneksi(s);

    }

    public boolean httpskoneksi(String url) {
        try {
            URL myUrl = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) myUrl.openConnection();
            connection.connect();
            return true;
        } catch (Exception e) {
            // Handle your exceptions
            return false;
        }
    }
    public boolean httpkoneksi(String url) {
        try {
            URL myUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
            connection.setConnectTimeout(5000);
            connection.connect();
            return true;
        } catch (Exception e) {
            // Handle your exceptions
            return false;
        }
    }
}
