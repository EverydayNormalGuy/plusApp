package com.plusapp.pocketbiceps.app;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;

/**
 * Diese Activity wird gestartet nachdem das Foto aufgenommen wurde.
 * Hier wird zuerst das aufgenommene Bild in eine Imageview gesetzt um dem Benutzer das Bild
 * anzuzeigen. Außerdem erlaubt die Act. dem Benutzer
 * eine Ueberschrift und eine Beschreibung hinzuzufuegen, die dann in der Datenbank
 * gespeichert wird
 */
public class AddActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    protected static final String IMAGE_NAME_PREFIX = "Moments_";

    EditText etTitle;
    EditText etDescription;
    ImageView imageViewAdd;
    String dbTitle;
    String dbDescription;
    String dbPath;
    int dbCounter = 0;
    long dbvCurrTime;
    MemoryAdapter memAdapter;
    Button btnGetLoc;
    GoogleApiClient googleApiClient;
    Toolbar toolbarAdd;
    MarkerDataSource data;
    private LocationRequest mLocationRequest;
    String dbLati;
    String dbLongi;
    boolean isDarkTheme;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;
    private String galleryPathName;
    View.OnClickListener sbOnClickListener; // Snackbar OnClickListener




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
        setContentView(R.layout.activity_add);


        data = new MarkerDataSource(this);

        toolbarAdd = (Toolbar) findViewById(R.id.toolbar2); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbarAdd);                   // Setting toolbar as the ActionBar with setSupportActionBar() call

        // Holt sich die currTime Variable die aus der MainActivity weitergegeben wurde
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Das Key Argument "currTime" muss mit dem Key aus der MainAct. uebereinstimmen
            this.dbvCurrTime = extras.getLong("currTime");
            if(extras.getString("pathName")!=null){
                this.galleryPathName = extras.getString("pathName");
            }
        }

        SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
        // Formatiert die currTime Variable von Millisekunden zu dem eindeutigen Index
        String imageDate = formatterForImageSearch.format(new Date(dbvCurrTime));


        File f = new File(MainActivity.IMAGE_PATH_URI + IMAGE_NAME_PREFIX + imageDate + ".jpg");
        dbPath = MainActivity.IMAGE_PATH_URI + IMAGE_NAME_PREFIX + imageDate + ".jpg";
        if (this.galleryPathName != null){
             f = new File(galleryPathName);
            this.dbPath = galleryPathName;
            String asd = dbPath;
        }

        memAdapter = new MemoryAdapter();


        imageViewAdd = (ImageView) findViewById(R.id.ivAddImage);
        etTitle = (EditText) findViewById(R.id.editTitle);
        etDescription = (EditText) findViewById(R.id.editDescription);
        btnGetLoc = (Button) findViewById(R.id.btnGetLocation);


        Picasso.with(getBaseContext()).load(f).resize(1080,1350).centerCrop().into(imageViewAdd);


        // Setzt das Bild in die Imageview
        //imageViewAdd.setImageBitmap(bmp);

        btnGetLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_ACCESS_COARSE_LOCATION);
                    // Snackbar wird benötigt dass das Android System genug Zeit hat nach der Permissionsabfrage die aktuelle Position zu bekommen.
                    // Der sbOnClickListener triggert onLocationChanged an, so dass bei betaetigen von Okay die aktuelle Position nochmals abgefragt wird.
                    Snackbar.make(findViewById(android.R.id.content),"Der Standort wird nur beim Speichern abgefragt",Snackbar.LENGTH_INDEFINITE).setAction("Okay", sbOnClickListener).show();

                }
                if (ContextCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                }

            }
        });

        sbOnClickListener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                buildGoogleApiClient();
            }
        };

    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getBaseContext()) //villeicht ohne basecontxt
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_add) {

            saveAddings();
            return true;
        }
        if (id == R.id.delete_add) {
            finish();
            Toast.makeText(getApplicationContext(), "Abgebrochen..", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);

    }

    public void saveAddings() {
//        if (TextUtils.isEmpty(etTitle.getText().toString().trim())) {
//            etTitle.setText("-");
//            return;
//        }
//
//        if (TextUtils.isEmpty(etDescription.getText().toString().trim())) {
//            etTitle.setText("-");
//            return;
//        }

        dbTitle = etTitle.getText().toString();
        dbDescription = etDescription.getText().toString();
        dbCounter = 1;
        data.open();
        // Setzt einen Eintrag mit den eingegeben Daten in die Datenbank
        if (dbLongi==null){
            data.addMarker(new MyMarkerObj(dbTitle, dbDescription, "position", dbvCurrTime, dbCounter, dbPath));
        }
        else{
            data.addMarker(new MyMarkerObj(dbTitle, dbDescription, dbLongi+" "+dbLati, dbvCurrTime, dbCounter, dbPath));
        }
        data.close();

        Intent intent = new Intent(AddActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        }


    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        dbLati = String.valueOf(location.getLatitude());
        dbLongi = String.valueOf(location.getLongitude());

        Toast.makeText(this, "bl" + location.getLatitude() + "  " + location.getLongitude(), Toast.LENGTH_LONG).show();
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }
}
