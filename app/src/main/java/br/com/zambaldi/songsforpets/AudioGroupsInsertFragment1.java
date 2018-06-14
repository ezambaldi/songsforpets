package br.com.zambaldi.songsforpets;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;


/**
 * Created by eduardo on 01/09/17.
 */

public class AudioGroupsInsertFragment1 extends Fragment implements AudioGroupsActivityInterface {

    private Button btnTime1;
    private Button btnTime2;
    public EditText edtName;
    private Button btnDate1;
    private Button btnDate2;
    private SeekBar seekBarInterval;
    private RadioButton rbType1;
    private RadioButton rbType2;
    private TextView txtValueInterval;
    public int nChamada;
    public int nChamada2;
    private int nId;
    private int nId_Group;
    private DataBase dataBase;
    private SQLiteDatabase conn;

    public void setrDate(String rDate) {
        this.rDate = rDate;
    }
    public String rDate = "";

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.audio_groups_insert_fragment1, container, false);
        View view = inflater.inflate(R.layout.audio_groups_insert_fragment1, container, false);

        SharedPreferences dados= PreferenceManager.getDefaultSharedPreferences(this.getContext());
        nChamada = dados.getInt("CHAMADA",0); // controla se é 0 insert ou 1 update
        nChamada2 = dados.getInt("CHAMADA2",0); // controla se saiu do foco activity = 1
        nId = dados.getInt("_IDD",0);


        // Conecta com banco de dados ***************************************************************************************
        try {
            dataBase = new DataBase(this.getContext());
            conn = dataBase.getWritableDatabase();

        } catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(this.getContext());
            dlg.setMessage("Error Database Create: " + ex.getMessage());
            dlg.setNeutralButton("Ok",null);
            dlg.show();
        }
        // *******************************************************************************************************************


        //Intent intent = getActivity().getIntent();
        //nChamada = intent.getIntExtra("CHAMADA",-1);
        //nId = intent.getIntExtra("_IDD",-1);

        class EventoTeclado implements TextView.OnEditorActionListener{
        @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i== EditorInfo.IME_ACTION_DONE){
                    if(edtName.getText().length() >0){

                        // aqui entra alguma ação após o ok do teclado


                    }
                }
                return false;
            }
        }
        EventoTeclado teclado = new EventoTeclado();
        EditText n_reseteo = (EditText)view.findViewById(R.id.edtName);
        n_reseteo.setOnEditorActionListener(teclado);

        btnTime1 = (Button) view.findViewById(R.id.btnTime1);
        btnTime2 = (Button) view.findViewById(R.id.btnTime2);
        btnDate1 = (Button) view.findViewById(R.id.btnDate1);
        btnDate2 = (Button) view.findViewById(R.id.btnDate2);
        edtName = (EditText) view.findViewById(R.id.edtName);
        seekBarInterval = (SeekBar) view.findViewById(R.id.seekBarInterval);
        rbType1 = (RadioButton) view.findViewById(R.id.rbType1);
        rbType2 = (RadioButton) view.findViewById(R.id.rbType2);
        txtValueInterval = (TextView) view.findViewById(R.id.txtValueInterval);

        btnDate1.setText("Insert Date Start");
        btnDate2.setText("Insert Date Finish");
        btnTime1.setText("Insert Time Start");
        btnTime2.setText("Insert Time Finish");
        edtName.setText("Insert Audio Name");
        seekBarInterval.setProgress(10);
        rbType1.setChecked(false);
        rbType2.setChecked(true);
        txtValueInterval.setText("10");

        if(nChamada == 1){

            String mSql = "select NAME_GROUP,TIME_START,TIME_FINISH,INTERVAL_MINUTES,DATE_TIME_START,DATE_TIME_FINISH,TYPE_EXECUTE from groups where _ID_GROUPS = '"+ nId+"'";
            Cursor cursor = conn.rawQuery(mSql, null);

            if(cursor.getCount() > 0){

                cursor.moveToFirst();
                edtName.setText(cursor.getString(0).toString());
                btnTime1.setText(cursor.getString(1).toString());
                btnTime2.setText(cursor.getString(2).toString());
                txtValueInterval.setText(cursor.getString(3).toString());
                btnDate1.setText(cursor.getString(4).toString());
                btnDate2.setText(cursor.getString(5).toString());

                if(cursor.getInt(6) == 0){
                    rbType1.setChecked(true);
                    rbType2.setChecked(false);

                } else {
                    rbType1.setChecked(false);
                    rbType2.setChecked(true);

                }
                seekBarInterval.setProgress(cursor.getInt(3));

            }

        }




        edtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String edt1 = edtName.getText().toString();
                String edt2 = "Insert Audio Name";

                if(edt1.equals(edt2)) {

                    edtName.setText("");

                }

            }
        });

        rbType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rbType1.setChecked(false);
                rbType2.setChecked(true);


            }
        });

        rbType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rbType2.setChecked(false);
                rbType1.setChecked(true);

            }
        });

        seekBarInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                txtValueInterval.setText(seekBarInterval.getProgress()+"");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDatePickerDialog(view);
  //              btnDate1.setTag(rDate);



            }
        });

        btnDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDatePickerDialog(view);
