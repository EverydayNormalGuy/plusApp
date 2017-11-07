package com.plusapp.pocketbiceps.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.github.chrisbanes.photoview.PhotoView;
import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
import com.plusapp.pocketbiceps.app.helperclasses.HackyViewPager;
import com.plusapp.pocketbiceps.app.helperclasses.Photo;

import java.util.List;

/**
 * Hierhin gelangt man nachdem man einen Eintrag in der MainActivity auswaehlt.
 * Der User bekommt das Bild in Originalgroeße mit Titel, Beschreibung und den Optionen Teilen und Bearbeiten, angezeigt
 * Hier liegen noch Codezeilen von der Funktion "hideUIControls.onClick" mit der es moeglich sein sollte den Titel die Beschreibung und die Buttons per Klick auf das Bild
 * zu verstecken. An dieser Funktion wird aber nicht mehr weitergearbeitet.
 */
public class ActivityDetailsSlider extends AppCompatActivity {

    public static final String EXTRA_PHOTO = "ActivityImageSlider.PHOTO";
    static int NUM_ITEMS = 5;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    HackyViewPager viewPager;
    MarkerDataSource data;
    public static List<MyMarkerObj> mList;
    public static Photo temp[];
    public static Photo photo;
    boolean isSetToDarkTheme;
    boolean isDarkTheme;
    static boolean isHighResolution;
    static boolean isSetToHighResolution;

    MyMarkerObj mmo;

    int clickedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String theme_key = getString(R.string.preference_key_darktheme);
        isSetToDarkTheme = sPrefs.getBoolean(theme_key, false);

        String resolution_key = getString(R.string.preference_key_resolution);
        isSetToHighResolution = sPrefs.getBoolean(resolution_key, false);

        if (isSetToDarkTheme) {
            setTheme(R.style.DarkTheme);
            isDarkTheme = true;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        data = new MarkerDataSource(this);
        data.open();

        photo = getIntent().getParcelableExtra(EXTRA_PHOTO);
        // bundle2 wird benoetigt um die position des angeklickten elements auslesen zu koennen
        Bundle bundle2 = getIntent().getExtras();
        clickedPosition = bundle2.getInt("position");
        mList = data.getMyMarkers(MainActivity.sortOrder);
        mmo = mList.get(clickedPosition);
        NUM_ITEMS = mList.size();
        temp = new Photo[mList.size()];

        for (int i = 0; i < mList.size(); i++){
            temp[i] = new Photo(mList.get(i).getPath(), mList.get(i).getTitle(), mList.get(i).getSnippet());
        }

        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (HackyViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);
        // Von wo der Pager starten soll
        viewPager.setCurrentItem(clickedPosition);
//        viewPager.setOffscreenPageLimit(6);
        data.updateMarker(mmo); // Zaehlt lediglich den Counter von "Gesehen" hoch
    }

    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {

        private ImageFragmentPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();

            fragment.mVisible2 = true;
            return SwipeFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS; // Groesse der Liste von allen Eintraegen
        }
    }

    public static class SwipeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View swipeView = inflater.inflate(R.layout.activity_details_slider, container, false);
            final PhotoView ivSlider = (PhotoView) swipeView.findViewById(R.id.ivMomentDetails);
            Bundle bundle = getArguments();
            final int position = bundle.getInt("position");

            mVisible2 = true;
            mControlsView2 = swipeView.findViewById(R.id.fullscreen_content_controls2); // Titel und Beschreibung
            mContentView2 = swipeView.findViewById(R.id.fullscreen_content2);
            mControlsViewTop2 = swipeView.findViewById(R.id.fullscreen_content_controls_top_buttons2); // Buttons Teilen und Bearbeiten

            // Set up the user interaction to manually show2 or hide2 the system UI.
            mContentView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggle2();
                }
            });

            // Upon interacting with UI controls, delay any scheduled hide2()
            // operations to prevent the jarring behavior of controls going away
            // while interacting with the UI.

            swipeView.findViewById(R.id.tvMomentsTitle).setOnTouchListener(mDelayHideTouchListener2);
            swipeView.findViewById(R.id.tvMomentsDetails).setOnTouchListener(mDelayHideTouchListener2);
            swipeView.findViewById(R.id.btnEditDetails).setOnTouchListener(mDelayHideTouchListener2);
            swipeView.findViewById(R.id.btnShareDetails).setOnTouchListener(mDelayHideTouchListener2);

            TextView tvDetailsTitle = (TextView) swipeView.findViewById(R.id.tvMomentsTitle);
            TextView tvDetailsDescr= (TextView) swipeView.findViewById(R.id.tvMomentsDetails);


            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(ivSlider);

            if (isSetToHighResolution){
                isHighResolution = true;
                Glide.with(this)
                        .load(temp[position].getUrl())
                        .error(R.drawable.cast_album_art_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(ivSlider);
            } else {
                Glide.with(this)
                        .load(temp[position].getUrl())
                        .error(R.drawable.cast_album_art_placeholder)
                        .override(612,612)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(ivSlider);
            }

            tvDetailsTitle.setText(temp[position].getTitle());
            tvDetailsDescr.setText(temp[position].getDescription());

            tvDetailsDescr.setMovementMethod(new ScrollingMovementMethod()); // Dadurch kann man durch die Textview scrollen
            Button btn_EditDetails = (Button) swipeView.findViewById(R.id.btnEditDetails);
            Button btn_ShareDetails = (Button) swipeView.findViewById(R.id.btnShareDetails);

            btn_EditDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getBaseContext(), ActivityEdit.class);
                    intent.putExtra("index", position);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    getActivity().startActivity(intent);
                }
            });

            btn_ShareDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Uri uri = Uri.parse("file://" + "/" + temp[position].getUrl());
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.setType("image/*");
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(share, "Share image File"));

                }
            });

