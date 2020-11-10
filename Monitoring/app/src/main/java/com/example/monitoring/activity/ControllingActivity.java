package com.example.monitoring.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.monitoring.task.ConnectionTask;
import com.example.monitoring.interfacee.ConnectionInterface;
import com.example.monitoring.adapter.JadwalAdapter;
import com.example.monitoring.interfacee.JadwalInterface;
import com.example.monitoring.model.JadwalModel;
import com.example.monitoring.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;

import static com.example.monitoring.DataInterface.myDateFormat;
import static com.example.monitoring.DataInterface.simpleDateFormat;
import static com.example.monitoring.DataInterface.myRefFeeder;

public class ControllingActivity extends AppCompatActivity implements ConnectionInterface {
    Button tambah, simpan, btnon, btnoff, update, hapus, cobalagi;
    LinearLayout bs, bsupd;
    CardView bst;
    RelativeLayout indicator;
    EditText nama, tgl, durasi, time, namaupd, tglupd, durasiupd;
    TextView status, txttoasterror, txttoastsuccess;
    String tanggal, formattgl, getNama, getTgl, getDurasi,
            getNamaUpd, getTglUpd, getDurasiUpd, getKey;
    int th,bl,hr,jm,mn;
    long epoch;
    View viewerrortoast, viewsuccesstoast;
    final long epochnow = Calendar.getInstance(TimeZone.getTimeZone("GMT+7")).getTime().getTime()/1000;
    BottomSheetDialog bottomSheetadd, bottomSheetupd, bstoast;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    JadwalAdapter jadwalAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<JadwalModel> listjadwal;
    SwipeRefreshLayout feederswipe;
    ShimmerFrameLayout feedershimmer;
    RelativeLayout feederlayout, errorlayout;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlling);
        viewerrortoast = LayoutInflater.from(ControllingActivity.this).inflate(R.layout.toast_error,(LinearLayout) findViewById(R.id.toast_error));
        viewsuccesstoast = LayoutInflater.from(ControllingActivity.this).inflate(R.layout.toast_success,(LinearLayout) findViewById(R.id.toast_success));

        txttoasterror = viewerrortoast.findViewById(R.id.texttoast);
        txttoastsuccess = viewsuccesstoast.findViewById(R.id.txttoasts);

        tambah = findViewById(R.id.tambah);
        btnoff = findViewById(R.id.btnoff);
        btnon = findViewById(R.id.btnon);
        bs = findViewById(R.id.bstambah);
        cobalagi = findViewById(R.id.cobafeeder);
        bsupd = findViewById(R.id.bsupd);
        status = findViewById(R.id.stsctrl);
        time = findViewById(R.id.aturdurasi);
        recyclerView = findViewById(R.id.jadwalrv);
        feederswipe = findViewById(R.id.feederswipe);
        feederlayout = findViewById(R.id.feederlayout);
        errorlayout = findViewById(R.id.errorcontrol);
        feedershimmer = findViewById(R.id.shimmercontrol);
        indicator = findViewById(R.id.indicator);


        feedershimmer.startShimmer();
        final ConnectionTask connectionTask = new ConnectionTask(ControllingActivity.this);
        connectionTask.execute();

        feederswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ConnectionTask(ControllingActivity.this).execute();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        feederswipe.setRefreshing(false);
                    }
                },1500);
            }
        });

        //init bottom sheet
        bottomSheetadd = new BottomSheetDialog(ControllingActivity.this, R.style.BottomSheetDialogTheme);
        bottomSheetupd = new BottomSheetDialog(ControllingActivity.this, R.style.BottomSheetDialogTheme);
        bstoast = new BottomSheetDialog(ControllingActivity.this);

        cobalagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConnectionTask(ControllingActivity.this).execute();
            }
        });
        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view = LayoutInflater.from(ControllingActivity.this).inflate(R.layout.bottom_sheet_add,(LinearLayout) findViewById(R.id.bstambah));
                nama = view.findViewById(R.id.nama);
                tgl = view.findViewById(R.id.tanggal);
                durasi = view.findViewById(R.id.durasi);
                simpan = view.findViewById(R.id.simpanjadwal);
                //SwitchDateTimeDialogFragment SHOW
                tgl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TanggalAdd(tgl, "DATETIME_ADD");
                    }
                });
                simpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getNama = nama.getText().toString();
                        getTgl = tgl.getText().toString();
                        getDurasi = durasi.getText().toString();
                        if (getNama.isEmpty() || getTgl.isEmpty() || getDurasi.isEmpty()){
                            txttoasterror.setText("Data Masih Ada yg Kosong!!");
                            errortoastShow();
                        } else {
                            if (epoch - epochnow > 1800){
                                new SweetAlertDialog(ControllingActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                        .setTitleText("Anda Yakin Menambahkan Jadwal "+nama.getText().toString())
                                        .setConfirmText("Simpan")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(final SweetAlertDialog sDialog) {
                                                //Cek Koneksi Jaringan
                                                new ConnectionTask(new ConnectionInterface() {
                                                    @Override
                                                    public void CekKoneksi(String s) {
                                                        if (s.equals("https")){
                                                            myRefFeeder.child("jadwal").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.getChildrenCount() < 7){
                                                                        myRefFeeder.child("jadwal/"+epoch).setValue(new JadwalModel(getNama,getTgl,getDurasi,"2"));
                                                                        sDialog.dismissWithAnimation();
                                                                        bottomSheetadd.dismiss();
                                                                        txttoastsuccess.setText("Jadwal Berhasil Ditambahkan, Silahkan Refresh Tampilan Jika Tidak Muncul");
                                                                        successtoastShow();
                                                                    } else {
                                                                        sDialog.dismissWithAnimation();
                                                                        txttoasterror.setText("Jumlah Jadwal Sudah Mencapai Maksimal");
                                                                        errortoastShow();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                        } else {
                                                            sDialog.dismissWithAnimation();
                                                            bottomSheetadd.dismiss();
                                                            txttoasterror.setText("Anda Tidak Terhubung Ke Internet, Data Gagal Dikirim!! Silahkan Refresh dan Coba Lagi");
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
                            } else{
                                txttoasterror.setText("Jadwal Terlalu Dekat Dijalankan, Tambahkan Jadwal Minimal 30 Menit dari Waktu Sekarang atau Gunakan Atur Sekarang");
                                errortoastShow();
                            }
                        }
                    }
                });
                bottomSheetadd.setCanceledOnTouchOutside(false);
                bottomSheetadd.setContentView(view);
                bottomSheetadd.show();
            }
        });

        MyRecyclerView();

    }

    @Override
    public void CekKoneksi(String s) {
        if (s.equals("https")){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    myRefFeeder.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String statusvalue = dataSnapshot.child("status").getValue(Integer.class).toString();
                            String statusjadwal = dataSnapshot.child("statusjadwal").getValue(Integer.class).toString();
                            String indikator = dataSnapshot.child("request").getValue(Integer.class).toString();
                            if (indikator.equals("2")){
                                indicator.setBackgroundColor(Color.rgb(0,188,212));
                                btnon.setBackground(getResources().getDrawable(R.drawable.shape_add_button));
                                btnon.setText("Atur Sekarang");
                            } else {
                                indicator.setBackgroundColor(Color.rgb(233,30,90));
                                btnon.setBackground(getResources().getDrawable(R.drawable.shape_add_button));
                                btnon.setText("Atur Sekarang");
                            }

                            if (statusvalue != null){
                                if (statusvalue.equals("1") || statusjadwal.equals("1")){
                                    status.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
                                    status.setText("Mesin Hidup");
                                    time.setEnabled(false);
                                    btnon.setEnabled(false);
                                    tambah.setEnabled(false);
                                } else if (indikator.equals("1")){
                                    indicator.setBackgroundColor(Color.rgb(255,193,7));
                                    status.setText("Mesin Mati");
                                    status.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLayout2));
                                    btnon.setText("Batalkan Pengendalian");
                                    btnon.setBackground(getResources().getDrawable(R.drawable.button_batalkan));
                                    tambah.setEnabled(false);
                                    btnoff.setEnabled(false);
                                    time.setEnabled(false);
                                    time.setText("");
                                } else {
                                    status.setText("Mesin Mati");
                                    status.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLayout2));
                                    btnon.setEnabled(true);
                                    tambah.setEnabled(true);
                                }
                                showLayout();
                                DataJadwal();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                        }
                    });
                }
            },2000);
        } else if (s.equals("pakan")){
            feederlayout.setVisibility(View.VISIBLE);
            errorlayout.setVisibility(View.INVISIBLE);
            m_Runnable.run();
        }
        else {
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
        feedershimmer.setVisibility(View.INVISIBLE);
        feedershimmer.stopShimmer();
        feederswipe.setRefreshing(false);
    }

    public void showLayout(){
        feederlayout.setVisibility(View.VISIBLE);
        errorlayout.setVisibility(View.INVISIBLE);
        feedershimmer.stopShimmer();
        feedershimmer.setVisibility(View.INVISIBLE);
        feederswipe.setRefreshing(false);
    }
    public void errorLayout(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                feederlayout.setVisibility(View.INVISIBLE);
                errorlayout.setVisibility(View.VISIBLE);
                feedershimmer.stopShimmer();
                feedershimmer.setVisibility(View.INVISIBLE);
                feederswipe.setRefreshing(false);
                status.setText("Gagal Memuat");
                status.setTextColor(getApplicationContext().getResources().getColor(R.color.main_orange_light_color));

                txttoasterror.setText("Anda Sedang Offline, Hubungkan Ke Internet atau WiFi Feeder");
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
        },3500);
    }

    private final Runnable m_Runnable = new Runnable() {
        @Override
        public void run() {
            OkHttpClient okHttpClient = new OkHttpClient()
                    .newBuilder()
                    .build();
            AndroidNetworking.initialize(getApplicationContext(), okHttpClient);
            AndroidNetworking.get("http://172.16.0.2/status")
                    .setTag("test")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            status.setText(response);
                            status.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
                            if (response.equals("1")){
                                status.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
                                status.setText("Mesin Hidup");
                                time.setEnabled(false);
                                btnon.setEnabled(false);
                                tambah.setEnabled(false);
                            } else {
                                status.setText("Mesin Mati");
                                status.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLayout2));
                                btnon.setEnabled(true);
                                tambah.setEnabled(false);
                            }
                            showLayout();
                        }
                        @Override
                        public void onError(ANError anError) {
                        }
                    });
            ControllingActivity.this.mHandler.postDelayed(m_Runnable,1500);
        }
    };

    public void DataJadwal(){
        myRefFeeder.child("jadwal").orderByChild("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Inisialisasi ArrayList
                listjadwal = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //Mapping data pada DataSnapshot ke dalam objek jadwal
                    JadwalModel jadwalModel = snapshot.getValue(JadwalModel.class);

                    //Mengambil Primary Key, digunakan untuk proses Update dan Delete
                    jadwalModel.setKey(snapshot.getKey());
                    listjadwal.add(jadwalModel);
                }

                //Inisialisasi Adapter dan data Mahasiswa dalam bentuk Array
                jadwalAdapter = new JadwalAdapter(listjadwal, ControllingActivity.this);
                //Memasang Adapter pada RecyclerView
                recyclerView.setAdapter(jadwalAdapter);
                jadwalAdapter.setJadwalInterface(new JadwalInterface() {
                    @Override
                    public void OnItemClick(int position, final JadwalModel jadwalModel) {
                        final View view = LayoutInflater.from(ControllingActivity.this).inflate(R.layout.bottom_sheet_update,(LinearLayout) findViewById(R.id.bsupd));
                        update = view.findViewById(R.id.updatejadwal);
                        hapus = view.findViewById(R.id.deletejadwal);
                        namaupd = view.findViewById(R.id.namaupdate);
                        tglupd = view.findViewById(R.id.tglupdate);
                        durasiupd = view.findViewById(R.id.durasiupdate);

                        namaupd.setText(jadwalModel.getNama());
                        getKey = jadwalModel.getKey();
                        epoch = Long.parseLong(getKey);
                        try {
                            formattgl = simpleDateFormat.format(myDateFormat.parse(jadwalModel.getDatetime()));
                            tglupd.setText(jadwalModel.getDatetime());
                            th = Integer.parseInt(formattgl.substring(4,8));
                            hr = Integer.parseInt(formattgl.substring(0,2));
                            bl = Integer.parseInt(formattgl.substring(2,4));
                            jm = Integer.parseInt(formattgl.substring(8,10));
                            mn = Integer.parseInt(formattgl.substring(10));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        durasiupd.setText(jadwalModel.getDurasi());

                        tglupd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TanggalAdd(tglupd, "DATETIME_UPDATE");
                            }
                        });
                        if (jadwalModel.getStatus().equals("0")){
                            txttoasterror.setText("Jadwal Sedang Berjalan!!");
                            errortoastShow();
                        } else {
                            bottomSheetupd.setCanceledOnTouchOutside(false);
                            bottomSheetupd.setContentView(view);
                            bottomSheetupd.show();
                        }
                        update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getNamaUpd = namaupd.getText().toString();
                                getTglUpd = tglupd.getText().toString();
                                getDurasiUpd = durasiupd.getText().toString();

                                if (getNamaUpd.isEmpty() || getTglUpd.isEmpty() || getDurasiUpd.isEmpty()){
                                    txttoasterror.setText("Data Masih Ada yg Kosong!!");
                                    errortoastShow();
                                } else {
                                    if (epoch - epochnow > 1800){
                                        new SweetAlertDialog(ControllingActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                                .setTitleText("Anda Yakin Mengupdate Jadwal "+namaupd.getText().toString())
                                                .setConfirmText("Update")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(final SweetAlertDialog sDialog) {
                                                        new ConnectionTask(new ConnectionInterface() {
                                                            @Override
                                                            public void CekKoneksi(String s) {
                                                                if (s.equals("https")){
                                                                    myRefFeeder.child("jadwal/"+getKey).removeValue();
                                                                    myRefFeeder.child("jadwal/"+epoch).setValue(new JadwalModel(getNamaUpd,getTglUpd,getDurasiUpd,"2"));
                                                                    sDialog.dismissWithAnimation();
                                                                    bottomSheetupd.dismiss();
                                                                    txttoastsuccess.setText("Jadwal Berhasil Diupdate, Silahkan Refresh Tampilan Jika Tidak Ada Perubahan");
                                                                    successtoastShow();

                                                                } else {
                                                                    sDialog.dismissWithAnimation();
                                                                    bottomSheetupd.dismiss();
                                                                    txttoasterror.setText("Anda Tidak Terhubung Ke Internet, Data Gagal Diupdate!! Silahkan Refresh dan Coba Lagi");
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
                                    } else{
                                        txttoasterror.setText("Jadwal Terlalu Dekat Dijalankan, Tambahkan Jadwal Minimal 30 Menit dari Waktu Sekarang atau Gunakan Atur Sekarang");
                                        errortoastShow();
                                    }
                                }
                            }
                        });

                        hapus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new SweetAlertDialog(ControllingActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                        .setTitleText("Anda Yakin Menghapus Jadwal "+namaupd.getText().toString())
                                        .setConfirmText("HAPUS")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(final SweetAlertDialog sDialog) {
                                                //Cek Koneksi Jaringan
                                                new ConnectionTask(new ConnectionInterface() {
                                                    @Override
                                                    public void CekKoneksi(String s) {
                                                        if (s.equals("https")){
                                                            myRefFeeder.child("jadwal/"+getKey).removeValue();
                                                            sDialog.dismissWithAnimation();
                                                            bottomSheetupd.dismiss();
                                                            txttoastsuccess.setText("Jadwal Berhasil Dihapus, Silahkan Refresh Tampilan Jika Tidak Berubah");
                                                            successtoastShow();
                                                        } else {
                                                            sDialog.dismissWithAnimation();
                                                            bottomSheetupd.dismiss();
                                                            txttoasterror.setText("Anda Tidak Terhubung Ke Internet, Data Gagal Dikirim!! Silahkan Refresh dan Coba Lagi");
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
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void BtnBatal(View view) {
        time.setEnabled(false);
        time.setText("");
        btnon.setText(R.string.txt_atur_skrg);
        btnoff.setEnabled(false);
        tambah.setEnabled(true);
    }

    public void MyRecyclerView(){
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    public void AturSekarang(View view){
        if (btnon.getText().toString().equals("Atur Sekarang")){
            btnon.setText(R.string.txt_hdp_skrg);
            time.setEnabled(true);
            btnoff.setEnabled(true);
            tambah.setEnabled(false);
        } else if (btnon.getText().equals("Hidupkan Sekarang")){
            final String getdurasiskrg = time.getText().toString();
            if (getdurasiskrg.equals("")){
                txttoasterror.setText("Data masih Kosong");
                errortoastShow();
            } else {
                new ConnectionTask(new ConnectionInterface() {
                    @Override
                    public void CekKoneksi(String s) {
                        if (s.equals("https")){
                            new SweetAlertDialog(ControllingActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Anda Yakin Mengatur Sekarang dengan Durasi "+getdurasiskrg+" detik dengan Jaringan Internet?")
                                    .setConfirmText("Simpan")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(final SweetAlertDialog sDialog) {
                                            myRefFeeder.child("durasi").setValue(getdurasiskrg);
                                            myRefFeeder.child("request").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    time.setEnabled(false);
                                                    time.setText("");
                                                    btnoff.setEnabled(false);
                                                    tambah.setEnabled(false);
                                                    sDialog.dismissWithAnimation();
                                                    txttoastsuccess.setText("Data Berhasil diKirim, Mesin Akan Hidup");
                                                    successtoastShow();
                                                }
                                            });
                                        }
                                    })
                                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                        }
                                    })
                                    .show();
                        } else if (s.equals("pakan")){
                            new SweetAlertDialog(ControllingActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Anda Yakin Mengatur Sekarang dengan Durasi "+getdurasiskrg+" detik dengan Jaringan Sistem?")
                                    .setConfirmText("Simpan")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(final SweetAlertDialog sDialog) {
                                            OkHttpClient okHttpClient = new OkHttpClient()
                                                    .newBuilder()
                                                    .build();
                                            AndroidNetworking.initialize(getApplicationContext(), okHttpClient);
                                            AndroidNetworking.get("http://172.16.0.2/get?durasi="+getdurasiskrg)
                                                    .setPriority(Priority.HIGH)
                                                    .build()
                                                    .getAsString(new StringRequestListener() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            time.setEnabled(false);
                                                            time.setText("");
                                                            btnon.setText(R.string.txt_atur_skrg);
                                                            txttoastsuccess.setText("Data Berhasil diKirim, Mesin Akan Hidup");
                                                            successtoastShow();
                                                            sDialog.dismissWithAnimation();
                                                        }
                                                        @Override
                                                        public void onError(ANError anError) {
                                                        }
                                                    });
                                        }
                                    })
                                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                        }
                                    })
                                    .show();
                        } else {
                            txttoasterror.setText("Anda Tidak Terhubung Ke Internet atau Sistem, Data Gagal Dikirim!! Silahkan Refresh dan Coba Lagi");
                            errortoastShow();
                        }
                    }
                }).execute();
            }
        } else {
            new ConnectionTask(new ConnectionInterface() {
                @Override
                public void CekKoneksi(String s) {
                    if (s.equals("https")){
                        new SweetAlertDialog(ControllingActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Anda Yakin Membatalkan Pengendalian Pemberian Pakan?")
                                .setConfirmText("Ya")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(final SweetAlertDialog sDialog) {
                                        myRefFeeder.child("request").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                sDialog.dismissWithAnimation();
                                                btnon.setText(R.string.txt_atur_skrg);
                                                btnon.setBackground(getResources().getDrawable(R.drawable.shape_add_button));
                                                txttoastsuccess.setText("Data Berhasil diKirim");
                                                successtoastShow();
                                            }
                                        });
                                    }
                                })
                                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                    }else {
                        txttoasterror.setText("Anda Tidak Terhubung Ke Internet Data Gagal Dikirim!! Silahkan Refresh dan Coba Lagi");
                        errortoastShow();
                    }
                }
            }).execute();
        }
    }

    public void TanggalAdd(final EditText editText, String TAG){
        SwitchDateTimeDialogFragment dateTimeDialogFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG);
        if(dateTimeDialogFragment == null) {
            dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel),
                    getString(R.string.clean) // Optional
            );
        }
        // Assign unmodifiable values
        dateTimeDialogFragment.set24HoursMode(true);
