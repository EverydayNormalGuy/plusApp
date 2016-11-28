package com.plusapp.pocketbiceps.app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    public MemoryAdapter ca;


    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Fuer die Permissions
        isStoragePermissionGranted();

        //Oeffnet die Datenbank
        data = new MarkerDataSource(this);
        data.open();

//        data.addMarker(new MyMarkerObj("Test", "Test2", "48.49766 9.19881", 1234234));

        // Erstellt die RecylerView
        RecyclerView recList = (RecyclerView) findViewById(R.id.lvMemories);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        // An den MemoryAdapter wird Liste an den Konstruktor weitergegeben
        ca = new MemoryAdapter(createList2(),this);
        recList.setAdapter(ca);


        // Der FAB startet die Kamera
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        // NavDrawer Header manipulieren
        List<MyMarkerObj> navHeaderGetImage = createList2();
        // Falls mind. ein Moment Eintrag existiert, wird das Foto dass als letztes gemacht wurde
        // in den NavHeader eingefügt
        if(navHeaderGetImage.size()!=0) {
            MyMarkerObj mmo = navHeaderGetImage.get(0);

            // Das Datum und die Zeit dienen als Index um die Bilder zu finden z.B. Moments_10-12-2016-18-24-10
            SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
            String imageDate = formatterForImageSearch.format(new Date(mmo.getTimestamp()));

            // Das Bild wird in die Variable f initialisiert
            File f = new File("sdcard/special_moments/" + IMAGE_NAME_PREFIX + imageDate + ".jpg");

            memAdapter = new MemoryAdapter();
            // Erzeugt ein Bitmap aus der .jpg um die Speichergroeße Bilder zu reduzieren
            Bitmap bmp = memAdapter.decodeFile(f);


            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View headNavView = navigationView.getHeaderView(0);
//            TextView nav_user = (TextView) headNavView.findViewById(R.id.tvNavHeaderTitle);
//            nav_user.setText("test1231231");

            ImageView nav_image_head = (ImageView) headNavView.findViewById(R.id.ivNavHead);
            // Setzt das Bild in den NavHeader
            nav_image_head.setImageBitmap(bmp);
            navigationView.setNavigationItemSelectedListener(this);

        }

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_main, new MainFragment()).commit();

    }

    // Die Permissions fuer das Schreiben des External Storage werden hier abgefragt
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }



    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
        String dateString = formatter.format(new Date(currTime));

        //Speichert das gemachte Bild im path
        String path = "sdcard/special_moments/"+IMAGE_NAME_PREFIX+dateString+".jpg";
        Drawable.createFromPath(path);
        Intent intent =new Intent(MainActivity.this,AddActivity.class);
        // Die currTime Variable wird in die AddActivity weitergegeben, da sie dort als Index benoetigt wird
        intent.putExtra("currTime",currTime);
        startActivity(intent);




    }

    /**
     * Diese Methode erstellt ggfs. einen Ordner auf dem Handy und speichert das Bild als .jpg Format
     * getFile() gibt ein .jpg File zurueck
     */
    @TargetApi(Build.VERSION_CODES.N)
    private File getFile() {

        this.currTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
        String dateString = formatter.format(new Date(currTime));


        File folder = new File("sdcard/special_moments");


        if (!folder.exists()) {
            //Make Directory
            folder.mkdir();
        }

        File image_file = new File(folder, IMAGE_NAME_PREFIX+dateString+".jpg");

        return image_file;
    }

    /**
     * Erstellt eine Liste aus den Markern (Moments) in der Datenbank
     * @return Marker Liste aus der DB
     */
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

    // Interaktion mit den Menuepunkten aus dem NavigationDrawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {


        } else if (id == R.id.nav_slideshow) {

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
