package com.example.monitoring;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.Date;
import java.util.TimeZone;

import static com.example.monitoring.DataInterface.formatwaktu;

public class MyMarkerView extends MarkerView {
    private final TextView tvContent;
    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        Date date = new Date((long)e.getX());
        formatwaktu.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        tvContent.setText("" +formatwaktu.format(date)+ "    " +e.getY());
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}