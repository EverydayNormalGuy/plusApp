package com.plusapp.pocketbiceps.app.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.plusapp.pocketbiceps.app.ActivityImageFromMarker;
import com.plusapp.pocketbiceps.app.MainActivity;
import com.plusapp.pocketbiceps.app.MemoryAdapter;
import com.plusapp.pocketbiceps.app.R;
import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static com.google.android.gms.wearable.DataMap.TAG;


/**
 * Created by Metin on 19.10.2016.
 */

public class GmapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        com.google.android.gms.location.LocationListener {

    protected static final String IMAGE_NAME_PREFIX = "Moments_";
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;
    public Bitmap bmpTemp;
    private Marker mCurrLocationMarker;
    private double lati;
    private double longi;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private MarkerDataSource data;
    private View mCustomMarkerView;
    private ImageView mMarkerImageView;
    private LatLng latLng;
    private MemoryAdapter memAdapter;
    private LatLng positionMarkers;
    private Bitmap bmpDecoded;

    public static Bitmap rotateBitmap(Bitmap source, float angle) {


        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_gmaps, container, false);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.gmap);
        fragment.getMapAsync(this);

        //Inflate die custom marker XML und referenziert auf die darininhaltende Imageview
        mCustomMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.placeholder_image_marker);

        data = new MarkerDataSource(getActivity().getBaseContext());
    }

    //Wird fuer die LocationGoogleApi gebraucht
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext()) //villeicht ohne basecontxt
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String gMapViewType = sPrefs.getString("gmapviewtype", "2");

        /**
         * "1" Standard, "2" Satelit, "3" Hybrid siehe string-array gmaptypevalues
         */
        if (gMapViewType.equals("1")) {
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        } else if (gMapViewType.equals("2")) {
            gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        } else if (gMapViewType.equals("3")) {
            gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }

        if (ContextCompat.checkSelfPermission(getActivity().getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            gMap.setMyLocationEnabled(true);
        }


        MapsInitializer.initialize(getActivity());
        try {
            addCustomMarker();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //Erstellt ein Bitmap die in die CustomMarker view eingefuegt wird
    private Bitmap getMarkerBitmapFromView(View view, Bitmap bitmap) {

//        Matrix matrix = new Matrix();
//
//        switch (orientation) {
//            case 6:
//                matrix.postRotate(90);
//                break;
//            default:
//                break;
//        }


        mMarkerImageView.setImageBitmap(bitmap);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
//        Bitmap returnedBitmap = Bitmap.createBitmap(bitmap, 0,0 , view.getMeasuredWidth(), view.getMeasuredHeight(),
//                matrix,false );

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);


        return returnedBitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addCustomMarker() throws IOException {
        Log.d(TAG, "addCustomMarker()");
        if (gMap == null) {
            return;
        }


        data.open();

        List<MyMarkerObj> m = data.getMyMarkers(MainActivity.sortOrder);
        for (int i = 0; i < m.size(); i++) {
            if (m.get(i).getPosition().equals("position")) {
                //donothing
            } else {

                String[] slatlng = m.get(i).getPosition().split(" ");
                //Stelle 0 ist Lat und Stelle 1 Long
                positionMarkers = new LatLng(Double.valueOf(slatlng[1]),
                        Double.valueOf(slatlng[0]));

                SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
                String imageDate = formatterForImageSearch.format(new Date(m.get(i).getTimestamp()));

                File f = new File(m.get(i).getPath()); // TODO Nicht sicher ob das klappt hier

                //Bitmap Decoder hat sich hier praktischer ergeben
                memAdapter = new MemoryAdapter();

                ExifInterface exif = new ExifInterface(m.get(i).getPath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);





                Bitmap bmpDecoded = memAdapter.decodeFile(f);

                Bitmap rotatedBmp;

                // Wird benoetigt damit die Bilder in den Marker auf der Map richtig rum gedreht sind
                // Maincamera in Landscape aufgenommen
                if (orientation == 6) {
                     rotatedBmp = rotateBitmap(bmpDecoded, 90);
                    bmpDecoded = rotatedBmp;
                }
                // Frontcamera in Portrait aufgenommen
                if (orientation == 8){
                    rotatedBmp = rotateBitmap(bmpDecoded, 270);
                    bmpDecoded = rotatedBmp;
                }
                // Frontcamera in Landscape aufgenommen
                if (orientation == 3){
                    rotatedBmp = rotateBitmap(bmpDecoded, 180);
                    bmpDecoded = rotatedBmp;
                }


                gMap.addMarker(new MarkerOptions()
                        .title(m.get(i).getTitle())
                        // .snippet(m.get(i).getSnippet())
                        .position(positionMarkers)
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, bmpDecoded))));


                gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        String locForDB;
                        String loc;
                        loc = marker.getPosition().toString();



                        Intent intent = new Intent(getActivity().getApplicationContext(), ActivityImageFromMarker.class);
                        intent.putExtra("location", loc);
                        getActivity().getApplicationContext().startActivity(intent);

                        return false;
                    }
                });
            }

        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
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


        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        gMap.getUiSettings().setMapToolbarEnabled(false);
        //move map camera
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        //stop location updates
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
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

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    /**
     * Sorgt dafür dass die Toolbar versteckt bzw. wieder angezeigt wird
     */
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

    }
}
