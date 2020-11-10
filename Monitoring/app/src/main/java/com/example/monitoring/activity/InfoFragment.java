package com.example.monitoring.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.monitoring.R;
import com.example.monitoring.activity.PanduanActivity;

public class InfoFragment extends Fragment implements View.OnClickListener {
    CardView cvph, cvsal, cvpantau, cvcontrol, cvpasut;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        cvph = view.findViewById(R.id.cvph);
        cvsal = view.findViewById(R.id.cvsal);
        cvpantau = view.findViewById(R.id.cvpantau);
        cvcontrol = view.findViewById(R.id.cvcontrol);
        cvpasut = view.findViewById(R.id.cvpasut);

        cvph.setOnClickListener(this);
        cvsal.setOnClickListener(this);
        cvpantau.setOnClickListener(this);
        cvcontrol.setOnClickListener(this);
        cvpasut.setOnClickListener(this);
        return view;
    }

    void CvClick(int id){
        Intent intent = new Intent(getContext(), PanduanActivity.class);
        Intent i = new Intent(getContext(), TipsActivity.class);
        switch (id){
            case R.id.cvph :
                i.putExtra("nama","ph");
                startActivity(i);
                break;
            case R.id.cvsal :
                i.putExtra("nama","salinitas");
                startActivity(i);
                break;
            case R.id.cvpantau :
                intent.putExtra("nama","monitor");
                startActivity(intent);
                break;
            case R.id.cvcontrol :
                intent.putExtra("nama","control");
                startActivity(intent);
                break;
            case R.id.cvpasut :
                intent.putExtra("nama","pasut");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            Activity a = getActivity();
            if  (a!=null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onClick(View view) {
        CvClick(view.getId());
    }
}