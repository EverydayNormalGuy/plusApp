package com.plusapp.pocketbiceps.app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
import com.plusapp.pocketbiceps.app.fragments.GmapsFragment;
import com.plusapp.pocketbiceps.app.fragments.MainFragment;
import com.plusapp.pocketbiceps.app.fragments.SortDialogFragment;
import com.plusapp.pocketbiceps.app.helperclasses.ViewTargets;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.steamcrafted.lineartimepicker.dialog.LinearDatePickerDialog;

import java.io.File;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;

    public MarkerDataSource data;
    static final int CAM_REQUEST = 1;
    protected static final String IMAGE_NAME_PREFIX = "Moments_";
    public static final String IMAGE_PATH_URI = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Mapix/";
    private final static String PACKAGE_NAME = "com.plusapp.pocketbiceps.app";
    private final static String PLAYSTORE_LINK = "market://details?id=";
    public long currTime = 0;
    public MemoryAdapter memAdapter;
    public MemoryAdapter ca;

    public Bitmap bmp;
    public NavigationView navigationView;

    private int counterTut = 0;
    ShowcaseView sv;

    RecyclerView recList;
    CardView cardView;

    FloatingActionMenu fab_Menu;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;

    private List<FloatingActionMenu> fab_Submenus = new ArrayList<>();
    private Handler mUiHandler = new Handler();

    TextView momentsCounter;
    int momentsCount;

    boolean isDarkTheme;
    boolean isSetToDarkTheme;
    boolean isCoverPhoto;
    boolean isSettoCoverPhoto;

    View headNavView;
    ImageView nav_image_head;

    public Target bmpHeaderTarget;

    SharedPreferences sp;
    public static int sortOrder;

    private static int RESULT_LOAD_IMG = 2;
    String imgDecodableString;
    Toolbar toolbar;

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String theme_key = getString(R.string.preference_key_darktheme);
        isSetToDarkTheme = sPrefs.getBoolean(theme_key, false);

        if (isSetToDarkTheme == true) {
            setTheme(R.style.DarkTheme);
            isDarkTheme = true;
        }

        // Prueft ob das Coverfoto gesetzt werden soll und speichert es ggfs. in einer Bitmap
        String cover_key = getString(R.string.preference_key_coverphoto);
        isSettoCoverPhoto = sPrefs.getBoolean(cover_key, false);

        //Oeffnet die Datenbank
        data = new MarkerDataSource(this);
        data.open();  //        data.addMarker(new MyMarkerObj("Test", "Test2", "48.49766 9.19881", 1234234));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Aenderd den Textfont von der Toolbar
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(getAssets(), "fonts/Antonio-Light.ttf");
                if (tv.getText().equals(toolbar.getTitle())) {

                    // Zum zentrieren
//                    Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT);
//                    params.gravity = Gravity.CENTER_HORIZONTAL;
//                    tv.setLayoutParams(params);
                    // Zum aendern der Schriftart
                    tv.setTypeface(titleFont);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                    break;
                }
            }
        }

        sp = getSharedPreferences("prefs_sort", Activity.MODE_PRIVATE);
        sortOrder = sp.getInt("sort_mode", 0);

        //Permissions Abfragen
        isStoragePermissionGranted();
        grantLocationPermission();

        // Erstellt die RecylerView
        recList = (RecyclerView) findViewById(R.id.lvMemories);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        momentsCount = createList2().size();

        fab_Menu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        // An den MemoryAdapter wird Liste an den Konstruktor weitergegeben
        this.ca = new MemoryAdapter(createList2(), this);
        this.recList.setAdapter(ca);

        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences firstPref = getSharedPreferences(PREFS_NAME, 0);
        if (firstPref.getBoolean("First_Time", true)) {
            //Do first operation
            showMainTutorial();
            firstPref.edit().putBoolean("First_Time", false).apply();
        }

        /*
        Hier wird der clicklListener der weiter unten programmiert ist hinzugefügt
        damit kann man auf Klick events mit einem Switch reagieren
         */

        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);
        fab3.setOnClickListener(clickListener);

        /*
        Gibt an in welcher Geschwindigkeit die normalen Buttons
        auftauchen sollen
         */
        int delay = 400;
        for (final FloatingActionMenu menu : fab_Submenus) {
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    menu.showMenuButton(true);
                }
            }, delay);
            delay += 150;
        }
        /*
        Das toggle sorgt dafür dass der Menübutton aufgeklappt und zugeklappt werden kann
        */
        fab_Menu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fab_Menu.isOpened()) {
                   /*
                   Mit getMenuButtonLabelText() bekommt man den Text der in der XML deklariert ist
                    */
                    // Toast.makeText(getBaseContext(), fab_Menu.getMenuButtonLabelText(), Toast.LENGTH_SHORT).show();
                }
                fab_Menu.toggle(true);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
