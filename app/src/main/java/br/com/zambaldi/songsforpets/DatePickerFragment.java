package br.com.zambaldi.songsforpets;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.util.TimeFormatException;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.text.Format;
import java.util.Date;

/**
 * Created by eduardo on 01/09/17.
 */



public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public int quemChamou;

    public void setQuemChamou(int quemChamou) {
        this.quemChamou = quemChamou;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it


        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onDateSet(DatePicker view, int year, int month, int day) {


                Log.w("DatePicker","Date = " + year);

                Calendar mDate = Calendar.getInstance();
                mDate.set(year,month,day);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                df.format(mDate.getTime());

                ((Button) getActivity().findViewById(quemChamou)).setText(df.format(mDate.getTime()));

    }

}
