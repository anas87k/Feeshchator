package com.example.monitoring.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.monitoring.R;
import com.example.monitoring.activity.ChartShowActivity;
import com.example.monitoring.model.ChartModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static com.example.monitoring.DataInterface.grafikurl;
import static com.example.monitoring.DataInterface.myRefGrafik;

public class ViewPagerDataAdapter extends PagerAdapter {
    private Context context;
    private String[] judul = {"Kadar PH","Kadar Garam (ppm)","Suhu Air (°C)"};
    private String[] satuan = {"","ppm","°C"};
    private String[] btn = {"ph","salinitas","suhu"};
    LayoutInflater layoutInflater;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    private ArrayList<Float> listph, listtemp, listsal;
    private ArrayList<Long> listwaktu;
    private RecyclerDataAdapter recyclerDataAdapter;
    public ViewPagerDataAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return judul.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_viewpager, null);
        layoutManager = new LinearLayoutManager(layoutInflater.getContext());
        Button chartbtn = view.findViewById(R.id.btnchart);
        TextView jdl = view.findViewById(R.id.jdl);
        recyclerView = view.findViewById(R.id.rvvv);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        myRefGrafik.child(grafikurl).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listph = new ArrayList<>();
                listsal = new ArrayList<>();
                listtemp = new ArrayList<>();
                listwaktu = new ArrayList<>();

                for (DataSnapshot mysnapshot : snapshot.getChildren()){
                    //Mapping data pada DataSnapshot ke dalam objek jadwal
                    ChartModel chartModel = mysnapshot.getValue(ChartModel.class);
                    listph.add(chartModel.getPh());
                    listsal.add(chartModel.getSalinitas());
                    listtemp.add(chartModel.getTemperature());
                    listwaktu.add(chartModel.getWaktu());
                }

                //Inisialisasi Adapter dan data dalam bentuk Array
                if (position == 0)
                    recyclerDataAdapter = new RecyclerDataAdapter(listph,satuan[position],listwaktu);
                else if (position == 1)
                    recyclerDataAdapter = new RecyclerDataAdapter(listsal,satuan[position],listwaktu);
                else recyclerDataAdapter = new RecyclerDataAdapter(listtemp,satuan[position],listwaktu);
                recyclerView.setAdapter(recyclerDataAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChartShowActivity.class);
                intent.putExtra("nama",btn[position]);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        jdl.setText(judul[position]);
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
