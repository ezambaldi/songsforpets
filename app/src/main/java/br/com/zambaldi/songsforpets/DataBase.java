package br.com.zambaldi.songsforpets;

/**
 * Created by eduardo on 19/08/17.
 */

import android.content.Context;
import android.database.sqlite.*;

public class DataBase  extends SQLiteOpenHelper {

    public DataBase(Context context){

        super(context,"BASE_SONGS_FOR_PETS",null, 1);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(ScriptSQL.getCreateBaseSoundsForPets_audios());
        db.execSQL(ScriptSQL.getCreateBaseSoundsForPets_groups());
        db.execSQL(ScriptSQL.getCreateBaseSoundsForPets_groups_items());
        db.execSQL(ScriptSQL.getCreateBaseURLBanner());

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}