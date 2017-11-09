package com.plusapp.pocketbiceps.app;

import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;

import java.io.File;
import java.util.Date;
import java.util.List;

public class ActivityImageFromMarker extends AppCompatActivity {

    MarkerDataSource data;
    MyMarkerObj mmo;
    List<MyMarkerObj> m;
    String locFromMarker;
    PhotoView ivMarkerImage;
    int index;
    File f;
    boolean isSetToDarkTheme;
    boolean isDarkTheme;
    boolean isHighResolution;
    boolean isSetToHighResolution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String theme_key = getString(R.string.preference_key_darktheme);
        isSetToDarkTheme = sPrefs.getBoolean(theme_key, false);

        String resolution_key = getString(R.string.preference_key_resolution);
        isSetToHighResolution = sPrefs.getBoolean(resolution_key, false);

        if (isSetToDarkTheme == true) {
            setTheme(R.style.DarkTheme);
            isDarkTheme = true;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_from_marker);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Das Key Argument "currTime" muss mit dem Key aus der MainAct. uebereinstimmen
            this.locFromMarker = extras.getString("location");

            data = new MarkerDataSource(this);
            data.open();
            m = data.getMyMarkers(MainActivity.sortOrder);

            ivMarkerImage = (PhotoView) findViewById(R.id.ivImageMarker);

            for (int i = 0; i < m.size(); i++){

                mmo = m.get(i);

                String compareLoc;
                String[] slatlng = m.get(i).getPosition().split(" ");

                //Stelle 1 ist Lat und Stelle 0 Long
                compareLoc = "lat/lng: ("+slatlng[1] +","+slatlng[0]+")";

                if (compareLoc.equals(locFromMarker)){
                    f = new File(mmo.getPath());
                    break;
                }
            }

            if (isSetToHighResolution == true){
                isHighResolution = true;
                Glide.with(this)
                        .load(f)
                        .error(R.drawable.cast_album_art_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(ivMarkerImage);
            } else {
                Glide.with(this)
                        .load(f)
                        .error(R.drawable.cast_album_art_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .override(612,612)
                        .into(ivMarkerImage);
            }
        }
    }
}
