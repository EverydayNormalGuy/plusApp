package com.plusapp.pocketbiceps.app.database;

/**
 * Created by Metin on 24.03.2015.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MarkerDataSource {
    MySqlHelper dbhelper;
    SQLiteDatabase db;

    String[] cols= {MySqlHelper.TITLE,MySqlHelper.SNIPPET,MySqlHelper.POSITION,MySqlHelper.TIME_STAMP};

    public MarkerDataSource(Context c) {
        dbhelper = new MySqlHelper(c);
    }

    public void open() {

        db = dbhelper.getWritableDatabase();
    }




    public void close() {
        db.close();
    }
    public void addMarker(MyMarkerObj m){
        ContentValues v = new ContentValues();

        v.put(MySqlHelper.TITLE, m.getTitle());
        v.put(MySqlHelper.SNIPPET, m.getSnippet());
        v.put(MySqlHelper.POSITION, m.getPosition());
        v.put(MySqlHelper.TIME_STAMP, m.getTimestamp());

        db.insert(MySqlHelper.TABLE_NAME, null, v);

    }

    public List<MyMarkerObj> getMyMarkers(){
        List<MyMarkerObj> markers = new ArrayList<MyMarkerObj>();

        Cursor cursor = db.query(MySqlHelper.TABLE_NAME, cols, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            MyMarkerObj m = cursorToMarker(cursor);
            markers.add(m);
            cursor.moveToNext();
        }
        cursor.close();
        return markers;

    }

    public void deleteMarker(MyMarkerObj m) {
        db.delete(MySqlHelper.TABLE_NAME, MySqlHelper.POSITION + " = '" +m.getPosition()+ "'",null);
    }

    private MyMarkerObj cursorToMarker(Cursor cursor){
        MyMarkerObj m = new MyMarkerObj();
        m.setTitle(cursor.getString(0));
        m.setSnippet(cursor.getString(1));
        m.setPosition(cursor.getString(2));
        m.setTimestamp(cursor.getLong(3));
        return m;
    }
}
