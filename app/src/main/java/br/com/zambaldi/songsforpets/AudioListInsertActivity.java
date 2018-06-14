package br.com.zambaldi.songsforpets;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.net.Uri.fromFile;

public class AudioListInsertActivity extends AppCompatActivity {

    private DataBase dataBase;
    private SQLiteDatabase conn;

    private Button btnRecordAudio;
    private Button btnSelectAudio;
    private Button btnInsertAudio;
    private Button btnCancelAudio;
    private TextView txtAudioSelected;
    private EditText edtAudioName;
    private String path_audio;
    private int RQS_RECORDING = 0;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list_insert);

        verifyStoragePermissions(this);

        btnRecordAudio = (Button)findViewById(R.id.btnRecordAudio_audiolistinsert);
        btnSelectAudio = (Button)findViewById(R.id.btnSelectAudio_audiolistinsert);
        btnInsertAudio = (Button)findViewById(R.id.btnInsert_audiolistinsert);
        btnCancelAudio = (Button)findViewById(R.id.btnCancel_audiolistinsert);
        txtAudioSelected = (TextView) findViewById(R.id.txtViewNameAudioSelected);
        edtAudioName = (EditText) findViewById(R.id.edtNameAudio);




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
        // *******************************************************************************************************************


        class EventoTeclado implements TextView.OnEditorActionListener{

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i== EditorInfo.IME_ACTION_DONE){
                    if(edtAudioName.getText().length() >0){

                        // aqui entra alguma ação após o ok do teclado


                    }
                }
                return false;
            }
        }

        EventoTeclado teclado = new EventoTeclado();
        EditText n_reseteo = (EditText)findViewById(R.id.edtNameAudio);
        n_reseteo.setOnEditorActionListener(teclado);


        btnInsertAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtAudioSelected.getText().toString() == ""){

                    android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(AudioListInsertActivity.this);
                    dlg.setMessage("Select audio");
                    dlg.setNeutralButton("Ok", null);
                    dlg.show();

                } else {

                    if(edtAudioName.getText().toString() != ""){

                        android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(AudioListInsertActivity.this);
                        dlg
                                .setMessage("Save?")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {


                                        conn.execSQL("insert into audios (name_audio,path_audio) values('"+ edtAudioName.getText() +"','"+ path_audio +"')");
                                        finish();

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                        dlg.show();

                    } else {

                        android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(AudioListInsertActivity.this);
                        dlg.setMessage("Name Audio is Empty");
                        dlg.setNeutralButton("Ok", null);
                        dlg.show();
                    }

                }




            }
        });

        edtAudioName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String edt1 = edtAudioName.getText().toString();
                String edt2 = "Insert here the audio name";

                if(edt1.equals(edt2)) {

                    edtAudioName.setText("");

                }

            }
        });

        btnCancelAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        btnRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //path_audio = "Path" + edtAudioName.getText();
                audioRecord();

            }
        });


        btnSelectAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                audioSelect();

            }
        });

    }



    public void audioSelect() {

        Intent takeAudioIntent = new Intent();
//        Intent takeAudioIntent = new Intent(MediaStore.Audio.Media.ALBUM);
        takeAudioIntent.setAction(Intent.ACTION_GET_CONTENT);
        takeAudioIntent.setType("audio/*");
        if (takeAudioIntent.resolveActivity(getPackageManager()) != null) {

            File audioFile = null;
            try {
                audioFile = createAudioFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (audioFile != null) {

                Uri audioURI = FileProvider.getUriForFile(this,"com.br.com.zambaldi.soundsforpets.package.files",audioFile);
                takeAudioIntent.putExtra(MediaStore.EXTRA_OUTPUT, audioURI);

                startActivityForResult(takeAudioIntent, RQS_RECORDING);
            }

        }

    }

    public void audioRecord() {

        Intent takeAudioIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (takeAudioIntent.resolveActivity(getPackageManager()) != null) {

            File audioFile = null;
            try {
                audioFile = createAudioFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (audioFile != null) {

                Uri audioURI = FileProvider.getUriForFile(this,"com.br.com.zambaldi.soundsforpets.package.files",audioFile);
                takeAudioIntent.putExtra(MediaStore.EXTRA_OUTPUT, audioURI);

                startActivityForResult(takeAudioIntent, RQS_RECORDING);
            }



        }




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){

            if (requestCode == RQS_RECORDING) {



                try{

                    InputStream stream = getContentResolver().openInputStream(data.getData());
                    String FilePath = data.getData().getPath();
                    File original = new File(FilePath);

                    File newfile = new File(path_audio);
                    FileOutputStream out = new FileOutputStream(newfile);

                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = stream.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }

                    out.close();
                    stream.close();


                } catch (Exception e){

                }







                txtAudioSelected.setText(path_audio);

            }

        }
    }


    private File createAudioFile() throws IOException {
        // Create an audio file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioFileName = "_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File audio = File.createTempFile(
                audioFileName,  /* prefix */
                ".mp3",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        path_audio = audio.getPath();
        return audio;
    }

public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
        }


    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}
