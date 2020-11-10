
package com.example.monitoring.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.monitoring.DatePickerFragment;
import com.example.monitoring.MonitorResult;
import com.example.monitoring.R;
import com.example.monitoring.adapter.ViewPagerDataAdapter;
import com.example.monitoring.interfacee.ConnectionInterface;
import com.example.monitoring.model.ChartModel;
import com.example.monitoring.task.ConnectionTask;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.Chart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.opencsv.CSVWriter;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.monitoring.DataInterface.DateFormat;
import static com.example.monitoring.DataInterface.formateDateFromstring;
import static com.example.monitoring.DataInterface.formatwaktu;
import static com.example.monitoring.DataInterface.grafikurl;
import static com.example.monitoring.DataInterface.myDateFormat;
import static com.example.monitoring.DataInterface.myRefFeeder;
import static com.example.monitoring.DataInterface.myRefGrafik;
import static com.example.monitoring.DataInterface.myRefMonitor;
import static com.example.monitoring.DataInterface.simpleDate;
import static com.example.monitoring.DataInterface.tanggal;

public class ChartActivity extends AppCompatActivity implements ConnectionInterface {
    ViewPager vplist;
    EditText txttgl;
    WormDotsIndicator vpindicator;
    ViewPagerDataAdapter viewPagerDataAdapter;
    Button btn, cobalagi;
    View viewerrortoast, viewsuccesstoast;
    RelativeLayout datalayout, errorlayout;
    ShimmerFrameLayout shimmerFrameLayout;
    TextView txttoasterror, txttoastsuccess;
    BottomSheetDialog bstoast;
    private ArrayList<Float> listph, listtemp, listsal;
    private ArrayList<Long> listwaktu;
    private ArrayList<String> dateList;
    ArrayList<String> getValues;
    List<String[]> da;
    String tgl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        viewerrortoast = LayoutInflater.from(ChartActivity.this).inflate(R.layout.toast_error,(LinearLayout) findViewById(R.id.toast_error));
        viewsuccesstoast = LayoutInflater.from(ChartActivity.this).inflate(R.layout.toast_success,(LinearLayout) findViewById(R.id.toast_success));

        txttoasterror = viewerrortoast.findViewById(R.id.texttoast);
        txttoastsuccess = viewsuccesstoast.findViewById(R.id.txttoasts);

        vplist = findViewById(R.id.vplist);
        txttgl = findViewById(R.id.date);
        cobalagi = findViewById(R.id.cobafeeder);
        datalayout = findViewById(R.id.layoutdata);
        errorlayout = findViewById(R.id.errordata);
        shimmerFrameLayout = findViewById(R.id.shimmerdata);
        vpindicator = findViewById(R.id.idktr);
        btn = findViewById(R.id.btntgl);

        grafikurl = simpleDate.format(Calendar.getInstance().getTime());
        tanggal = grafikurl;
        shimmerFrameLayout.startShimmer();
        final ConnectionTask connectionTask = new ConnectionTask(ChartActivity.this);
        connectionTask.execute();
        adapterVP();
        bstoast = new BottomSheetDialog(ChartActivity.this);
        try {
            String formattgl = DateFormat.format(simpleDate.parse(grafikurl));
            txttgl.setText(formattgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cobalagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConnectionTask(ChartActivity.this).execute();
            }
        });
        txttgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogfragment = new DatePickerFragment();
                dialogfragment.show(getSupportFragmentManager(), "Theme 1");
