package com.plusapp.pocketbiceps.app;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ActivityDetailsFullScreen extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static List<MyMarkerObj> m;
    private static int index;
    protected static final String IMAGE_NAME_PREFIX = "Moments_";
    public static MarkerDataSource data;
    private String imageDate;
    private Button btn_ShareDetails;
    private Button btn_EditDetails;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 50;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private View mControlsViewTop;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
            mControlsViewTop.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_full_screen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        mControlsViewTop = findViewById(R.id.fullscreen_content_controls_top_buttons);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.tvMomentsTitle).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.tvMomentsDetails).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.btnEditDetails).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.btnShareDetails).setOnTouchListener(mDelayHideTouchListener);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Das Key Argument "currTime" muss mit dem Key aus der MainAct. uebereinstimmen
            this.index = extras.getInt("index");


            data = new MarkerDataSource(this);
            data.open();
            m = data.getMyMarkers(MainActivity.sortOrder);
            m.get(index);

            ImageView ivMomentDetails = (ImageView) findViewById(R.id.ivMomentDetails);
            TextView tvDetailsTitle = (TextView) findViewById(R.id.tvMomentsTitle);
            TextView tvDetailsDescr= (TextView) findViewById(R.id.tvMomentsDetails);

            MyMarkerObj mmo = m.get(index);

            SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
            imageDate=formatterForImageSearch.format(new Date(mmo.getTimestamp()));

            File f = new File(MainActivity.IMAGE_PATH_URI+IMAGE_NAME_PREFIX+imageDate+".jpg");

            MemoryAdapter mem = new MemoryAdapter();
            //Bitmap bmp = mem.decodeFile(f);

            String orientation="";
            try {
                ExifInterface exif = new ExifInterface(MainActivity.IMAGE_PATH_URI+IMAGE_NAME_PREFIX+imageDate+".jpg");
                orientation= exif.getAttribute(ExifInterface.TAG_ORIENTATION); // 6 Vertikal, 1 Horizontal
            } catch (IOException e) {
                e.printStackTrace();
            }

//            if (orientation.equals("1")){
//                Picasso.with(getBaseContext()).load(f).fit().into(ivMomentDetails);
//            }
//            else if (orientation.equals("6")){
//                Picasso.with(getBaseContext()).load(f).fit().into(ivMomentDetails);
//            }

            final Point displaySize = getDisplaySize(getWindowManager().getDefaultDisplay());
            final int size = (int) Math.ceil(Math.sqrt(displaySize.x * displaySize.y));
            Picasso.with(this)
                    .load(f)
                    .resize(size, size)
                    .centerInside()
                    .into(ivMomentDetails);


            //imageView.setImageBitmap(bmp);
//            textView.setText(mmo.getTitle());
//            textViewDescr.setText(mmo.getSnippet());

            tvDetailsTitle.setText(mmo.getTitle());
            tvDetailsDescr.setText(mmo.getSnippet());
            tvDetailsDescr.setMovementMethod(new ScrollingMovementMethod()); // Dadurch kann man durch die Textview scrollen
            data.updateMarker(mmo);

            btn_EditDetails = (Button) findViewById(R.id.btnEditDetails);
            btn_ShareDetails = (Button) findViewById(R.id.btnShareDetails);

            btn_EditDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ActivityDetailsFullScreen.this, "editieren..", Toast.LENGTH_SHORT).show();
                }
            });

            btn_ShareDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("file://" + "/" + MainActivity.IMAGE_PATH_URI + IMAGE_NAME_PREFIX + imageDate + ".jpg");

                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.setType("image/*");
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(share, "Share image File"));

                }
            });

        }
    }

    // https://stackoverflow.com/questions/10271020/bitmap-too-large-to-be-uploaded-into-a-texture
    public Point getDisplaySize(Display display) {
        Point size = new Point();

            int width = display.getWidth();
            int height = display.getHeight();
            size = new Point(width, height);

        return size;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mControlsViewTop.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    // Sorgt dafuer dass der Stack der Activities geloescht wird
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ActivityDetailsFullScreen.this, MainActivity.class);
        //Cleared den ganzen Activitystack
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
