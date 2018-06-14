package br.com.zambaldi.songsforpets;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioListActivity extends AppCompatActivity {

    private DataBase dataBase;
    private SQLiteDatabase conn;

    private Button btnBack_audiolist;
    private Button btnInsert_audiolist;
    private ListView lstView_audiolist;
    private MediaPlayer mPlay;
    private int mTocando = 0;

    private ArrayList<String> data_name = new ArrayList<String>();
    private ArrayList<String> data_path = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        // Conecta com banco de dados ***************************************************************************************
        try {
            dataBase = new DataBase((this));
            conn = dataBase.getWritableDatabase();

        } catch (SQLException ex) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setMessage("Error Database Create: " + ex.getMessage());
            dlg.setNeutralButton("Ok", null);
            dlg.show();
        }
        // *******************************************************************************************************************


        btnBack_audiolist = (Button) findViewById(R.id.btnBack_audiolist);
        btnInsert_audiolist = (Button) findViewById(R.id.btnInsert_audiolist);
        lstView_audiolist = (ListView) findViewById(R.id.lstView_audiolist);

        btnBack_audiolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        btnInsert_audiolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(AudioListActivity.this, AudioListInsertActivity.class);
                startActivity(i);

            }
        });



/*
        for(int i=0; i<5; i++){

            data.add("Item da lista número " + i);

        }
*/

//        carregaDados();
//        lstView_audiolist.setAdapter(new MyListAdapter(this, R.layout.audio_list_listview_components,data));

    }

    private class MyListAdapter extends ArrayAdapter<String>{

        private int layout;

        public MyListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
            super(context, resource, objects);

            layout = resource;

        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

            ViewHolder mainViewHolder = null;

            if(convertView == null){
                final LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout,parent, false);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.buttonPlay = (ImageButton) convertView.findViewById(R.id.btnPlay_componente);
                viewHolder.buttonRemove = (ImageButton) convertView.findViewById(R.id.btnRemove_componente);
                viewHolder.name = (TextView) convertView.findViewById(R.id.txtName_componente);

                viewHolder.name.setText(data_name.get(position));


                viewHolder.buttonPlay.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View view) {

                        //Toast.makeText(getContext(), "" + data_path.get(position), Toast.LENGTH_SHORT).show();
                        String path = data_path.get(position);
                        File file = new File(path);

                        if(file.exists()) {


                            if(mTocando == 1){
                                mPlay.stop();

                                lstView_audiolist.getChildAt(0);

                                viewHolder.buttonPlay.setImageResource(android.R.drawable.ic_media_play);
                                mTocando = 0;

                            } else {

                                try{

                                    Uri uri = Uri.fromFile(file);
                                    mPlay = MediaPlayer.create(AudioListActivity.this,uri);
                                    viewHolder.buttonPlay.setImageResource(android.R.drawable.ic_lock_silent_mode_off);

                                    mPlay.setVolume(50,50);
                                    mPlay.start();
                                    mTocando = 1;

                                } catch (Exception e){

                                }

                            }

                        }

                        mPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {

                                viewHolder.buttonPlay.setImageResource(android.R.drawable.ic_media_play);
                                mTocando = 0;

                            }
                        });

                    }
                });



                viewHolder.buttonRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Toast.makeText(getContext(), "Botão remover foi clicado " + position, Toast.LENGTH_SHORT).show();

                        android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(AudioListActivity.this);
                        dlg
                                .setMessage("Remove?")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        File file = new File(data_path.toString().replace("[","").replace("]",""));


                                        if(file.exists()){

                                            file.delete();

                                        }



                                        conn.execSQL("delete from audios where path_audio = '"+ data_path.get(position) +"'");

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                        dlg.show();

                    }
                });



                convertView.setTag(viewHolder);

            } else {

                mainViewHolder = (ViewHolder) convertView.getTag();
                //mainViewHolder.name.setText(getItem(position));
                mainViewHolder.name.setText(data_name.get(position));

            }
            return convertView;
        }
    }

    public class ViewHolder{
        ImageButton buttonPlay;
        ImageButton buttonRemove;
        TextView name;


    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);


        carregaDados();
        lstView_audiolist.setAdapter(new MyListAdapter(this, R.layout.audio_list_listview_components,data_name));


    }

    public  void carregaDados(){

        String mSql = "select name_audio, path_audio from audios order by name_audio";
        Cursor cursor = conn.rawQuery(mSql, null);

        data_name.clear();
        data_path.clear();
        if (cursor.getCount() > 0) {

            cursor.moveToFirst();

            do {

                String name = cursor.getString(0);
                String path = cursor.getString(1);
                data_name.add(name);
                data_path.add(path);

            } while (cursor.moveToNext()) ;

        }

    }


}