//        dateTimeFragment.setHighlightAMPMSelection(false);
        dateTimeDialogFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeDialogFragment.setMaximumDateTime(new GregorianCalendar(2045, Calendar.DECEMBER, 31).getTime());

        // Define new day and month format
        try {
            dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", new Locale("id","ID")));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
        }
        // Set listener for date
        // Or use dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onNeutralButtonClick(Date date) {
                editText.setText("");
            }

            @Override
            public void onPositiveButtonClick(Date date) {
                editText.setText(myDateFormat.format(date));
                try {
                    epoch = (myDateFormat.parse(editText.getText().toString()).getTime())/1000;
                    tanggal = String.valueOf(epoch);
                } catch (ParseException e) {
                }
                String dt = simpleDateFormat.format(date);
                th = Integer.parseInt(dt.substring(4,8));
                hr = Integer.parseInt(dt.substring(0,2));
                bl = Integer.parseInt(dt.substring(2,4));
                jm = Integer.parseInt(dt.substring(8,10));
                mn = Integer.parseInt(dt.substring(10));
            }

            @Override
            public void onNegativeButtonClick(Date date) {

            }
        });


        final String datetime = editText.getText().toString();
        String today = simpleDateFormat.format(Calendar.getInstance().getTime());
        int thn,hrn,bln,jmn,mnn;
        thn = Integer.parseInt(today.substring(4,8));
        hrn = Integer.parseInt(today.substring(0,2));
        bln = Integer.parseInt(today.substring(2,4));
        jmn = Integer.parseInt(today.substring(8,10));
        mnn = Integer.parseInt(today.substring(10));
        if(datetime.isEmpty()){
            dateTimeDialogFragment.startAtCalendarView();
            dateTimeDialogFragment.setDefaultDateTime(new GregorianCalendar(thn, bln-1,hrn,jmn,mnn).getTime());
            dateTimeDialogFragment.show(getSupportFragmentManager(), TAG);
        } else {
            dateTimeDialogFragment.startAtCalendarView();
            dateTimeDialogFragment.setDefaultDateTime(new GregorianCalendar(th, bl-1,hr,jm,mn).getTime());
            dateTimeDialogFragment.show(getSupportFragmentManager(), TAG);
        }

    }
}