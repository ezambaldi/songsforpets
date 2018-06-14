package br.com.zambaldi.songsforpets;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eduardo on 01/09/17.
 */

public class AudioGroupsInsertFragment2 extends Fragment  implements AudioGroupsActivityInterface {
    private DataBase dataBase;
    private SQLiteDatabase conn;
    private ListView lstGroupListComponents1;
    private ListView lstGroupListComponents2;
    private MediaPlayer mPlay;
    private int mTocando = 0;
    private int nId_Group = 0;
    private int mCarrega = 0;
    private int nChamada2;
    private int nEntrouAqui = 0;

    private ArrayList<String> data_name = new ArrayList<String>();
    private ArrayList<String> data_id = new ArrayList<String>();
    private ArrayList<String> data_path = new ArrayList<String>();

    private ArrayList<String> mydata_name = new ArrayList<String>();
    private ArrayList<String> mydata_id = new ArrayList<String>();
    private ArrayList<String> mydata_path = new ArrayList<String>();
    private ArrayList<String> mydata_id_audio = new ArrayList<String>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.audio_groups_insert_fragment2, container, false);
        View view = inflater.inflate(R.layout.audio_groups_insert_fragment2, container, false);


        nEntrouAqui = 1;
        // Conecta com banco de dados ***************************************************************************************
        try {
            dataBase = new DataBase(this.getContext());
            conn = dataBase.getWritableDatabase();

        } catch (SQLException ex) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this.getContext());
            dlg.setMessage("Error Database Create: " + ex.getMessage());
            dlg.setNeutralButton("Ok", null);
            dlg.show();
        }
        // *******************************************************************************************************************

        lstGroupListComponents1 = (ListView) view.findViewById(R.id.lstAudioListComponents);
        lstGroupListComponents2 = (ListView) view.findViewById(R.id.lstMyListComponents);




        final class MyListAdapter2 extends ArrayAdapter<String> {

            private int layout;

            public MyListAdapter2(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
                super(context, resource, objects);

                layout = resource;

            }

            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

                AudioGroupsInsertFragment2.ViewHolder mainViewHolder = null;

                if(convertView == null){
                    final LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(layout,parent, false);
                    final AudioGroupsInsertFragment2.ViewHolder viewHolder = new AudioGroupsInsertFragment2.ViewHolder();
                    viewHolder.name = (TextView) convertView.findViewById(R.id.txtName_componente3);
                    viewHolder.buttonRemove = (ImageButton) convertView.findViewById(R.id.btnRemove_componente3);
                    viewHolder.buttonPlay = (ImageButton) convertView.findViewById(R.id.btnPlay_componente3);

                    viewHolder.name.setText(mydata_name.get(position));

                    viewHolder.buttonPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Toast.makeText(getContext(), "" + data_path.get(position), Toast.LENGTH_SHORT).show();
                            String path = mydata_path.get(position);
                            File file = new File(path);

                            if(file.exists()) {


                                if(mTocando == 1){
                                    mPlay.stop();

                                    lstGroupListComponents2.getChildAt(0);

                                    viewHolder.buttonPlay.setImageResource(android.R.drawable.ic_media_play);
                                    mTocando = 0;

                                } else {

                                    try{

                                        Uri uri = Uri.fromFile(file);
                                        mPlay = MediaPlayer.create(AudioGroupsInsertFragment2.this.getContext(),uri);
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

                            mydata_id_audio.remove(position);
                            mydata_id.remove(position);
                            mydata_path.remove(position);
                            mydata_name.remove(position);
                            lstGroupListComponents2.setAdapter(new MyListAdapter2(getContext(), R.layout.audio_groups_listview2b_components,mydata_name));


                        }
                    });

                    convertView.setTag(viewHolder);

                } else {

                    mainViewHolder = (AudioGroupsInsertFragment2.ViewHolder) convertView.getTag();
                    //mainViewHolder.name.setText(getItem(position));
                    mainViewHolder.name.setText(mydata_name.get(position));
                }
                return convertView;
            }
        }


        final class MyListAdapter1 extends ArrayAdapter<String> {

            private int layout;

            public MyListAdapter1(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
                super(context, resource, objects);

                layout = resource;

            }

            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {

                AudioGroupsInsertFragment2.ViewHolder mainViewHolder = null;

                if(convertView == null){
                    final LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(layout,parent, false);
                    final AudioGroupsInsertFragment2.ViewHolder viewHolder = new AudioGroupsInsertFragment2.ViewHolder();
                    viewHolder.name = (TextView) convertView.findViewById(R.id.txtName_componente2);
                    viewHolder.buttonAdd = (ImageButton) convertView.findViewById(R.id.btnAdd_componente2);
                    viewHolder.buttonPlay = (ImageButton) convertView.findViewById(R.id.btnPlay_componente2);

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

                                    lstGroupListComponents1.getChildAt(0);

                                    viewHolder.buttonPlay.setImageResource(android.R.drawable.ic_media_play);
                                    mTocando = 0;

                                } else {

                                    try{

                                        Uri uri = Uri.fromFile(file);
                                        mPlay = MediaPlayer.create(AudioGroupsInsertFragment2.this.getContext(),uri);
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


                    viewHolder.buttonAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mydata_id_audio.add(data_id.get(position));
                            mydata_name.add(data_name.get(position));
                            mydata_path.add(data_path.get(position));
                            mydata_id.add("0");
                            lstGroupListComponents2.setAdapter(new MyListAdapter2(getContext(), R.layout.audio_groups_listview2b_components,mydata_name));

                        }
                    });

                    convertView.setTag(viewHolder);

                } else {

                    mainViewHolder = (AudioGroupsInsertFragment2.ViewHolder) convertView.getTag();
                    //mainViewHolder.name.setText(getItem(position));
                    mainViewHolder.name.setText(data_name.get(position));
                }
                return convertView;
            }
        }





        carregaDados();
        lstGroupListComponents1.setAdapter(new MyListAdapter1(this.getContext(), R.layout.audio_groups_listview2a_components,data_name));
        lstGroupListComponents2.setAdapter(new MyListAdapter2(this.getContext(), R.layout.audio_groups_listview2b_components,mydata_name));



        return view;
    }


    @Override
    public int recordDataDetails() {
        int nReturn = 0;
        return nReturn;
    }

    @Override
    public void recordDataListAudio(int nIdGroup) {


        if(nEntrouAqui == 1) {

            nId_Group = nIdGroup;

            String nSql = "delete from groups_items where _fk_id_groups = "+ nId_Group;
            conn.execSQL(nSql);

            for(int i=0; i<mydata_name.size(); i++){

                nSql = "insert into groups_items (ORDER_EXECUTE,_FK_ID_AUDIOS,_FK_ID_GROUPS,TOTAL_EXECUTE) " +
                        "values("+i+","+mydata_id_audio.get(i)+","+nId_Group+",0)";

                conn.execSQL(nSql);

            }

        }


    }

    @Override
    public void nFinish(Activity activity) {

    }

    public class ViewHolder{
        ImageButton buttonAdd;
        ImageButton buttonRemove;
        ImageButton buttonPlay;
        TextView name;
    }

    public  void carregaDados(){

        if(mCarrega == 0){

            String mSql = "select _ID_AUDIOS, NAME_AUDIO, PATH_AUDIO from audios order by name_audio";
            Cursor cursor = conn.rawQuery(mSql, null);

            data_name.clear();
            data_id.clear();
            data_path.clear();
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();

                do {

                    String _id = cursor.getString(0);
                    String name = cursor.getString(1);
                    String path = cursor.getString(2);
                    data_id.add(_id);
                    data_name.add(name);
                    data_path.add(path);

                } while (cursor.moveToNext()) ;

            }

            SharedPreferences dados= PreferenceManager.getDefaultSharedPreferences(this.getContext());
            nId_Group = dados.getInt("ID_GROUP",0);


            String mSql2 = "select a._ID_GROUPS_ITEMS, b.name_audio, b.path_audio, a._FK_ID_AUDIOS from groups_items a, audios b where a._fk_id_groups = "+ nId_Group +" and b._id_audios = a._fk_id_audios order by order_execute";
            Cursor cursor2 = conn.rawQuery(mSql2, null);

            mydata_name.clear();
            mydata_id.clear();
            mydata_path.clear();
            mydata_id_audio.clear();
            if (cursor2.getCount() > 0) {

                cursor2.moveToFirst();

                do {

                    String _id = cursor2.getString(0);
                    String name = cursor2.getString(1);
                    String path = cursor2.getString(2);
                    String _id_audio = cursor2.getString(3);
                    mydata_id.add(_id);
                    mydata_name.add(name);
                    mydata_path.add(path);
                    mydata_id_audio.add(_id_audio);

                } while (cursor2.moveToNext()) ;

            }



        } else {

            // ver se entra algo aqui

        }


    }

    public void onResume(){
        super.onResume();

        SharedPreferences dados= PreferenceManager.getDefaultSharedPreferences(this.getContext());
        nChamada2 = dados.getInt("CHAMADA3",0); // controla se saiu do foco activity = 1

        if(nChamada2 == 1){

            nId_Group = dados.getInt("ID_GROUP",0);
            String stringListSounds = dados.getString("ARRAYSOUNDS","");

            String[] nString = stringListSounds.split(",");
            for(int i=0; i<nString.length; i++){


                String nId = nString[i].toString().replace("[","").replace("]","");
                if(nId != "") {

                    String mSql2 = "select name_audio, path_audio from audios where _ID_AUDIOS = " + nId;
                    Cursor cursor2 = conn.rawQuery(mSql2, null);

                    if(cursor2.getCount() >0){

                        cursor2.moveToFirst();
                        mydata_id_audio.add(nString[i].toString());
                        mydata_name.add(cursor2.getString(0));
                        mydata_path.add(cursor2.getString(1));
                        mydata_id.add("0");

                    }

                }


            }

        }


    }


    public void onPause(){
        super.onPause();

        SharedPreferences dados= PreferenceManager.getDefaultSharedPreferences(this.getContext());
        SharedPreferences.Editor meuEditor = dados.edit();
        String stringListSounds = mydata_id_audio.toString();
        meuEditor.putString("ARRAYSOUNDS",stringListSounds);
        meuEditor.putInt("ID_GROUP",nId_Group);
        meuEditor.putInt("CHAMADA3" +
                "",1);
        meuEditor.apply();

    }


}
