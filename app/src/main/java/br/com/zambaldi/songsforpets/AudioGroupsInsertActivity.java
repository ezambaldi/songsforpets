package br.com.zambaldi.songsforpets;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AudioGroupsInsertActivity extends AppCompatActivity  {

    private Button btnConfigAudioGroupsInsert;
    private Button btnAudioListAudioGroupsInsert;
    private Button btnSaveAudioGroupsInsert;
    private Button btnCancelAudioGroupsInsert;

    FragmentManager frag = getSupportFragmentManager();
    AudioGroupsInsertFragment1 frag1 = new AudioGroupsInsertFragment1();
    AudioGroupsInsertFragment2 frag2 = new AudioGroupsInsertFragment2();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_groups_insert);

        btnConfigAudioGroupsInsert = (Button) findViewById(R.id.btnConfigAudioGroups);
        btnAudioListAudioGroupsInsert = (Button) findViewById(R.id.btnListAudioGroups);
        btnSaveAudioGroupsInsert = (Button) findViewById(R.id.btnSaveAudioGroups);
        btnCancelAudioGroupsInsert = (Button) findViewById(R.id.btnCancelAudioGroups);

        btnCancelAudioGroupsInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences dados= PreferenceManager.getDefaultSharedPreferences(AudioGroupsInsertActivity.this);
                SharedPreferences.Editor meuEditor = dados.edit();
                meuEditor.putInt("CHAMADA2",0);// 0 = dados em branco
                meuEditor.putInt("_IDD",0);
                meuEditor.apply();
                finish();

            }
        });



        //FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft1 = frag.beginTransaction();
        ft1.add(R.id.frame_fragment,frag1,"FRAG1");
        ft1.commit();

        btnConfigAudioGroupsInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

/*
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = fm.findFragmentById(R.id.frame_fragment);
                FragmentTransaction ft2 = fm.beginTransaction();
                ft2.remove(fragment);
                ft2.commit();
*/

                FragmentTransaction ft1 = frag.beginTransaction();
                ft1.replace(R.id.frame_fragment, frag1,"FRAG1");
                ft1.commit();


            }
        });

        btnAudioListAudioGroupsInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //SharedPreferences dados= PreferenceManager.getDefaultSharedPreferences(AudioGroupsInsertActivity.this);
                //SharedPreferences.Editor meuEditor = dados.edit();
                //meuEditor.putInt("CHAMADA2",0);// 0 = dados em branco
                //meuEditor.putInt("CHAMADA",0);// 0 = INSERT  1 = UPDATE
                //meuEditor.putInt("_IDD",0);//
                //meuEditor.apply();

                FragmentTransaction ft1 = frag.beginTransaction();
                ft1.replace(R.id.frame_fragment, frag2,"FRAG2");
                ft1.commit();

            }
        });

        btnSaveAudioGroupsInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(AudioGroupsInsertActivity.this);
                dlg
                        .setMessage("Save?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //chama gravação em banco do fragment1
                                AudioGroupsActivityInterface _inter = frag1;
                                int codigo = _inter.recordDataDetails();

                                //chama gravação em banco do fragment2
                                _inter = frag2;
                                _inter.recordDataListAudio(codigo);

                                //chama activity principal
                                Intent intent  = new Intent(AudioGroupsInsertActivity.this, AudioGroupsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                dlg.show();


            }
        });






/*

// Substitui um Fragment
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_content, new MainFragment());
        ft.commit();

// Remove um Fragment
        Fragment fragment = fm.findFragmentById(R.id.fragment_content);
        FragmentTransaction ft2 = fm.beginTransaction();
        ft2.remove(fragment);
        ft2.commit();

*/

    }





}
