package com.example.monitoring.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.monitoring.R;
import com.example.monitoring.adapter.PanduanAdapter;
import com.example.monitoring.interfacee.ConnectionInterface;
import com.example.monitoring.model.PanduanModel;
import com.example.monitoring.task.ConnectionTask;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.monitoring.DataInterface.myRefPanduan;

public class PanduanActivity extends AppCompatActivity implements ConnectionInterface {
    RecyclerView rv_panduan_monitor;
    PanduanAdapter panduanAdapter;
    ArrayList list_panduan;
    RelativeLayout errorlayout;
    BottomSheetDialog bstoast;
    View viewerrortoast;
    TextView txttoasterror, jdl;
    Button cobalagi;
    String extra, judul;
    RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panduan);
        viewerrortoast = LayoutInflater.from(PanduanActivity.this).inflate(R.layout.toast_error,(LinearLayout) findViewById(R.id.toast_error));

        txttoasterror = viewerrortoast.findViewById(R.id.texttoast);
        cobalagi = findViewById(R.id.cobafeeder);
        errorlayout = findViewById(R.id.errordata);
        rv_panduan_monitor = findViewById(R.id.rv_panduan);
        jdl = findViewById(R.id.jdlm);
        progressBar = findViewById(R.id.progressbar);
        Sprite chasingDots = new ChasingDots();
        progressBar.setIndeterminateDrawable(chasingDots);
        chasingDots.setColor(Color.rgb(3,169,244));
        bstoast = new BottomSheetDialog(PanduanActivity.this);

        Bundle bundle = getIntent().getExtras();
        extra = bundle.getString("nama");
        switch (extra){
            case "monitor" :
                judul = "Panduan Pemantauan";
                break;
            case "control" :
                judul = "Panduan Pengendalian";
                break;
            case "pasut" :
                judul = "Panduan Pasang Surut";
                break;
        }
        jdl.setText(judul);

        new ConnectionTask(PanduanActivity.this).execute();
        cobalagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConnectionTask(PanduanActivity.this).execute();
            }
        });
    }

    public void showLayout(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rv_panduan_monitor.setVisibility(View.VISIBLE);
                errorlayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        },2000);
    }
    public void errorLayout(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rv_panduan_monitor.setVisibility(View.INVISIBLE);
                errorlayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                txttoasterror.setText("Anda Sedang Offline, Hubungkan Ke Internet dan Silahkan Coba Lagi");
                errortoastShow();
            }
        },3000);
    }

    void errortoastShow(){
        bstoast.setContentView(viewerrortoast);
        bstoast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bstoast.dismiss();
            }
        },3500);
    }

    @Override
    public void CekKoneksi(String s) {
        if (s.equals("https")){
            showLayout();
            myRefPanduan.child(extra).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Inisialisasi ArrayList
                    list_panduan = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        //Mapping data pada DataSnapshot ke dalam objek jadwal
                        PanduanModel panduanModel = snapshot.getValue(PanduanModel.class);
                        list_panduan.add(panduanModel);
                    }

                    //Inisialisasi Adapter dan data Mahasiswa dalam bentuk Array
                    panduanAdapter = new PanduanAdapter(list_panduan, PanduanActivity.this);
                    //Memasang Adapter pada RecyclerView
                    rv_panduan_monitor.setAdapter(panduanAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            layoutManager = new LinearLayoutManager(this);
            rv_panduan_monitor.setLayoutManager(layoutManager);
            rv_panduan_monitor.setHasFixedSize(true);
        } else {
            errorLayout();
        }
    }
}