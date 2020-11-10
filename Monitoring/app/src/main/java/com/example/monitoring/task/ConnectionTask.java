package com.example.monitoring.task;

import android.os.AsyncTask;

import com.example.monitoring.interfacee.ConnectionInterface;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ConnectionTask extends AsyncTask<String, String, String> {
    private ConnectionInterface connectionInterface;
    boolean status;
    URI uri;

    public ConnectionTask(ConnectionInterface connectionInterface){
        this.connectionInterface = connectionInterface;
    }
    @Override
    protected String doInBackground(String... strings) {
        String value;
        if (httpskoneksi("https://www.google.com")){
            value = "https";
            return value;
        } else if (httpkoneksi("http://172.16.0.2/")){
            value = "pakan";
            return value;
        } else if (httpkoneksi("http://172.16.1.2/")){
            value = "air";
            return value;
        } else return "o";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        connectionInterface.CekKoneksi(s);

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
