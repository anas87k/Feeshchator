package com.example.monitoring.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import static com.example.monitoring.DataInterface.myDateFormat;
import static com.example.monitoring.DataInterface.myRefMonitor;
import com.example.monitoring.MonitorResult;
import com.example.monitoring.R;
import com.example.monitoring.interfacee.ConnectionInterface;
import com.example.monitoring.task.ConnectionTask;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MonitoringActivity extends AppCompatActivity implements ConnectionInterface {
    //Deklarasi Variable
    private TextView txtph, txtsuhu, txtsalinity, txtupdate, txttoasterror, txtfab1,txtfab2;
    private TextView lblsuhu, lblsal;
    RelativeLayout errorlayout, monitorlayout;
    SwipeRefreshLayout monitorswipe;
    ShimmerFrameLayout shimmerFrameLayout;
    View viewerrortoast;
    Button cobalagi;
    boolean isOpen;
    Handler mHandler;
    FloatingActionButton fab, fab1, fab2, fab3;
    Animation fab_open, fab_close, backward, forward;
    BottomSheetDialog bstoast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        this.mHandler = new Handler();
        viewerrortoast = LayoutInflater.from(MonitoringActivity.this).inflate(R.layout.toast_error,(LinearLayout) findViewById(R.id.toast_error));

        txttoasterror = viewerrortoast.findViewById(R.id.texttoast);

        FirebaseApp.initializeApp(this);
        txtsuhu = findViewById(R.id.txtsuhu);
        txtph = findViewById(R.id.txtph);
        txtsalinity = findViewById(R.id.txtsalinity);
        txtupdate = findViewById(R.id.update);
        lblsal = findViewById(R.id.lblsal);
        lblsuhu = findViewById(R.id.lblcelcius);
        cobalagi = findViewById(R.id.cobafeeder);

        errorlayout = findViewById(R.id.errormonitor);
        monitorlayout = findViewById(R.id.monitorlayout);
        monitorswipe = findViewById(R.id.monitorswipe);
        shimmerFrameLayout = findViewById(R.id.shimmermonitor);
        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);
        txtfab1 = findViewById(R.id.txtfab1);
        txtfab2 = findViewById(R.id.txtfab2);

        //Animation FAB
        fab_open = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        forward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        backward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MonitoringActivity.this, ChartActivity.class));
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MonitoringActivity.this, PanduanActivity.class));
            }
        });
        bstoast = new BottomSheetDialog(MonitoringActivity.this);

        shimmerFrameLayout.startShimmer();
        new ConnectionTask(MonitoringActivity.this).execute();

        monitorswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ConnectionTask(MonitoringActivity.this).execute();
            }
        });

        cobalagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConnectionTask(MonitoringActivity.this).execute();
            }
        });


    }

    private void animateFab(){
        if (isOpen){
            fab.startAnimation(forward);
            fab1.startAnimation(fab_close);
            fab2.setAnimation(fab_close);
            txtfab1.startAnimation(fab_close);
            txtfab2.setAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab1.setVisibility(View.INVISIBLE);
            fab2.setVisibility(View.INVISIBLE);
            txtfab1.setVisibility(View.INVISIBLE);
            txtfab2.setVisibility(View.INVISIBLE);
            isOpen = false;
        } else {
            fab.startAnimation(backward);
            fab1.startAnimation(fab_open);
            fab2.setAnimation(fab_open);
            txtfab1.startAnimation(fab_open);
            txtfab2.setAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab1.setVisibility(View.VISIBLE);
//            fab2.setVisibility(View.VISIBLE);
            txtfab1.setVisibility(View.VISIBLE);
//            txtfab2.setVisibility(View.VISIBLE);
            isOpen = true;
        }
    }

    public void showLayout(){
        monitorlayout.setVisibility(View.VISIBLE);
        errorlayout.setVisibility(View.INVISIBLE);
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.INVISIBLE);
        monitorswipe.setRefreshing(false);
    }
    public void errorLayout(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                monitorlayout.setVisibility(View.INVISIBLE);
                errorlayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.INVISIBLE);
                monitorswipe.setRefreshing(false);
                txtupdate.setText(" Gagal Memuat");
                txtupdate.setTextColor(getApplicationContext().getResources().getColor(R.color.main_orange_light_color));
                txttoasterror.setText("Anda Sedang Offline, Hubungkan Ke Internet atau WiFi Monitoring");
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.setVisibility(View.INVISIBLE);
        shimmerFrameLayout.stopShimmer();
        monitorswipe.setRefreshing(false);
    }


    @Override
    public void CekKoneksi(String s) {
        if (s.equals("https")){
            myRefMonitor.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final MonitorResult monitorResult = snapshot.getValue(MonitorResult.class);
                    if (monitorResult != null){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Date date = new Date(monitorResult.Update);
                                myDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
                                txtph.setText(monitorResult.PH);
                                txtsuhu.setText(monitorResult.Temperature);
                                txtsalinity.setText(monitorResult.Salinitas);
                                txtupdate.setText("Update: "+myDateFormat.format(date)+" WIB");
                                txtupdate.setTextColor(getApplicationContext().getResources().getColor(R.color.colorSecondaryText));
                                showLayout();
                            }
                        },2000);
                    }

                    if (Float.parseFloat(monitorResult.PH) > 8.50 || Float.parseFloat(monitorResult.PH) < 7.00)
                        txtph.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLayout2));
                    else txtph.setTextColor(getApplicationContext().getResources().getColor(R.color.colorSecondaryText));

                    if (Float.parseFloat(monitorResult.Salinitas) > 25.00 || Float.parseFloat(monitorResult.Salinitas) < 5.00) {
                        txtsalinity.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLayout2));
                        lblsal.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLayout2));
                    } else {
                        txtsalinity.setTextColor(getApplicationContext().getResources().getColor(R.color.colorSecondaryText));
                        lblsal.setTextColor(getApplicationContext().getResources().getColor(R.color.colorSecondaryText));
                    }

                    if (Float.parseFloat(monitorResult.Temperature) > 35.00 || Float.parseFloat(monitorResult.Temperature) < 27.00) {
                        txtsuhu.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLayout2));
                        lblsuhu.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLayout2));
                    } else {
                        txtsuhu.setTextColor(getApplicationContext().getResources().getColor(R.color.colorSecondaryText));
                        lblsuhu.setTextColor(getApplicationContext().getResources().getColor(R.color.colorSecondaryText));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (s.equals("air")){
            m_Runnable.run();
        } else {
            errorLayout();
        }
    }

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()
        {
            AndroidNetworking.initialize(getApplicationContext());
            AndroidNetworking.get("http://172.16.1.2/monitor")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String ph = response.getString("ph");
                                String sal = response.getString("sal");
                                String suhu = response.getString("suhu");
                                showLayout();
                                txtph.setText(ph);
                                txtsuhu.setText(suhu);
                                txtsalinity.setText(sal);
                                txtupdate.setText("Update: "+myDateFormat.format(Calendar.getInstance().getTime())+" WIB");
                                txtupdate.setTextColor(getApplicationContext().getResources().getColor(R.color.colorSecondaryText));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });

            MonitoringActivity.this.mHandler.postDelayed(m_Runnable, 90000);
        }
    };
}
