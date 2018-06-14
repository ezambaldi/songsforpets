package br.com.zambaldi.songsforpets;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.sql.Time;

/**
 * Created by eduardo on 01/09/17.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public int quemChamou;

    public void setQuemChamou(int quemChamou) {
        this.quemChamou = quemChamou;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


        Time mTime = new Time(0);
        mTime.setHours(hourOfDay);
        mTime.setMinutes(minute);

        ((Button) getActivity().findViewById(quemChamou)).setText(mTime.toString());


    }
}
