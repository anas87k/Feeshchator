package com.example.monitoring.activity;

import android.os.Bundle;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoring.R;
import com.example.monitoring.adapter.TipsAdapter;

import java.util.HashMap;
import java.util.List;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
}