//            Glide.with(this).load(temp[position].getUrl()).into(ivSlider);
            return swipeView;
        }
        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }

        /**
         * Whether or not the system UI should be auto-hidden after
         * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
         */
        private static final boolean AUTO_HIDE = false;

        /**
         * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private static final int AUTO_HIDE_DELAY_MILLIS = 50;

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private static final int UI_ANIMATION_DELAY = 50;
        private final Handler mHideHandler = new Handler();
        private  View mContentView2;
        private final Runnable mHidePart2Runnable = new Runnable() {
            @SuppressLint("InlinedApi")
            @Override
            public void run() {
                // Delayed removal of status and navigation bar

                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView2.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        };
        private  View mControlsView2;
        private  View mControlsViewTop2;

        private final Runnable mShowPart2Runnable = new Runnable() {
            @Override
            public void run() {
                // Delayed display of UI elements
                // Da vom Fragment aufgerufen muss getActivity davor stehen
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }

            mControlsView2.setVisibility(View.VISIBLE);
                mControlsViewTop2.setVisibility(View.VISIBLE);
            }
        };
        boolean mVisible2;
        private final Runnable mHideRunnable = new Runnable() {
            @Override
            public void run() {
                hide2();
            }
        };
        /**
         * Touch listener to use for in-layout UI controls to delay hiding the
         * system UI. This is to prevent the jarring behavior of controls going away
         * while interacting with activity UI.
         */
        private final View.OnTouchListener mDelayHideTouchListener2 = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (AUTO_HIDE) {
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
                return false;
            }
        };
        private void toggle2() {
            if (mVisible2) {
                hide2();
            } else {
                show2();
            }
        }
        private void hide2() {
            // Hide UI first
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
            mControlsView2.setVisibility(View.GONE);
            mControlsViewTop2.setVisibility(View.GONE);
            mVisible2 = false;

            // Schedule a runnable to remove the status and navigation bar after a delay
            mHideHandler.removeCallbacks(mShowPart2Runnable);
            mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
        }

        @SuppressLint("InlinedApi")
        private void show2() {
            // Show the system bar
            mContentView2.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            mVisible2 = true;

            // Schedule a runnable to display UI elements after a delay
            mHideHandler.removeCallbacks(mHidePart2Runnable);
            mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
        }

        /**
         * Schedules a call to hide2() in [delay] milliseconds, canceling any
         * previously scheduled calls.
         */
        private void delayedHide(int delayMillis) {
            mHideHandler.removeCallbacks(mHideRunnable);
            mHideHandler.postDelayed(mHideRunnable, delayMillis);
        }
    }

    // Sorgt dafuer dass der Stack der Activities geloescht wird
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ActivityDetailsSlider.this, MainActivity.class);
        //Cleared den ganzen Activitystack
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}