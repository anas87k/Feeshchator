package com.example.monitoring.activity;

import android.os.Bundle;

import com.example.monitoring.R;
import com.example.monitoring.adapter.TabAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {
    private TabAdapter tabAdapter;
    private TabLayout tabs;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        viewPager = findViewById(R.id.view_pager);
        tabs = findViewById(R.id.tabs);

        tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabAdapter.addFragment(new InfoFragment(),"Panduan");
        tabAdapter.addFragment(new PasutFragment(),"Pasang Surut");
        viewPager.setAdapter(tabAdapter);
        tabs.setupWithViewPager(viewPager);
    }
}