//                TanggalAdd(txttgl,"Tanggal");
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grafikurl = tanggal;
                adapterVP();
                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                datalayout.setVisibility(View.INVISIBLE);
                new ConnectionTask(new ConnectionInterface() {
                    @Override
                    public void CekKoneksi(String s) {
                        if (s.equals("https")){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myRefGrafik.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            boolean cek = snapshot.hasChildren();
                                            if (cek){
                                                showLayout();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            },2000);
                        }
                        else {
                            errorLayout();
                        }
                    }
                }).execute();
            }
        });
    }

    private void adapterVP(){
        viewPagerDataAdapter = new ViewPagerDataAdapter(getApplicationContext());
        vplist.setAdapter(viewPagerDataAdapter);
        vpindicator.setViewPager(vplist);
    }
    @Override
    public void CekKoneksi(String s) {
        if (s.equals("https")){
            myRefGrafik.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean cek = snapshot.hasChildren();
                    if (cek){
                        showLayout();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            errorLayout();
        }
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
//        feederswipe.setRefreshing(false);
    }

    public void showLayout(){
        datalayout.setVisibility(View.VISIBLE);
        errorlayout.setVisibility(View.INVISIBLE);
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.INVISIBLE);
//        feederswipe.setRefreshing(false);
    }
    public void errorLayout(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                datalayout.setVisibility(View.INVISIBLE);
                errorlayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.INVISIBLE);
//                feederswipe.setRefreshing(false);

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

    void successtoastShow(){
        bstoast.setContentView(viewsuccesstoast);
        bstoast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bstoast.dismiss();
            }
        },4500);
    }

    public void exportfile(View view){
        tgl = formateDateFromstring("ddMMyyyy", "dd MMM yyyy", grafikurl);
        new SweetAlertDialog(ChartActivity.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Anda Yakin Menyimpan Data Monitoring "+tgl+" ?")
                .setConfirmText("Simpan")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        new ConnectionTask(new ConnectionInterface() {
                            @Override
                            public void CekKoneksi(String s) {
                                if (s.equals("https")){
                                    myRefGrafik.child(grafikurl).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            //generate data
                                            StringBuilder data = new StringBuilder();
                                            listph = new ArrayList<>();
                                            listsal = new ArrayList<>();
                                            listtemp = new ArrayList<>();
                                            listwaktu = new ArrayList<>();
                                            getValues = new ArrayList<>();

                                            for (DataSnapshot mysnapshot : snapshot.getChildren()) {
                                                //Mapping data pada DataSnapshot ke dalam objek jadwal
                                                ChartModel chartModel = mysnapshot.getValue(ChartModel.class);
                                                listph.add(chartModel.getPh());
                                                listsal.add(chartModel.getSalinitas());
                                                listtemp.add(chartModel.getTemperature());
                                                listwaktu.add(chartModel.getWaktu());
                                                da = new ArrayList<String[]>();
                                                da.add(new String[]{"Data Monitoring Kualitas Air Tambak "+tgl});
                                                da.add(new String[]{""});
                                                da.add(new String[]{"Waktu","Kadar PH","Kadar Garam (ppm)", "Suhu Air (°C)"});
                                                for(int i = 0; i < listph.size(); i++) {
                                                    Date date = new Date(listwaktu.get(i));
                                                    myDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
                                                    da.add(new String[]{myDateFormat.format(date),String.valueOf(listph.get(i)),String.valueOf(listsal.get(i)),String.valueOf(listtemp.get(i))});
                                                }
                                            }
                                            File folder = new File(Environment.getExternalStorageDirectory()+ "/Feeshchator");
                                            if (!folder.exists())
                                                folder.mkdir();
                                            final String csv = folder.toString() + "/Feeshchator_Data-"+grafikurl+".csv";
                                            CSVWriter writer = null;
                                            try {
                                                writer = new CSVWriter(new FileWriter(csv));
                                                writer.writeAll(da); // data is adding to csv
                                                writer.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    sDialog.dismissWithAnimation();
                                    txttoastsuccess.setText("Data Monitoring Tanggal "+tgl+" Berhasil Disimpan. Silahkan Lihat di Internal/Feeshchator");
                                    successtoastShow();
                                } else {
                                    sDialog.dismissWithAnimation();
                                    txttoasterror.setText("Penyimpanan Gagal!! Hubungkan dengan Koneksi Internet");
                                    errortoastShow();
                                }
                            }
                        }).execute();
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
}