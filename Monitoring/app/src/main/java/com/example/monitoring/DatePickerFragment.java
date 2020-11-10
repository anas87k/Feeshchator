package com.example.monitoring;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.monitoring.activity.ChartActivity;

import java.text.ParseException;

import androidx.fragment.app.DialogFragment;

import static com.example.monitoring.DataInterface.DateFormat;
import static com.example.monitoring.DataInterface.grafikurl;
import static com.example.monitoring.DataInterface.simpleDate;
import static com.example.monitoring.DataInterface.tanggal;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int year = Integer.parseInt(tanggal.substring(4,8));
        int day = Integer.parseInt(tanggal.substring(0,2));
        int month = Integer.parseInt(tanggal.substring(2,4));

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                R.style.AlertDialogStyle,this,year,month-1,day);
        return datepickerdialog;
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String bulan = String.format("%02d", month+1);
        String tgl = String.format("%02d",day);
        String tahun = String.valueOf(year);
        EditText txttgl = getActivity().findViewById(R.id.date);

        try {
            txttgl.setText(DateFormat.format(simpleDate.parse(tgl+bulan+tahun)));
            tanggal = tgl+bulan+tahun;
            Log.e("TANGGAL", tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
