package com.plusapp.pocketbiceps.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class GMapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private GoogleMap gMap;
    Context context = this;
    MarkerDataSource data;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    //
    double latlng;
    String latlng2;
    String longitudefordb;
    String latitudefordb;

    // protected LocationManager locationManager3;
    LocationListener locationListener;


    TextView txtLat;
    String lat;
    String provider;
    protected String latitude3, longitude3;
    protected boolean gps_enabled, network_enabled;

    double latitude;
    double longitude;
    public LocationManager lm;

    public Location mCurrentLocation;


    private GoogleApiClient googleApiClient;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme_AppBarOverlay); damit wird der navigation drawer schwarz
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmaps);
        Toolbar toolbarGmaps = (Toolbar) findViewById(R.id.toolbarGmaps);
        setSupportActionBar(toolbarGmaps);




        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();



        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbarGmaps, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gmaps, menu);
        return true;
    }


    @Override
    public void onConnected(Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (gMap != null) {
                gMap.setMyLocationEnabled(true);
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                gMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            }


        }
        Toolbar toolbarGmaps = (Toolbar) findViewById(R.id.toolbarGmaps);
        setSupportActionBar(toolbarGmaps);
    }


    private void addM() {
        List<MyMarkerObj> m = data.getMyMarkers();
        for (int i = 0; i < m.size(); i++) {
            String[] slatlng = m.get(i).getPosition().split(" ");
            LatLng lat = new LatLng(Double.valueOf(slatlng[0]),
                    Double.valueOf(slatlng[1]));
            gMap.addMarker(new MarkerOptions()
                    .title(m.get(i).getTitle())
                    .snippet(m.get(i).getSnippet())
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(lat));


        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap=googleMap;



        Toolbar toolbarGmaps = (Toolbar) findViewById(R.id.toolbarGmaps);
        setSupportActionBar(toolbarGmaps);

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        String provider = lm.getBestProvider(new Criteria(), true);


        data = new MarkerDataSource(context);
        data.open();

/**
        addM();

        // Get latitude of the current location
        double latitude = mCurrentLocation.getLatitude();
        String latitudefordb = String.valueOf(latitude);
        this.latitudefordb = latitudefordb;
        // Get longitude of the current location
        double longitude = mCurrentLocation.getLongitude();
        String longitudefordb = String.valueOf(longitude);
        this.longitudefordb = longitudefordb;

        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Show the current location in Google Map
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
**/
        // Zoom in the Google Map
        gMap.addMarker(new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("here"));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gMap.setMyLocationEnabled(true);

    }


    public void poisToDb(){

//        data.addMarker(new MyMarkerObj("twitter", "twitter HQ",
//                "48.802302 9.802771"));
//        data.addMarker(new MyMarkerObj("twitter", "twitter HQ",
//                "45.77734 9.37783"));

//        data.addMarker(new MyMarkerObj("Metins Wohnung",
//                "Hier zocken wir meistens :P", "48.49937 9.19023"));
//        data.addMarker(new MyMarkerObj("Sportplatz",
//                "Hier gingen wir �fter mit Kimbo und Lena spazieren",
//                "48.49896 9.18496"));
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
         mCurrentLocation = location;


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
