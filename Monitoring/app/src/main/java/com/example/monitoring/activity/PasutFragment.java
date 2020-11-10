package com.example.monitoring.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.example.monitoring.R;
import com.example.monitoring.activity.PasutActivity;

public class PasutFragment extends Fragment {

    private WebView webView;
    RelativeLayout relativeLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        View view = inflater.inflate(R.layout.fragment_pasut, container, false);
        relativeLayout = view.findViewById(R.id.pasut_rotate);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            Intent intent = new Intent(getActivity(), PasutActivity.class);
            startActivity(intent);
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            Activity a = getActivity();
            if  (a!=null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
}