//                btnDate2.setTag(rDate);
            }
        });

        btnTime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showTimePickerDialog(view);

            }
        });

        btnTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showTimePickerDialog(view);

            }
        });




        return view;
    }

    public void showTimePickerDialog(View v) {
//        DialogFragment newFragment = new TimePickerFragment();
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setQuemChamou(v.getId());
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
//        DialogFragment newFragment = new DatePickerFragment();
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setQuemChamou(v.getId());
        newFragment.show(getFragmentManager(), "datePicker");

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void onPause(){
        super.onPause();



        SharedPreferences dados= PreferenceManager.getDefaultSharedPreferences(this.getContext());
        SharedPreferences.Editor meuEditor = dados.edit();

        meuEditor.putString("DATE1",btnDate1.getText().toString());
        meuEditor.putString("DATE2",btnDate2.getText().toString());
        meuEditor.putString("TIME1",btnTime1.getText().toString());
        meuEditor.putString("TIME2",btnTime2.getText().toString());
        meuEditor.putString("NAME",edtName.getText().toString());
        meuEditor.putString("INTERVAL",txtValueInterval.getText().toString());
        meuEditor.putInt("CHAMADA2",1);
        if(rbType1.isChecked()) {
            meuEditor.putInt("TYPE",0);
        } else{
            meuEditor.putInt("TYPE",1);
        }

        meuEditor.apply();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onResume(){

        super.onResume();

        SharedPreferences dados= PreferenceManager.getDefaultSharedPreferences(this.getContext());
        nChamada = dados.getInt("CHAMADA",0); // controla se é 0 insert ou 1 update
        nChamada2 = dados.getInt("CHAMADA2",0); // controla se saiu do foco activity = 1
        nId = dados.getInt("_IDD",0);

        if(nChamada2 == 1) {

            edtName.setText(dados.getString("NAME",""));
            btnDate1.setText(dados.getString("DATE1",""));
            btnDate2.setText(dados.getString("DATE2",""));
            btnTime1.setText(dados.getString("TIME1",""));
            btnTime2.setText(dados.getString("TIME2",""));
            txtValueInterval.setText(dados.getString("INTERVAL",""));
            if(txtValueInterval.getText().toString().trim() != ""){
                seekBarInterval.setProgress(Integer.parseInt(txtValueInterval.getText().toString()));
            }
            int ntype = dados.getInt("TYPE",0);
            if(ntype==0){
                rbType1.setChecked(true);
                rbType2.setChecked(false);
            } else {
                rbType1.setChecked(false);
                rbType2.setChecked(true);
            }

        }

    }


    @Override
    public int recordDataDetails() {



                    if(nChamada == 0){

                        int typeExec = 0;
                        if(rbType2.isChecked()) {
                            typeExec = 1;
                        }

                        String nSql = "insert into groups " +
                                "(NAME_GROUP,TYPE_EXECUTE,TIME_START,TIME_FINISH,INTERVAL_MINUTES,DATE_TIME_START,DATE_TIME_FINISH,LAST_ITEM,TIME_START_INTERVAL) " +
                                " values('"+ edtName.getText() +"',"+typeExec+",'"+btnTime1.getText()+"','"+btnTime2.getText()+"',"+txtValueInterval.getText()+",'"+btnDate1.getText()+"','"+btnDate2.getText()+"',0,'00:00:00')";
                        conn.execSQL(nSql);

                        String mSql2 = "select _ID_GROUPS from groups order by _ID_GROUPS desc LIMIT 1";
                        Cursor cursor2 = conn.rawQuery(mSql2, null);

                        if(cursor2.getCount() > 0) {
                            cursor2.moveToFirst();
                            nId_Group = cursor2.getInt(0);

                        }


                    } else {


                        int typeExec = 0;
                        if(rbType2.isChecked()) {
                            typeExec = 1;
                        }

                        String nSql = "update groups set " +
                                "NAME_GROUP = '"+ edtName.getText() +"', TYPE_EXECUTE = "+typeExec+" ,TIME_START = '"+btnTime1.getText()+"',TIME_FINISH = '"+btnTime2.getText()+"',INTERVAL_MINUTES = "+txtValueInterval.getText()+",DATE_TIME_START = '"+btnDate1.getText()+"',DATE_TIME_FINISH = '"+btnDate2.getText()+"' " +
                                " where _ID_GROUPS = "+ nId;
                        conn.execSQL(nSql);

                        nId_Group = nId;



                    }


            return nId_Group;


    }

    @Override
    public void recordDataListAudio(int nIdGroup) {

    }

    @Override
    public void nFinish(Activity activity) {

    }
}
