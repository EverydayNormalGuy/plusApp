package com.plusapp.pocketbiceps.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;

import java.io.File;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    EditText etTitle;
    EditText etDescription;
    ImageView imageViewAdd;

    String dbTitle;
    String dbDescription;
    String dbLongitude;
    String dbLatitude;
    int dbCounter = 0;
    long dbvCurrTime;
    MemoryAdapter memAdapter;

    protected static final String IMAGE_NAME_PREFIX = "Moments_";


    Toolbar toolbarAdd;

    MarkerDataSource data;

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        data = new MarkerDataSource(this);

        toolbarAdd = (Toolbar) findViewById(R.id.toolbar2); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbarAdd);                   // Setting toolbar as the ActionBar with setSupportActionBar() call


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.dbvCurrTime = extras.getLong("currTime");
            //The key argument here must match that used in the other activity
        }


        SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
        String imageDate = formatterForImageSearch.format(new Date(dbvCurrTime));
        File f = new File("sdcard/special_moments/" + IMAGE_NAME_PREFIX + imageDate + ".jpg");

        memAdapter = new MemoryAdapter();
        Bitmap bmp = memAdapter.decodeFile(f);


        imageViewAdd = (ImageView) findViewById(R.id.ivAddImage);
        etTitle = (EditText) findViewById(R.id.editTitle);
        etDescription = (EditText) findViewById(R.id.editDescription);

        imageViewAdd.setImageBitmap(bmp);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_add) {
            saveAddings();
            return true;
        }
        if (id == R.id.delete_add){
            Toast.makeText(getApplicationContext(),"Gelöscht", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);



    }


    public void saveAddings(){
        if (TextUtils.isEmpty(etTitle.getText().toString().trim())) {
            etTitle.setError("Gib einen Titel ein");
            return;
        }

        if (TextUtils.isEmpty(etDescription.getText().toString().trim())) {
            etTitle.setError("Gib eine Beschreibung ein");
            return;
        }

        dbTitle = etTitle.getText().toString();
        dbDescription = etDescription.getText().toString();
        dbCounter = 1;
        data.open();
        data.addMarker(new MyMarkerObj(dbTitle,dbDescription,"position",dbvCurrTime,dbCounter));
        data.close();

        Intent intent = new Intent(AddActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
