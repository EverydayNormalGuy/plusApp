package com.plusapp.pocketbiceps.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
import com.plusapp.pocketbiceps.app.helperclasses.Blur;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.Date;
import java.util.List;

public class ActivityEdit extends AppCompatActivity {

    boolean isDarkTheme;
    Toolbar toolbarAdd;
    MarkerDataSource data;
    private static List<MyMarkerObj> m;
    private static int index;
    public MyMarkerObj mmo;
    EditText updateTitle;
    EditText updateDescription;
    ImageView ivMomentUpdate;
    String imageDate;
    String dbTitle;
    String dbDescription;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String theme_key = getString(R.string.preference_key_darktheme);
        boolean isSetToDarkTheme = sPrefs.getBoolean(theme_key, false);

        if (isSetToDarkTheme == true) {
            setTheme(R.style.DarkTheme);
            isDarkTheme = true;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        toolbarAdd = (Toolbar) findViewById(R.id.toolbar2); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbarAdd);                   // Setting toolbar as the ActionBar with setSupportActionBar() call

        for(int i = 0; i < toolbarAdd.getChildCount(); i++){
            View view = toolbarAdd.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(getAssets(), "fonts/Antonio-Light.ttf");
                if(tv.getText().equals(toolbarAdd.getTitle())){
                    tv.setTypeface(titleFont);
//                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                    break;
                }
            }
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Das Key Argument "index" muss mit dem Key aus der MainAct. uebereinstimmen
            this.index = extras.getInt("index");


            data = new MarkerDataSource(this);
            data.open();
            m = data.getMyMarkers(MainActivity.sortOrder);
            m.get(index);


            ivMomentUpdate = (ImageView) findViewById(R.id.ivUpdateImage);
            updateTitle = (EditText) findViewById(R.id.updateTitle);
            updateDescription = (EditText) findViewById(R.id.updateDescription);

            mmo = m.get(index);

            SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
            imageDate = formatterForImageSearch.format(new Date(mmo.getTimestamp()));

            final File f = new File(mmo.getPath());

            MemoryAdapter mem = new MemoryAdapter();
            //Bitmap bmp = mem.decodeFile(f);

            Transformation blurTransformation = new Transformation() {
                @Override
                public Bitmap transform(Bitmap source) {
                    Bitmap blurred = Blur.fastblur(getBaseContext(), source, 5);
                    source.recycle();
                    return blurred;
                }

                @Override
                public String key() {
                    return "blur()";
                }
            };

            Glide.with(this)
                    .load(f)
                    .error(R.drawable.cast_album_art_placeholder)
                    .override(1080, 1350)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(ivMomentUpdate);


            updateTitle.setText(mmo.getTitle());
            updateDescription.setText(mmo.getSnippet());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_edit) {

            saveAddings();
            return true;
        }
        if (id == R.id.delete_edit) {
            finish();
            Toast.makeText(getApplicationContext(), R.string.canceled, Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveAddings() {

        dbTitle = updateTitle.getText().toString();
        dbDescription = updateDescription.getText().toString();

        mmo.setTitle(dbTitle);
        mmo.setSnippet(dbDescription);
        data.open();

        data.updateMarker(mmo);

        Intent intent = new Intent(ActivityEdit.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

