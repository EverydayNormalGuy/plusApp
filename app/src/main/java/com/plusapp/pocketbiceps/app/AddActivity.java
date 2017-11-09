package com.plusapp.pocketbiceps.app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;

import net.steamcrafted.lineartimepicker.dialog.LinearDatePickerDialog;
import net.steamcrafted.lineartimepicker.dialog.LinearTimePickerDialog;

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
    Button btnEditTime;
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
    private boolean fromGallery;

    //Test
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private static final int PLACE_PICKER_REQUEST = 1;

    @TargetApi(Build.VERSION_CODES.N)
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
        setContentView(R.layout.activity_add);


        data = new MarkerDataSource(this);

        toolbarAdd = (Toolbar) findViewById(R.id.toolbar2); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbarAdd);                   // Setting toolbar as the ActionBar with setSupportActionBar() call

        for (int i = 0; i < toolbarAdd.getChildCount(); i++) {
            View view = toolbarAdd.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(getAssets(), "fonts/Antonio-Light.ttf");
                if (tv.getText().equals(toolbarAdd.getTitle())) {
                    tv.setTypeface(titleFont);
//                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                    break;
                }
            }
        }

        // Holt sich die currTime Variable die aus der MainActivity weitergegeben wurde
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Das Key Argument "currTime" muss mit dem Key aus der MainAct. uebereinstimmen
            this.dbvCurrTime = extras.getLong("currTime");
            if (extras.getString("pathName") != null) {
                this.galleryPathName = extras.getString("pathName");
            }
            if (extras.getString("fromGallery") != null) {

                if (extras.getString("fromGallery").equals("true")) {
                    this.fromGallery = true;
                }
            }

        }

        SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
        // Formatiert die currTime Variable von Millisekunden zu dem eindeutigen Index
        String imageDate = formatterForImageSearch.format(new Date(dbvCurrTime));


        File f = new File(MainActivity.IMAGE_PATH_URI + IMAGE_NAME_PREFIX + imageDate + ".jpg");
        dbPath = MainActivity.IMAGE_PATH_URI + IMAGE_NAME_PREFIX + imageDate + ".jpg";
        if (this.galleryPathName != null) {
            f = new File(galleryPathName);
            this.dbPath = galleryPathName;
        }

        memAdapter = new MemoryAdapter();

        imageViewAdd = (ImageView) findViewById(R.id.ivAddImage);
        etTitle = (EditText) findViewById(R.id.editTitle);
        etDescription = (EditText) findViewById(R.id.editDescription);
        btnGetLoc = (Button) findViewById(R.id.btnGetLocation);
        btnEditTime = (Button) findViewById(R.id.btnEditTime);
        Glide.with(this)
                .load(f)
                .error(R.drawable.cast_album_art_placeholder)
                .override(1080, 1350)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageViewAdd);

        com.github.amlcurran.showcaseview.targets.Target targetGetLoc = new ViewTarget(R.id.btnGetLocation, this);

        ShowcaseView sv;
        sv = new ShowcaseView.Builder(this)
                .setTarget(targetGetLoc)
                .setContentTitle(getString(R.string.save_loc))
                .setContentText(getString(R.string.save_loc_tut))
                .singleShot(4311)
                .setStyle(R.style.CustomShowcaseTheme2)
                .build();

        sv.setButtonText(getString(R.string.got_it));

        if (fromGallery) {
            btnGetLoc.setText(R.string.add_location);
        }


        btnEditTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LinearDatePickerDialog dialog = LinearDatePickerDialog.Builder.with(AddActivity.this)
                        .setButtonCallback(new LinearDatePickerDialog.ButtonCallback() {
                            @Override
                            public void onPositive(DialogInterface dialog, int year, int month, int day) {
                                //TODO: Datum in Miliseconds speichern!
                            }

                            @Override
                            public void onNegative(DialogInterface dialog) {

                            }
                        })
                        .setDialogBackgroundColor(R.color.colorCardViewBlue)
                        .setPickerBackgroundColor(R.color.color_white)
                        .setLineColor(R.color.colorCardViewBlue)
                        .setTextBackgroundColor(R.color.color_grey)
                        .setButtonColor(R.color.color_white)
                        .setYear(2017)
                        .setShowTutorial(false)
                        .build();
                dialog.show();

            }
        });


        btnGetLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!fromGallery) {
                    if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_ACCESS_COARSE_LOCATION);
                        // Snackbar wird benötigt dass das Android System genug Zeit hat nach der Permissionsabfrage die aktuelle Position zu bekommen.
                        // Der sbOnClickListener triggert onLocationChanged an, so dass bei betaetigen von Okay die aktuelle Position nochmals abgefragt wird.
                        Snackbar.make(findViewById(android.R.id.content), R.string.snachbar_loc, Snackbar.LENGTH_INDEFINITE).setAction(R.string.got_it, sbOnClickListener).show();

                    }
                    if (ContextCompat.checkSelfPermission(getBaseContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        buildGoogleApiClient();
                    }
                } else if (fromGallery) {

                    try {
                        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
//                    intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                        Intent intent = intentBuilder.build(AddActivity.this);
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);

                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        sbOnClickListener = new View.OnClickListener() {

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
            Toast.makeText(getApplicationContext(), R.string.canceled, Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String latlng = place.getLatLng().toString();
            Log.d("bla", latlng);

            dbLati = TextUtils.substring(latlng, latlng.indexOf("(") + 1, latlng.indexOf(","));
            dbLongi = TextUtils.substring(latlng, latlng.indexOf(",") + 1, latlng.indexOf(')'));

//            String attributions = (String) place.getAttributions();
//            if (attributions == null) {
//                attributions = "";
//            }


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


    public void saveAddings() {

        dbTitle = etTitle.getText().toString();
        dbDescription = etDescription.getText().toString();
        dbCounter = 1;
        data.open();
        // Setzt einen Eintrag mit den eingegeben Daten in die Datenbank
        if (dbLongi == null) {
            data.addMarker(new MyMarkerObj(dbTitle, dbDescription, "position", dbvCurrTime, dbCounter, dbPath));
        } else {
            data.addMarker(new MyMarkerObj(dbTitle, dbDescription, dbLongi + " " + dbLati, dbvCurrTime, dbCounter, dbPath));
        }

        // Laesst den Media Scanner nach neuen Bildern scannen damit die in der Gallery angezeigt werden koennen
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(dbPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        data.close();

        Intent intent = new Intent(AddActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }
}
