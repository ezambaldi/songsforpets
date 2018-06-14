package br.com.zambaldi.songsforpets;

/**
 * Created by eduardo on 19/08/17.
 */

public class ScriptSQL {

    public static String getCreateBaseSoundsForPets_audios(){

        //faz a junção de um texto grande
        StringBuilder sqlBuilder1 = new StringBuilder();

        sqlBuilder1.append("CREATE TABLE IF NOT EXISTS AUDIOS ( ");
        sqlBuilder1.append("_ID_AUDIOS INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sqlBuilder1.append("NAME_AUDIO VARCHAR(100),");
        sqlBuilder1.append("PATH_AUDIO VARCHAR(200)); ");

        return sqlBuilder1.toString();
    }

    public static String getCreateBaseSoundsForPets_groups(){

        //faz a junção de um texto grande
        StringBuilder sqlBuilder2 = new StringBuilder();

        sqlBuilder2.append("CREATE TABLE IF NOT EXISTS GROUPS ( ");
        sqlBuilder2.append("_ID_GROUPS INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sqlBuilder2.append("NAME_GROUP VARCHAR(100),");
        sqlBuilder2.append("LAST_ITEM INTEGER,");
        sqlBuilder2.append("TYPE_EXECUTE INTEGER,");
        sqlBuilder2.append("TIME_START TIME,");
        sqlBuilder2.append("TIME_FINISH TIME,");
        sqlBuilder2.append("INTERVAL_MINUTES INTEGER,");
        sqlBuilder2.append("DATE_TIME_START DATETIME,");
        sqlBuilder2.append("DATE_TIME_FINISH DATETIME,");
        sqlBuilder2.append("TIME_START_INTERVAL TIME); ");

        return sqlBuilder2.toString();
    }

    public static String getCreateBaseSoundsForPets_groups_items(){

        //faz a junção de um texto grande
        StringBuilder sqlBuilder3 = new StringBuilder();

        sqlBuilder3.append("CREATE TABLE IF NOT EXISTS GROUPS_ITEMS ( ");
        sqlBuilder3.append("_ID_GROUPS_ITEMS INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sqlBuilder3.append("ORDER_EXECUTE INTEGER,");
        sqlBuilder3.append("_FK_ID_AUDIOS INTEGER,");
        sqlBuilder3.append("_FK_ID_GROUPS INTEGER,");
        sqlBuilder3.append("TOTAL_EXECUTE INTEGER); ");

        return sqlBuilder3.toString();
    }

    public static String getCreateBaseURLBanner(){

        //faz a junção de um texto grande
        StringBuilder sqlBuilder4 = new StringBuilder();

        sqlBuilder4.append("CREATE TABLE IF NOT EXISTS BANNER ( ");
        sqlBuilder4.append("_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sqlBuilder4.append("URLBANNER VARCHAR(200),");
        sqlBuilder4.append("CLICKS INTEGER); ");

        return sqlBuilder4.toString();
    }




}

