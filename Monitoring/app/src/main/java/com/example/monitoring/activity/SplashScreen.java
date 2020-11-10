package com.example.monitoring.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.monitoring.task.ConnectionTask;
import com.example.monitoring.interfacee.ConnectionInterface;
import com.example.monitoring.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.example.monitoring.DataInterface.myRefFeeder;

public class SplashScreen extends AppCompatActivity {

    Dialog errordialog;
    Button close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Log.e("TOKEN", "onCreate: "+ FirebaseInstanceId.getInstance().getToken());
        errordialog = new Dialog(this);
        final int WAKTU = 4000;
        FirebaseMessaging.getInstance().subscribeToTopic("feeshchator");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new ConnectionTask(new ConnectionInterface() {
                    @Override
                    public void CekKoneksi(String s) {
                        if (s.equals("https") || s.equals("pakan") || s.equals("air")) {
                            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else opendialog();
                    }
                }).execute();
            }
        }, WAKTU);
    }

    public void opendialog(){
        errordialog.setContentView(R.layout.dialog_koneksi);
        close = errordialog.findViewById(R.id.closedialog);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errordialog.dismiss();
                SplashScreen.this.finish();
                System.exit(0);
            }
        });
        errordialog.setCanceledOnTouchOutside(false);
        errordialog.setCancelable(false);
        errordialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        errordialog.show();
    }
}