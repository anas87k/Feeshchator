package com.example.monitoring.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.monitoring.R;
import com.example.monitoring.model.PanduanModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PanduanAdapter extends RecyclerView.Adapter<PanduanAdapter.ViewHolder> {
    private ArrayList<PanduanModel> list_panduan;
    Context context;
    public PanduanAdapter(ArrayList<PanduanModel> list_panduan, Context context) {
        this.list_panduan = list_panduan;
        this.context = context;
    }
    @NonNull
    @Override
    public PanduanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_panduan, parent, false);
        return new PanduanAdapter.ViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull PanduanAdapter.ViewHolder holder, int position) {
        final int no = list_panduan.get(position).getNo_panduan();
        final String panduan = list_panduan.get(position).getText_panduan();
        final String img = list_panduan.get(position).getImage_url();
        holder.no_panduan.setText(String.valueOf(no));
        holder.text_panduan.setText(Html.fromHtml(panduan));
        Glide.with(holder.img_panduan.getContext()).load(img).into(holder.img_panduan);
    }

    @Override
    public int getItemCount() {
        return list_panduan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView no_panduan, text_panduan;
        ImageView img_panduan;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            no_panduan = itemView.findViewById(R.id.no_panduan);
            text_panduan = itemView.findViewById(R.id.txt_panduan);
            img_panduan = itemView.findViewById(R.id.img_panduan);
        }
    }
}
