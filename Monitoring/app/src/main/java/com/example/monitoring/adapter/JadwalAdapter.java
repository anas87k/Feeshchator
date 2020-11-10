package com.example.monitoring.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.monitoring.model.JadwalModel;
import com.example.monitoring.R;
import com.example.monitoring.interfacee.JadwalInterface;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.ViewHolder>{

    private ArrayList<JadwalModel> listjadwal;
    private Context context;
    JadwalInterface jadwalInterface;
    public JadwalAdapter(ArrayList<JadwalModel> listjadwal, Context context) {
        this.listjadwal = listjadwal;
        this.context = context;
    }
    @NonNull
    @Override
    public JadwalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_jadwal, parent, false);
        return new ViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull JadwalAdapter.ViewHolder holder, final int position) {
        final String Tgl = listjadwal.get(position).getDatetime();
        final String Nama = listjadwal.get(position).getNama();
        final String Status = listjadwal.get(position).getStatus();
        if (Status.equals("2")){
            holder.status.setTextColor(context.getResources().getColor(R.color.colorPrimaryText));
            holder.status.setText("Terjadwal");
        } else if (Status.equals("3")){
            holder.status.setTextColor(context.getResources().getColor(R.color.colorLayout3));
            holder.status.setText("Selesai");
        } else if (Status.equals("0")){
            holder.status.setTextColor(context.getResources().getColor(R.color.colorLayout2));
            holder.status.setText("Berjalan");
        } else {
            holder.status.setText("Tidak Berjalan");
            holder.status.setTextColor(context.getResources().getColor(R.color.main_orange_light_color));
        }

        final String Durasi = listjadwal.get(position).getDurasi();

        //Memasukan Nilai/Value kedalam View (TextView)
        holder.nama.setText(Nama);
        holder.durasi.setText("Durasi: "+Durasi+" detik");
        holder.tanggal.setText(Tgl+" WIB");
        holder.cvlist.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                jadwalInterface.OnItemClick(position, listjadwal.get(position));
                                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listjadwal.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tanggal, nama, durasi, status;
        CardView cvlist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tanggal = itemView.findViewById(R.id.txttgl);
            nama = itemView.findViewById(R.id.txtnama);
            durasi = itemView.findViewById(R.id.txtdurasi);
            status = itemView.findViewById(R.id.statustxt);
            cvlist = itemView.findViewById(R.id.cvlist);
        }
    }

    public void setJadwalInterface(JadwalInterface jadwalInterface){
        this.jadwalInterface = jadwalInterface;
    }

    public void UpdateData(int position, JadwalModel jadwalModel){

        listjadwal.remove(position);
        listjadwal.add(jadwalModel);
        notifyItemChanged(position);
        notifyDataSetChanged();
    }
}
