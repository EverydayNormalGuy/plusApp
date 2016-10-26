package com.plusapp.pocketbiceps.app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
import com.plusapp.pocketbiceps.app.fragments.GmapsFragment;
import com.plusapp.pocketbiceps.app.fragments.DetailsFragment;
import com.plusapp.pocketbiceps.app.fragments.MainFragment;

import java.io.File;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GoogleApiClient googleApiClient;
    public MarkerDataSource data;
    Context context;
    static final int CAM_REQUEST = 1;
    protected static final String IMAGE_NAME_PREFIX = "Moments_";
    public long currTime=0;
    public MemoryAdapter memAdapter;


//    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static String READ_EXTERNAL_STORAGE;
//    private static String WRITE_EXTERNAL_STORAGE;




    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        data = new MarkerDataSource(this);
        data.open();

//        data.addMarker(new MyMarkerObj("Test", "Test2", "48.49766 9.19881", 1234234));


        RecyclerView recList = (RecyclerView) findViewById(R.id.lvMemories);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);


        MemoryAdapter ca = new MemoryAdapter(createList2(),this);
        recList.setAdapter(ca);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getFile();
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera_intent, CAM_REQUEST);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavDrawer Header manipulieren


        List<MyMarkerObj> navHeaderGetImage = createList2();
        if(navHeaderGetImage.size()!=0) {
            MyMarkerObj mmo = navHeaderGetImage.get(0);

            SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
            String imageDate = formatterForImageSearch.format(new Date(mmo.getTimestamp()));


            File f = new File("sdcard/special_moments/" + IMAGE_NAME_PREFIX + imageDate + ".jpg");

            memAdapter = new MemoryAdapter();
            Bitmap bmp = memAdapter.decodeFile(f);


            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View headNavView = navigationView.getHeaderView(0);
//            TextView nav_user = (TextView) headNavView.findViewById(R.id.tvNavHeaderTitle);
//            nav_user.setText("test1231231");
            ImageView nav_image_head = (ImageView) headNavView.findViewById(R.id.ivNavHead);
            nav_image_head.setImageBitmap(bmp);
            navigationView.setNavigationItemSelectedListener(this);

        }







        /**
         * Fügt ein Fragment zur MainActivity
         */
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_main, new MainFragment()).commit();

    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data); unnötig

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        }
//

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
        String dateString = formatter.format(new Date(currTime));



        //Speichert das gemacht Bild im path
        String path = "sdcard/special_moments/"+IMAGE_NAME_PREFIX+dateString+".jpg";
        Drawable.createFromPath(path);
        Intent intent =new Intent(MainActivity.this,AddActivity.class);
        intent.putExtra("currTime",currTime);
        startActivity(intent);




    }

    @TargetApi(Build.VERSION_CODES.N)
    private File getFile() {

        this.currTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
        String dateString = formatter.format(new Date(currTime));

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        }

        File folder = new File("sdcard/special_moments");


        if (!folder.exists()) {
            //Make Directory
            folder.mkdir();
        }

        File image_file = new File(folder, IMAGE_NAME_PREFIX+dateString+".jpg");

        return image_file;
    }

    private List<MyMarkerObj> createList2() {

        List<MyMarkerObj> m = data.getMyMarkers();
        return m;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        FragmentManager fm = getFragmentManager();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
//            fm.beginTransaction().replace(R.id.content_main, new DetailsFragment()).commit();
        } else if (id == R.id.nav_gallery) {


        } else if (id == R.id.nav_slideshow) {
//            Intent intent = new Intent(this,GMapsActivity.class);
//            startActivity(intent);
            fm.beginTransaction().replace(R.id.content_main, new GmapsFragment()).commit();

            Toast.makeText(getBaseContext(), "Map staretet", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
