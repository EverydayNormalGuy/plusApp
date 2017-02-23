package com.plusapp.pocketbiceps.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Bei einem Klick auf die Cardview wird die DetailsAct_wo_pager aufgerufen.
 * Hier wird das Bild die dazugehoerige Ueberschrift und der Beschreibung angezeigt
 */
public class DetailsAct_wo_pager extends AppCompatActivity {


    private long currTime;
    private static List<MyMarkerObj> m;
    private static int index;
    protected static final String IMAGE_NAME_PREFIX = "Moments_";
    public static MarkerDataSource data;
    public boolean isDarkTheme;
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String theme_key = getString(R.string.preference_key_darktheme);
        boolean isSetToDarkTheme = sPrefs.getBoolean(theme_key,false);

        if(isSetToDarkTheme==true){
            setTheme(R.style.DarkTheme);
            isDarkTheme=true;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_act_wo_pager);

        Toolbar toolbarDetails = (Toolbar) findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbarDetails);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Das Key Argument "currTime" muss mit dem Key aus der MainAct. uebereinstimmen
            this.index = extras.getInt("index");


            data = new MarkerDataSource(this);
            data.open();
            m = data.getMyMarkers(MainActivity.sortOrder);
            m.get(index);

        }

        TextView textView = (TextView) findViewById(R.id.tvDetailsActTitle);
        TextView textViewDescr = (TextView) findViewById(R.id.tvDetailsActDescription);
        ImageView imageView = (ImageView) findViewById(R.id.ivDetailsAct);

        MyMarkerObj mmo = m.get(index);

        SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
        String imageDate=formatterForImageSearch.format(new Date(mmo.getTimestamp()));

        File f = new File("sdcard/special_moments/"+IMAGE_NAME_PREFIX+imageDate+".jpg");

        MemoryAdapter mem = new MemoryAdapter();
        //Bitmap bmp = mem.decodeFile(f);

        String orientation="";
        try {
            ExifInterface exif = new ExifInterface("sdcard/special_moments/"+IMAGE_NAME_PREFIX+imageDate+".jpg");
              orientation= exif.getAttribute(ExifInterface.TAG_ORIENTATION); // 6 Vertikal, 1 Horizontal
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (orientation.equals("1")){
            Picasso.with(getBaseContext()).load(f).resize(800,566).centerCrop().into(imageView);
        }
        else if (orientation.equals("6")){
            Picasso.with(getBaseContext()).load(f).resize(1080,1350).centerCrop().into(imageView);
        }



        //imageView.setImageBitmap(bmp);
        textView.setText(mmo.getTitle());
        textViewDescr.setText(mmo.getSnippet());

        data.updateMarker(mmo);

        int temp = mmo.getCounter();

    }



    // Sorgt dafuer dass der Stack der Activities geloescht wird
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(DetailsAct_wo_pager.this, MainActivity.class);
        //Cleared den ganzen Activitystack
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
