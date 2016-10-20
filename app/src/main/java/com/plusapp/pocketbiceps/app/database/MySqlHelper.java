package com.plusapp.pocketbiceps.app.database;

/**
 * Created by Metin on 24.03.2015.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySqlHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "locations";
    public static final String ID_COL = "loc_id";
    public static final String TITLE = "loc_title";
    public static final String SNIPPET = "loc_snippet";
    public static final String POSITION = "loc_position";
    public static final String TIME_STAMP = "loc_time";
    public static final String COUNTER ="loc_counter";


    private static final int D_VERSION = 1;
    private static final String DB_NAME = "markerlocations.db";
    private static final String DB_CREATE = "create table "+ TABLE_NAME + "("
            + ID_COL + " integer primary key autoincrement, "
            + TITLE + " text, "
            + SNIPPET + " text, "
            + TIME_STAMP + " integer, "
            + COUNTER + " integer, "
            + POSITION + " text);"
            ;

    public MySqlHelper(Context context){
        super(context, DB_NAME, null, D_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(DB_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }



}
