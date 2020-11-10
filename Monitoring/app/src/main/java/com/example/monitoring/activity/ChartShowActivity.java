package com.example.monitoring.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.widget.TextView;

import com.example.monitoring.DataInterface;
import com.example.monitoring.MyMarkerView;
import com.example.monitoring.R;
import com.example.monitoring.model.ChartModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.example.monitoring.DataInterface.formateDateFromstring;
import static com.example.monitoring.DataInterface.formatwaktu;
import static com.example.monitoring.DataInterface.grafikurl;
import static com.example.monitoring.DataInterface.myRefGrafik;
import static com.example.monitoring.DataInterface.myRefMonitor;

public class ChartShowActivity extends AppCompatActivity {
    LineChart lineChart;
    LineDataSet lineDataSet = new LineDataSet(null,null);
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;
    float yvalue;
    String extra;
    LimitLine upper, lower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_show);
        Bundle bundle = getIntent().getExtras();
        extra = bundle.getString("nama");
        lineChart = findViewById(R.id.chartline);
        lineChart.setNoDataText("Data Grafik Tidak Tersedia.");
        lineChart.setNoDataTextColor(Color.rgb(3,169,244));
        myRefGrafik.child(grafikurl).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> DataVals = new ArrayList<Entry>();
                for (DataSnapshot mysnapshot : snapshot.getChildren()) {
                    ChartModel chartModel = mysnapshot.getValue(ChartModel.class);
                    if (chartModel != null){
                        switch (extra) {
                            case "ph":
                                yvalue = chartModel.getPh();
                                lineDataSet.setLabel("PH");
                                upper = new LimitLine(8.5f, "Batas Atas");
                                lower = new LimitLine(7f, "Batas Bawah");
                                break;
                            case "salinitas":
                                yvalue = chartModel.getSalinitas();
                                lineDataSet.setLabel("Salinitas (ppm)");
                                upper = new LimitLine(25f, "Batas Atas");
                                lower = new LimitLine(5f, "Batas Bawah");
                                break;
                            case "suhu":
                                yvalue = chartModel.getTemperature();
                                lineDataSet.setLabel("Temperature (°C)");
                                upper = new LimitLine(35f, "Batas Atas");
                                lower = new LimitLine(27f, "Batas Bawah");
                                break;
                        }
                        DataVals.add(new Entry(chartModel.getWaktu(), yvalue));
                    } else
                        DataVals.add(null);

                    ShowChart(DataVals);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //mChart.invalidate();

        // dont forget to refresh the drawing
        // mChart.invalidate();
    }

    private void ShowChart(ArrayList<Entry> DataVals){
        MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.my_marker_view);
        lineChart.setMarkerView(mv);
        upper.setLineWidth(2f);
        upper.enableDashedLine(10f,10f,0f);
        upper.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper.setTextSize(10f);

        lower.setLineWidth(2f);
        lower.enableDashedLine(10f,10f,0f);
        lower.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower.setTextSize(10f);

        YAxis leftaxisy = lineChart.getAxisLeft();
        leftaxisy.removeAllLimitLines();
        leftaxisy.addLimitLine(upper);
        leftaxisy.addLimitLine(lower);
        switch (extra){
            case "ph":
                leftaxisy.setAxisMaximum(14f);
                leftaxisy.setAxisMinimum(0f);
                break;
            case "salinitas":
                leftaxisy.setAxisMaximum(100f);
                leftaxisy.setAxisMinimum(0f);
                break;
            case "suhu":
                leftaxisy.setAxisMaximum(70f);
                leftaxisy.setAxisMinimum(0f);
                break;
        }
        leftaxisy.enableGridDashedLine(10f,10f,0f);
        leftaxisy.setDrawZeroLine(true);
        leftaxisy.setDrawLimitLinesBehindData(true);
        leftaxisy.setLabelCount(7,false);
        leftaxisy.setDrawGridLines(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                Date date = new Date((long)value);
                formatwaktu.setTimeZone(TimeZone.getTimeZone("GMT+7"));
                return formatwaktu.format(date);
            }
        });

        lineDataSet.setValues(DataVals);
        lineDataSet.setDrawIcons(false);
        lineDataSet.setColor(Color.rgb(3,169,244));
        lineDataSet.setCircleColor(Color.rgb(3,169,244));
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(0f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        lineDataSet.setFormSize(15.f);
        lineDataSet.setFillColor(Color.rgb(3,169,244));

        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);
        lineData = new LineData(iLineDataSets);
        String tanggal = formateDateFromstring("yyyy-MM-dd", "dd, MMM yyyy", grafikurl);
        Description description = lineChart.getDescription();
        description.setText("Waktu ("+tanggal+")");
        description.setTextSize(12f);

        lineChart.clear();
        lineChart.setData(lineData);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.animateX(2000, Easing.EaseInOutBounce);
        lineChart.invalidate();
    }
}