//            TextView nav_user = (TextView) headNavView.findViewById(R.id.tvNavHeaderTitle);
//            nav_user.setText("test1231231");

        navigationView.setNavigationItemSelectedListener(this);

        headNavView = navigationView.getHeaderView(0);
        nav_image_head = (ImageView) headNavView.findViewById(R.id.ivNavHead);

        if (isDarkTheme) {
            nav_image_head.setImageResource(R.drawable.headerblack);
        } else {
            nav_image_head.setImageResource(R.drawable.headerblue);
        }

        if (isSettoCoverPhoto) {

            // NavDrawer Header manipulieren
            List<MyMarkerObj> navHeaderGetImage = createList2();
            // Falls mind. ein Eintrag existiert, wird das Foto dass als letztes gemacht wurde
            // in den NavHeader eingefügt
            if (navHeaderGetImage.size() != 0) {
                MyMarkerObj mmo = navHeaderGetImage.get(0);

                // Das Datum und die Zeit dienen als Index um die Bilder zu finden z.B. Moments_10-12-2016-18-24-10
                SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
                String imageDate = formatterForImageSearch.format(new Date(mmo.getTimestamp()));

                // Das Bild wird in die Variable f initialisiert
                File f = new File(mmo.getPath());

                memAdapter = new MemoryAdapter();

                bmpHeaderTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bmp = bitmap;

                        // Setzt das Bild in den NavHeader wenn bmp not null ist
                        if (bmp != null) {
                            nav_image_head.setImageBitmap(bmp);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                Picasso.with(this).load(f).resize(540, 540).centerCrop().into(bmpHeaderTarget);

                isCoverPhoto = true;

            }
        }

        momentsCounter = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_camera));
        // Damit wird der Counter initialisiert
        initializeCountDrawer();

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_main, new MainFragment()).commit();
    }

    // Tutorial fuer den User beim ersten Starten der App
    private void showMainTutorial() {
        // Nimmt das FAB Menue als Target
        com.github.amlcurran.showcaseview.targets.Target targetFab = new ViewTarget(R.id.fab_menu, this);

        // Oeffnet das FAB Menue
        fab_Menu.toggle(true);

        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // This aligns button to the bottom left side of screen
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        // Set margins to the button, we add 16dp margins here
        int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        // Startet das Tutorial
        sv = new ShowcaseView.Builder(this)
                .setTarget(targetFab)
                .setContentTitle(getString(R.string.getting_started))
                .setContentText(getString(R.string.getting_started_desc))
                .setStyle(R.style.CustomShowcaseTheme2)
                .setOnClickListener(this)
//                .singleShot(4211)
                .build();

        sv.setButtonText(getString(R.string.next));

        // Button wird nach linksunten verschoben
        sv.setButtonPosition(lps);
    }

    @Override
    public void onClick(View v) {

        // Nach dem die erste Setite vom Tutorial gestartet wurde wird der Counter auf 0 gesetzt und wird dann um eins hochgezaehlt jedesmal wenn man auf Next klickt
        switch (counterTut) {
            case 0:
                try {
                    fab_Menu.toggle(false);
                    // Nimmt den Sidemenu Button als naechsten Fokus
                    ViewTarget navigationButtonViewTarget = ViewTargets.navigationButtonViewTarget(toolbar);
                    sv.setShowcase(navigationButtonViewTarget, true);
                    sv.setContentTitle(getString(R.string.sidemenu));
                    sv.setContentText(getString(R.string.sidemenu_desc));
                    sv.setButtonText(getString(R.string.next));
                    break;
                } catch (ViewTargets.MissingViewException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                sv.setTarget(com.github.amlcurran.showcaseview.targets.Target.NONE);
                sv.setContentTitle(getString(R.string.have_fun));
                sv.setContentText(getString(R.string.have_fun_desc));
                sv.setButtonText(getString(R.string.got_it));
                break;
            case 2:
                sv.hide();
                break;
        }
        counterTut++;
    }

    // Zeigt die Gesamtzahl der Eintraege, im Sidemenu an
    private void initializeCountDrawer() {
        // Gravity property aligns the text
        momentsCounter.setGravity(Gravity.CENTER_VERTICAL);
        momentsCounter.setTypeface(null, Typeface.BOLD);
        if (isDarkTheme) {
            momentsCounter.setTextColor(getResources().getColor(R.color.colorCardViewBlue));
        } else {
            momentsCounter.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        momentsCounter.setText(String.valueOf(momentsCount));

    }


    /*
     Was soll passieren wenn man die normalen Buttons betätigt
      */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab1:
                    Intent i = new Intent(MainActivity.this, ActivityPreference.class);
                    startActivity(i);
                    break;

                case R.id.fab2:

                    FragmentManager fm = getFragmentManager();
                    GmapsFragment gmap = (GmapsFragment) getFragmentManager().findFragmentByTag("GMAP_TAG");

                    if (gmap != null && gmap.isVisible()) {
                        // Es muss ueberprueft werden ob das Gmap Fragment schon sichtbar ist, falls das der Fall ist darf das Fragment nicht nochmal geoeffnet werden
                        // ansonsten stuerzt die App bei BackButtonPressed ab
                    } else {
                        fm.beginTransaction().replace(R.id.content_main, new GmapsFragment(), "GMAP_TAG").addToBackStack(null).commit();
                    }

                    // Damit wird nach den Permissions gefragt bevor die Map aufgebaut wird, somit kann direkt auf den Standort gezoomt werden
                    if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_ACCESS_COARSE_LOCATION);
                    }

                    fab_Menu.toggle(true);

                    break;
                case R.id.fab3:
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = getFile();
                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                            m.invoke(null);
                            camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            startActivityForResult(camera_intent, CAM_REQUEST);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        startActivityForResult(camera_intent, CAM_REQUEST);
                    }
                    break;
            }
        }
    };

    /**
     * Aktualisiert die Listview. Die SharedPreferences von sortDialog werden hiern nochmal
     * aufgerufen und in sortorder gespeichert. danach kann die neue Liste mit der
     * Sortierunge aufgerufen werden
     */
    public void refresh() {

        sp = getBaseContext().getSharedPreferences("prefs_sort", Activity.MODE_PRIVATE);
        this.sortOrder = sp.getInt("sort_mode", 0);
        this.ca = new MemoryAdapter(createList2(), this);
        this.recList.setAdapter(ca);
        ca.notifyDataSetChanged();

        if (createList2().size() == 1) {
            recreate();
        }
    }

    // Die Permissions fuer das Schreiben des External Storage werden hier abgefragt
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
        }
    }

    public void grantLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    // RequestCodes: 1 = Bild mit Kamera aufnehmen, 2 = Bild von Gallerie importieren
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            // Nach dem man ein Foto mit der Kamera aufgenommen hat wird onActivityResult aufgerufen, bei dem ueberprueft wird ob man das Bild mit Ok oder mit Abbrechen bestaetigt hat
            case 1:
                // Wenn nicht auf Abbrechen in der CameraAct. gedrueckt wurde, werden die daten gespeichert
                // und die AddActivity wird gestartet
                if (resultCode == RESULT_OK) {

                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
                    String dateString = formatter.format(new Date(currTime));
                    //Speichert das gemachte Bild im path
                    String path = IMAGE_PATH_URI + IMAGE_NAME_PREFIX + dateString + ".jpg";
                    Drawable.createFromPath(path);
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    // Die currTime Variable wird in die AddActivity weitergegeben, da sie dort als Index benoetigt wird
                    intent.putExtra("currTime", currTime);

                    startActivity(intent);
                }
                break;

            // Bild von der Gallerie importieren
            case 2:
                super.onActivityResult(requestCode, resultCode, data);

                try {
                    // Wenn das Bild ausgewaehlt wurde
                    if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {

                        // Hole das Bild von data
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        String time = "";
                        long originalTimeInMilli = 0;
                        // Cursor holen
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imgDecodableString = cursor.getString(columnIndex);
                        cursor.close();
                        Drawable.createFromPath(imgDecodableString);

                        // Hole das heutige Datum
                        this.currTime = System.currentTimeMillis();


                        ExifInterface exif = new ExifInterface(imgDecodableString);
                        if (exif != null) {
                            time = exif.getAttribute(ExifInterface.TAG_DATETIME);
                            if (time != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
                                try {
                                    Date mDate = sdf.parse(time);
                                    originalTimeInMilli = mDate.getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    time = null;
                                }
                            }
                        }

                        Intent intent = new Intent(MainActivity.this, AddActivity.class);
                        if (time == null) {
                            intent.putExtra("currTime", currTime);
                        } else {
                            intent.putExtra("currTime", originalTimeInMilli);
                        }
                        intent.putExtra("pathName", imgDecodableString);
                        intent.putExtra("fromGallery", "true");


                        startActivity(intent);

                    } else {
                        Toast.makeText(this, R.string.no_pic_chosen, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                }
                break;

        }

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

        File folder = new File(IMAGE_PATH_URI);

        if (!folder.exists()) {
            //Make Directory
            folder.mkdir();
        }

        File image_file = new File(folder, IMAGE_NAME_PREFIX + dateString + ".jpg");

        return image_file;
    }

    /**
     * Erstellt eine Liste aus den Markern (Eintraegen) in der Datenbank
     *
     * @return Marker Liste aus der DB
     */
    public List<MyMarkerObj> createList2() {
        List<MyMarkerObj> m = data.getMyMarkers(sortOrder);
        return m;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

            try{
                for (int i = 0; i <navigationView.getMenu().size() ; i++) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
            } catch (Exception e){

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Die Sortierungsoption in der Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_sort) {
            showSortDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        FragmentManager fm = getFragmentManager();
        DialogFragment sortFragment = new SortDialogFragment();
        sortFragment.show(fm, "SORT_DIALOG");
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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_gallery) {

            // Create intent to Open Image applications like Gallery, Google Photos
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setType("image/*");
            galleryIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

        } else if (id == R.id.nav_moments_gallery) {
            Intent intent = new Intent(MainActivity.this, ActivityGallery.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {

            GmapsFragment gmap = (GmapsFragment) getFragmentManager().findFragmentByTag("GMAP_TAG");

            if (gmap != null && gmap.isVisible()) {
                // Es muss ueberprueft werden ob das Gmap Fragment schon sichtbar ist, falls das der Fall ist darf das Fragment nicht nochmal geoeffnet werden
                // ansonsten stuerzt die App bei BackButtonPressed ab
            } else {

                try{
                    for (int i = 0; i <navigationView.getMenu().size() ; i++) {
                        navigationView.getMenu().getItem(i).setChecked(false);
                    }
                } catch (Exception e){

                }
                /**
                 * addToBackStack verhindert dass die App sich beim BackPressed im GMap Fragment schließt
                 */
                fm.beginTransaction().replace(R.id.content_main, new GmapsFragment(), "GMAP_TAG").addToBackStack(null).commit();

            }

            // Damit wird nach den Permissions gefragt bevor die Map aufgebaut wird, somit kann direkt auf den Standort gezoomt werden
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_ACCESS_COARSE_LOCATION);
            }

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(MainActivity.this, ActivityPreference.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAYSTORE_LINK + this.getPackageName())));
        } else if (id == R.id.nav_send) {


            displayImpressumAlertDialog();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayImpressumAlertDialog() {
        WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.dialog_licenses, null);
        view.loadUrl("file:///android_asset/impressum.html");
        AlertDialog mAlertDialog;
        mAlertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        try{
            for (int i = 0; i <navigationView.getMenu().size() ; i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        } catch (Exception e){

        }
    }
}
