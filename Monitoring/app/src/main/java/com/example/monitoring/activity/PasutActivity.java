package com.example.monitoring.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoring.R;

public class PasutActivity extends AppCompatActivity {
    private WebView webView;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        webView = (WebView) findViewById(R.id.webview);
        relativeLayout = findViewById(R.id.lay_rotate);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            relativeLayout.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl("http://pasut.maritimsemarang.com");

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
        } else  {
            finish();
//            relativeLayout.setVisibility(View.VISIBLE);
//            webView.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }
}