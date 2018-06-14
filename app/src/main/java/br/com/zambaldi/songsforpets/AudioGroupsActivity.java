package br.com.zambaldi.songsforpets;

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
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioGroupsActivity extends AppCompatActivity {

    private DataBase dataBase;
    private SQLiteDatabase conn;
    private Button btnBackGroupList;
    private Button btnInsertGroupList;
    private ListView lstGroupList;
    private ArrayList<String> data_name = new ArrayList<String>();
    private ArrayList<String> data_date = new ArrayList<String>();
    private ArrayList<String> data_id = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_groups);


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


        btnBackGroupList = (Button) findViewById(R.id.btnBack_GroupList);
        btnInsertGroupList = (Button) findViewById(R.id.btnInsert_GroupList);
        lstGroupList = (ListView) findViewById(R.id.lstView_GroupList);

        btnBackGroupList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnInsertGroupList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences dados= PreferenceManager.getDefaultSharedPreferences(AudioGroupsActivity.this);
                SharedPreferences.Editor meuEditor = dados.edit();
                meuEditor.putInt("CHAMADA",0);// 0 = INSERT  1 = UPDATE
                meuEditor.putInt("CHAMADA2",0);// 0 = campos vazios  1 = saiu do foco
                meuEditor.putInt("CHAMADA3",0);// 0 = campos vazios  1 = saiu do foco
                meuEditor.putInt("ID_GROUP",0);// 0 = campos vazios  1 = saiu do foco
                meuEditor.putInt("_IDD",0);// 0 = INSERT  1 = UPDATE
                meuEditor.apply();

                Intent i = new Intent(AudioGroupsActivity.this, AudioGroupsInsertActivity.class);
                startActivity(i);

            }
        });


    }

    private class MyListAdapter extends ArrayAdapter<String> {

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
                viewHolder.date = (TextView) convertView.findViewById(R.id.txtDate_componente);
                viewHolder.name = (TextView) convertView.findViewById(R.id.txtName_componente);
                viewHolder.buttonEdit = (ImageButton) convertView.findViewById(R.id.btnEdit_componente);
                viewHolder.buttonRemove = (ImageButton) convertView.findViewById(R.id.btnRemove_componente);

                viewHolder.name.setText(data_name.get(position));
                viewHolder.date.setText(data_date.get(position));


                viewHolder.buttonEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String nId = data_id.get(position);
                        SharedPreferences dados= PreferenceManager.getDefaultSharedPreferences(AudioGroupsActivity.this);
                        SharedPreferences.Editor meuEditor = dados.edit();
                        meuEditor.putInt("CHAMADA",1);// 0 = INSERT  1 = UPDATE
                        meuEditor.putInt("CHAMADA2",0);//
                        meuEditor.putInt("CHAMADA3",0);// 0 = campos vazios  1 = saiu do foco
                        meuEditor.putInt("_IDD", Integer.parseInt(nId));// utilizado pelo fragment1
                        meuEditor.putInt("ID_GROUP", Integer.parseInt(nId));// utilizado pelo fragment2
                        meuEditor.apply();

                        Intent i = new Intent(AudioGroupsActivity.this, AudioGroupsInsertActivity.class);
                        startActivity(i);

                    }
                });


                viewHolder.buttonRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Toast.makeText(getContext(), "Botão remover foi clicado " + position, Toast.LENGTH_SHORT).show();

                        android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(AudioGroupsActivity.this);
                        dlg
                                .setMessage("Remove?")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        String nId = data_id.get(position);
                                        conn.execSQL("delete from groups where _ID_GROUPS = '"+ nId +"'");
                                        conn.execSQL("delete from groups_items where _ID_GROUPS_ITEMS = '"+ nId +"'");

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
                mainViewHolder.date.setText(data_date.get(position));
            }
            return convertView;
        }
    }

    public class ViewHolder{
        TextView date;
        ImageButton buttonRemove;
        ImageButton buttonEdit;
        TextView name;


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);


        carregaDados();
        lstGroupList.setAdapter(new MyListAdapter(this, R.layout.audio_groups_listview_components,data_name));


    }

    public  void carregaDados(){

        String mSql = "select _ID_GROUPS, DATE_TIME_START, NAME_GROUP from groups order by DATE_TIME_START";
        Cursor cursor = conn.rawQuery(mSql, null);

        data_date.clear();
        data_name.clear();
        data_id.clear();
        if (cursor.getCount() > 0) {

            cursor.moveToFirst();

            do {

                String _id = cursor.getString(0);
                String date = cursor.getString(1);
                String name = cursor.getString(2);
                data_name.add(name);
                data_id.add(_id);
                data_date.add(date);

            } while (cursor.moveToNext()) ;

        }

    }


}
