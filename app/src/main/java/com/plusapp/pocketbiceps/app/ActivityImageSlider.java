package com.plusapp.pocketbiceps.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
import com.plusapp.pocketbiceps.app.helperclasses.HackyViewPager;
import com.plusapp.pocketbiceps.app.helperclasses.Photo;

import java.io.File;
import java.util.List;

public class ActivityImageSlider extends FragmentActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String theme_key = getString(R.string.preference_key_darktheme);
        isSetToDarkTheme = sPrefs.getBoolean(theme_key, false);

        if (isSetToDarkTheme == true) {
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
        int clickedPosition = bundle2.getInt("position");
        mList = data.getMyMarkers(MainActivity.sortOrder);
        NUM_ITEMS = mList.size();
        temp = new Photo[mList.size()];

        for (int i = 0; i < mList.size(); i++){
            temp[i] = new Photo(mList.get(i).getPath(), mList.get(i).getTitle());
        }

        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (HackyViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);
        // Von wo der Pager starten soll
        viewPager.setCurrentItem(clickedPosition);
        viewPager.setOffscreenPageLimit(6);
    }

    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {

        public ImageFragmentPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }

    public static class SwipeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View swipeView = inflater.inflate(R.layout.swipe_fragment, container, false);
            final PhotoView ivSlider = (PhotoView) swipeView.findViewById(R.id.ivSlider);
            Bundle bundle = getArguments();
            final int position = bundle.getInt("position");

            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(ivSlider);

            Glide.with(this)
                    .load(temp[position].getUrl())
                    .error(R.drawable.cast_album_art_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(ivSlider);

                return swipeView;
        }
        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
    }
}
