package com.plusapp.pocketbiceps.app;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.plusapp.pocketbiceps.app.helperclasses.Photo;

public class ActivitySinglePhoto extends AppCompatActivity {

    public static final String EXTRA_PHOTO = "ActivitySinglePhoto.PHOTO";

    private ImageView mImageView;
    private TextView tvTitle;
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
        setContentView(R.layout.activity_single_photo);

        mImageView = (ImageView) findViewById(R.id.image);
        tvTitle = (TextView) findViewById(R.id.singlePhotoTitle);
        final Photo photo = getIntent().getParcelableExtra(EXTRA_PHOTO);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);


        Glide.with(this)
                .load(photo.getUrl())
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

                        onPalette(Palette.from(resource).generate());
                        progressBar.setVisibility(View.GONE);
                        mImageView.setImageBitmap(resource);
                        tvTitle.setText(photo.getTitle());
                        return false;
                    }

                    public void onPalette(Palette palette) {
                        if (null != palette) {
                            ViewGroup parent = (ViewGroup) mImageView.getParent().getParent();
//                            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY)); aendert die Hintergrund farbe
                        }
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImageView);

    }

   /* private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
           onPalette(Palette.from(bitmap).generate());
           mImageView.setImageBitmap(bitmap);
        }
        public void onPalette(Palette palette) {
            if (null != palette) {
                ViewGroup parent = (ViewGroup) mImageView.getParent().getParent();
                parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
            }
        }
    };*/
}
