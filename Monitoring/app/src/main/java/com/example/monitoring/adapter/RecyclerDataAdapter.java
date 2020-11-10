package com.example.monitoring.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.monitoring.R;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.monitoring.DataInterface.formatwaktu;
import static com.example.monitoring.DataInterface.myDateFormat;

class RecyclerDataAdapter extends RecyclerView.Adapter<RecyclerDataAdapter.ViewHolder> {
    private ArrayList<Float> listdata;
    private ArrayList<Long> listwaktu;
    private String satuan;
    public RecyclerDataAdapter(ArrayList<Float> listdata, String satuan, ArrayList<Long> listwaktu) {
        this.listdata = listdata;
        this.satuan = satuan;
        this.listwaktu = listwaktu;
    }
    @NonNull
    @Override
    public RecyclerDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_data_monitor, parent, false);
        return new RecyclerDataAdapter.ViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerDataAdapter.ViewHolder holder, final int position) {
        final Float Data = listdata.get(position);
        final Long Waktu = listwaktu.get(position);
        Date date = new Date(Waktu);
        formatwaktu.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String formattedDate = formatwaktu.format(date);
        //Memasukan Nilai/Value kedalam View (TextView)
        holder.waktu.setText(formattedDate);
        holder.data.setText(Data.toString()+" "+satuan);
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView waktu, data;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            waktu = itemView.findViewById(R.id.txtwaktu);
            data = itemView.findViewById(R.id.txtdata);
        }
    }

}
