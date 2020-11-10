package com.example.monitoring.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.monitoring.R;
import com.example.monitoring.adapter.PanduanAdapter;
import com.example.monitoring.adapter.TipsAdapter;
import com.example.monitoring.interfacee.ConnectionInterface;
import com.example.monitoring.model.PanduanModel;
import com.example.monitoring.model.TipsModel;
import com.example.monitoring.task.ConnectionTask;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.monitoring.DataInterface.myRefPanduan;

public class TipsActivity extends AppCompatActivity implements ConnectionInterface {
    RecyclerView rv_naik, rv_turun;
    TipsAdapter tipsAdapternaik, tipsAdapterturun;
    ScrollView layouttips;
    ArrayList list_naik, list_turun;
    RelativeLayout errorlayout;
    BottomSheetDialog bstoast;
    View viewerrortoast;
    TextView txttoasterror, jdl, txt_desc, txt_naik, txt_turun;
    Button cobalagi;
    String extra, judul;
    RecyclerView.LayoutManager layoutManager, layoutManager2;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        viewerrortoast = LayoutInflater.from(TipsActivity.this).inflate(R.layout.toast_error,(LinearLayout) findViewById(R.id.toast_error));

        txttoasterror = viewerrortoast.findViewById(R.id.texttoast);
        cobalagi = findViewById(R.id.cobafeeder);
        errorlayout = findViewById(R.id.errordata);
        layouttips = findViewById(R.id.layouttips);
        rv_naik = findViewById(R.id.rv_naik);
        rv_turun = findViewById(R.id.rv_turun);
        jdl = findViewById(R.id.judultips);
        txt_desc = findViewById(R.id.desc);
        txt_naik = findViewById(R.id.text_naik);
        txt_turun = findViewById(R.id.text_turun);
        progressBar = findViewById(R.id.progressbar);
        Sprite chasingDots = new ChasingDots();
        progressBar.setIndeterminateDrawable(chasingDots);
        chasingDots.setColor(Color.rgb(3,169,244));
        bstoast = new BottomSheetDialog(TipsActivity.this);

        Bundle bundle = getIntent().getExtras();
        extra = bundle.getString("nama");
        switch (extra){
            case "ph" :
                judul = "Tips PH Air Tambak";
                break;
            default :
                judul = "Tips Salinitas Air Tambak";
                break;
        }
        jdl.setText(judul);

        new ConnectionTask(TipsActivity.this).execute();
        cobalagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConnectionTask(TipsActivity.this).execute();
            }
        });
    }

    public void showLayout(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layouttips.setVisibility(View.VISIBLE);
                errorlayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        },2000);
    }
    public void errorLayout(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layouttips.setVisibility(View.INVISIBLE);
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
                    txt_desc.setText(Html.fromHtml(dataSnapshot.child("desc").getValue(String.class)));
                    txt_naik.setText(Html.fromHtml(dataSnapshot.child("text_naik").getValue(String.class)));
                    txt_turun.setText(Html.fromHtml(dataSnapshot.child("text_turun").getValue(String.class)));
                    //Inisialisasi ArrayList
                    list_naik = new ArrayList<>();
                    list_turun = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.child("naik").getChildren()){
                        //Mapping data pada DataSnapshot ke dalam objek jadwal
                        TipsModel tipsModel = snapshot.getValue(TipsModel.class);
                        list_naik.add(tipsModel);
                    }
                    for (DataSnapshot snapshot : dataSnapshot.child("turun").getChildren()){
                        //Mapping data pada DataSnapshot ke dalam objek jadwal
                        TipsModel tipsModel1 = snapshot.getValue(TipsModel.class);
                        list_turun.add(tipsModel1);
                    }
                    //Inisialisasi Adapter dan data Mahasiswa dalam bentuk Array
                    tipsAdapterturun = new TipsAdapter(list_turun, TipsActivity.this);
                    tipsAdapternaik = new TipsAdapter(list_naik, TipsActivity.this);
                    //Memasang Adapter pada RecyclerView
                    rv_turun.setAdapter(tipsAdapterturun);
                    rv_naik.setAdapter(tipsAdapternaik);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            layoutManager = new LinearLayoutManager(this);
            layoutManager2 = new LinearLayoutManager(this);
            rv_naik.setLayoutManager(layoutManager);
            rv_naik.setHasFixedSize(true);
            rv_turun.setLayoutManager(layoutManager2);
            rv_turun.setHasFixedSize(true);
        } else {
            errorLayout();
        }
    }
}