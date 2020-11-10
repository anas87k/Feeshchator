package com.example.monitoring.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.monitoring.R;
import com.example.monitoring.model.PanduanModel;
import com.example.monitoring.model.TipsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.ViewHolder> {
    private ArrayList<TipsModel> list_tips;
    Context context;
    public TipsAdapter(ArrayList<TipsModel> list_tips, Context context) {
        this.list_tips = list_tips;
        this.context = context;
    }
    @NonNull
    @Override
    public TipsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tips, parent, false);
        return new TipsAdapter.ViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsAdapter.ViewHolder holder, int position) {
        final int no = list_tips.get(position).getNo_tips();
        final String text = list_tips.get(position).getText_tips();
        holder.nomor.setText(String.valueOf(no));
        holder.text_tips.setText(Html.fromHtml(text));
    }

    @Override
    public int getItemCount() {
        return list_tips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomor, text_tips;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomor = itemView.findViewById(R.id.no_tips);
            text_tips = itemView.findViewById(R.id.text_tips);
        }
    }
}

