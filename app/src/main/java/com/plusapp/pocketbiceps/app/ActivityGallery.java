package com.plusapp.pocketbiceps.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.plusapp.pocketbiceps.app.helperclasses.Photo;

public class ActivityGallery extends AppCompatActivity {

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
        setContentView(R.layout.activity_gallery);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        ActivityGallery.ImageGalleryAdapter adapter = new ActivityGallery.ImageGalleryAdapter(this, Photo.getPhotos(getBaseContext()));
        recyclerView.setAdapter(adapter);
    }

    private class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>  {

        @Override
        public ImageGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the layout
            View photoView = inflater.inflate(R.layout.custom_item_layout, parent, false);

            ImageGalleryAdapter.MyViewHolder viewHolder = new ImageGalleryAdapter.MyViewHolder(photoView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ImageGalleryAdapter.MyViewHolder holder, int position) {

            Photo photo = mPhotos[position];
            ImageView imageView = holder.mPhotoImageView;

            // Hier muss mit Target gearbeitet werden ansonsten werden bei Gifs lediglich placeholder angezeigt
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);

            Glide.with(mContext)
                    .load(photo.getUrl())
                    .placeholder(R.drawable.cast_album_art_placeholder)
                    .into(imageViewTarget);
        }

        @Override
        public int getItemCount() {
            return (mPhotos.length);
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView mPhotoImageView;

            public MyViewHolder(View itemView) {

                super(itemView);
                mPhotoImageView = (ImageView) itemView.findViewById(R.id.iv_photo);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Photo photo = mPhotos[position];

                    Intent intent = new Intent(mContext, ActivityImageSlider.class);
                    intent.putExtra(ActivityImageSlider.EXTRA_PHOTO, photo);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            }
        }

        private Photo[] mPhotos;
        private Context mContext;

        public ImageGalleryAdapter(Context context, Photo[] photos) {
            mContext = context;
            mPhotos = photos;
        }
    }
}



