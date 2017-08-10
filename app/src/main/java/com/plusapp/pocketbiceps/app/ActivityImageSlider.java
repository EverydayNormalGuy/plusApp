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
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
import com.plusapp.pocketbiceps.app.helperclasses.Photo;

import java.util.List;

public class ActivityImageSlider extends FragmentActivity {

    public static final String EXTRA_PHOTO = "ActivityImageSlider.PHOTO";
    static int NUM_ITEMS = 5;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    public static final String[] IMAGE_NAME = {"lak","kla","lkja"};
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
        viewPager = (ViewPager) findViewById(R.id.pager);
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
            final ImageView ivSlider = (ImageView) swipeView.findViewById(R.id.ivSlider);
            final TextView tvSliderTitle = (TextView) swipeView.findViewById(R.id.tvSliderTitle);
            Bundle bundle = getArguments();
            final int position = bundle.getInt("position");

            // Da es sich hier um eine inner class handelt muss auf die rootView die findViewById Methode angesetzt werden. Ausserdem muss es wegen der innerclass final sein
            final ProgressBar progressBar = (ProgressBar) swipeView.findViewById(R.id.progress);

            Glide.with(this)
                    .load(temp[position].getUrl())
                    .asBitmap()
                    .error(R.drawable.cast_album_art_placeholder)
                    .listener(new RequestListener<String, Bitmap>() {

                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            onPalette(Palette.from(resource).generate());
                            ivSlider.setImageBitmap(resource);
                            tvSliderTitle.setText(temp[position].getTitle());
                            return false;
                        }

                        public void onPalette(Palette palette) {
                            if (null != palette) {
                                ViewGroup parent = (ViewGroup) ivSlider.getParent().getParent();
//                            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY)); aendert die Hintergrund farbe
                            }
                        }
                    })
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
