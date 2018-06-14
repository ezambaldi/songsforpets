package br.com.zambaldi.songsforpets;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private DataBase dataBase;
    private SQLiteDatabase conn;

    private Button btnAudioList;
    private Button btnAudioGroups;
    private Button btnStart;
    private Button btnExit;
    private TextView textView;
    public int paraLoop = 0;

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAudioGroups = (Button)findViewById(R.id.btnAudioGroup);
        btnAudioList = (Button)findViewById(R.id.btnAudioList);
        btnStart = (Button)findViewById(R.id.btnStartStop);
        btnExit = (Button)findViewById(R.id.btnExit);
        textView = (TextView) findViewById(R.id.textView);

        // Conecta com banco de dados ***************************************************************************************

        try {
            dataBase = new DataBase((this));
            conn = dataBase.getWritableDatabase();

        } catch (SQLException ex){
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setMessage("Error Database Create: " + ex.getMessage());
            dlg.setNeutralButton("Ok",null);
            dlg.show();
        }

        final SensorEventListener mSensorListener = new SensorEventListener() {

            public void onSensorChanged(SensorEvent se) {
                float x = se.values[0];
                float y = se.values[1];
                float z = se.values[2];
                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
                float delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;


        class LongOperation extends AsyncTask<String, Void, String> {
            int nIdGroup;
            int nInterval;
            int ntypeExecute;
            int nLastItem;
            String nTimeStartInterval;
            String nName;
            String nDate1;
            String nTime1;
            String nDate2;
            String nTime2;
            String nType;
            MediaPlayer mPlay;
            int mTocando = 0;

            public void setmLoop(int mLoop) {
                this.mLoop = mLoop;
            }

            int mLoop = 0;

            @Override
            protected String doInBackground(String... params) {


                mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
                do {


                    //String mSql = "select _ID_GROUPS, NAME_GROUP, TYPE_EXECUTE, INTERVAL_MINUTES, TIME_START_INTERVAL, LAST_ITEM, DATE_TIME_START, TIME_START, DATE_TIME_FINISH, TIME_FINISH from groups " +
                    //        "order by date_time_start";


                        String mSql = "select _ID_GROUPS, NAME_GROUP, TYPE_EXECUTE, INTERVAL_MINUTES, TIME_START_INTERVAL, LAST_ITEM, DATE_TIME_START, TIME_START, DATE_TIME_FINISH, TIME_FINISH from groups " +
                        "where strftime('%Y-%m-%d',date_time_start) <= date() and " +
                        "strftime('%Y-%m-%d',date_time_finish) >= date() " +
                        "and time_start <= time() " +
                        "and time_finish >= time() " +
                        "order by date_time_start";


                    Cursor cursor = conn.rawQuery(mSql, null);

                    if(cursor.getCount() >0){

                        cursor.moveToFirst();
                        nIdGroup = cursor.getInt(0);
                        nName = cursor.getString(1);
                        ntypeExecute = cursor.getInt(2);
                        nInterval = cursor.getInt(3);
                        nTimeStartInterval = cursor.getString(4);
                        nLastItem = cursor.getInt(5);
                        nDate1 = cursor.getString(6);
                        nTime1 = cursor.getString(7);
                        nDate2 = cursor.getString(8);
                        nTime2 = cursor.getString(9);

                        if(ntypeExecute == 0) {
                            nType = "Shake";
                        } else {
                            nType = "Interval";
                        }

                        publishProgress();

                        if(ntypeExecute == 1){
                            try {
                                Thread.sleep(nInterval*1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        String mSql2 = "select a._fk_id_audios, a.order_execute, b.path_audio from groups_items a, audios b where a._fk_id_audios = b._id_audios and  _fk_id_groups = "+ nIdGroup +" order by order_execute";
                        Cursor cursor2 = conn.rawQuery(mSql2, null);


                        if(cursor2.getCount() >0){

                            cursor2.moveToFirst();
                            for(int i=0; i<cursor2.getCount(); i++){

                                if(cursor2.getInt(1) == nLastItem){

                                    String path = cursor2.getString(2);
                                    File file = new File(path);

                                    if(file.exists()) {

                                            try{

                                                Uri uri = Uri.fromFile(file);

                                                mPlay = MediaPlayer.create(MainActivity.this,uri);
                                                mPlay.setVolume(50,50);

                                                if(ntypeExecute == 1){
                                                    mPlay.start();
                                                } else {


                                                    do {

                                                        if(mAccel < -0.05 || mAccel > 0.05){
                                                            mPlay.start();
                                                            try {
                                                                Thread.sleep(nInterval*1000);
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                            break;
                                                        }

                                                    } while (false);


                                                }



                                                mTocando = 1;

                                            } catch (Exception e){

                                            }


                                    }

                                    setmLoop(paraLoop);

                                    if(mLoop != 0){
                                        break;
                                    }

                                    int nProximo;
                                    if(cursor2.isLast()){
                                        nProximo = 0;
                                    } else{
                                        nProximo = cursor2.getInt(1)+1;
                                    }
                                    String nSql = "update groups set last_item = "+ nProximo +" where _id_groups = "+ nIdGroup;
                                    conn.execSQL(nSql);


                                }

                                cursor2.moveToNext();


                            }


                        }


                    } else {


                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }


                } while (mLoop == 0);
                mSensorManager.unregisterListener(mSensorListener);
                return "Executed";
            }

            @Override
            protected void onPostExecute(String result) {

                textView.setText("Finish");

            }

            @Override
            protected void onPreExecute() {

                textView.setText("Starting.....");


            }

            @Override
            protected void onProgressUpdate(Void... values) {

                textView.setText("Group: "+nName+"\nType: "+nType+" - Interval: "+ nInterval+"\nDate Start: "+nDate1+" - Date Finish: "+nDate2+"\nTime Start: "+nTime1+" - Time Finish: "+nTime2+"\n ");

            }
        }


        btnAudioList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AudioListActivity.class);
                startActivity(i);

            }
        });

        btnAudioGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AudioGroupsActivity.class);
                startActivity(i);

            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btnStart.getContentDescription() == "STOP") {
                    btnStart.setContentDescription("START");
                    btnStart.setBackgroundResource(R.drawable.play);
                    //btnStart.setText("START");
                    paraLoop = 1;

                } else {
                    btnStart.setContentDescription("STOP");
                    btnStart.setBackgroundResource(R.drawable.stop);
                    //btnStart.setText("STOP");
                    paraLoop = 0;
                    LongOperation execSound = new LongOperation();
                    execSound.setmLoop(0);
                    execSound.execute();

                }

            }
        });



    }

    public void onPause(){
        super.onPause();

        SharedPreferences dados= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor meuEditor = dados.edit();
        meuEditor.putInt("CHAMADA",-1);
        meuEditor.apply();

    }

}
