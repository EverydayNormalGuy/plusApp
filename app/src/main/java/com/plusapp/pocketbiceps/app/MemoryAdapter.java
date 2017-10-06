package com.plusapp.pocketbiceps.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
import com.plusapp.pocketbiceps.app.fragments.DetailsFragment;
import com.plusapp.pocketbiceps.app.fragments.GmapsFragment;
import com.plusapp.pocketbiceps.app.helperclasses.Photo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Metin on 19.10.2016.
 */

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>  {


    public MarkerDataSource data;

    MyMarkerObj mmo;
    MainActivity mainActivity;
    DetailsFragment detailsFrag = new DetailsFragment();


    // Liste der Titels und Snippets
    private List<MyMarkerObj> m;
    protected static final String IMAGE_NAME_PREFIX = "Moments_";
    private Context mContext;
    boolean isShowDate;
    boolean isSetToShowDate;
    boolean isHighResolution;
    boolean isSetToHighResolution;
    public MemoryAdapter(){

    }

    public MemoryAdapter(List<MyMarkerObj> markerList, Context context){
        this.m = markerList;
        this.mContext=context;

    }
    // Löscht die Liste des Adapters um sie mit den neuen Daten zu füllen und auszugeben
    public void updateAdapter(List<MyMarkerObj> mx){

        mainActivity = ((MainActivity) mContext);
        mainActivity.refresh();

//        m.clear();
//        m.addAll(mx);
//        notifyDataSetChanged();
//        Toast.makeText(mContext, "as"+m, Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final MemoryViewHolder memoryViewHolder, final int i) {

        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String theme_key = mContext.getString(R.string.preference_key_showdate);
        isSetToShowDate = sPrefs.getBoolean(theme_key, true);

        String resolution_key = mContext.getString(R.string.preference_key_resolution);
        isSetToHighResolution = sPrefs.getBoolean(resolution_key, false);

         mmo = m.get(i);
        // Setzt die Werte aus der Datenbank in die CardView Felder
        memoryViewHolder.vTitle.setText(mmo.getTitle());
        memoryViewHolder.vDescription.setText(mmo.getSnippet());
        memoryViewHolder.vCounter.setText(String.valueOf(mmo.getCounter()));

        // Leasst das GMap Icon Grau darstellen wenn keine position zu einem Bild gespeichert wurde
        if (mmo.getPosition().equals("position")){
            DrawableCompat.setTint(memoryViewHolder.ibShowMarker.getDrawable(), ContextCompat.getColor(mContext, R.color.color_grey));
        } else {
            DrawableCompat.setTint(memoryViewHolder.ibShowMarker.getDrawable(), ContextCompat.getColor(mContext, R.color.colorCardViewBlue));
        }

        // Sorge fuer eine dynamische Darstellung der Cardview, je nach dem ob Titel oder Beschreibungen vorhanden sind oder nicht
        if (mmo.getTitle().equals("") && mmo.getSnippet().equals("")){
            memoryViewHolder.vTitle.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) memoryViewHolder.btnDelete.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) memoryViewHolder.btnEdit.getLayoutParams();
            params2.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) memoryViewHolder.ibShare.getLayoutParams();
            params3.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) memoryViewHolder.vCounter.getLayoutParams();
            params4.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params5 = (RelativeLayout.LayoutParams) memoryViewHolder.divider1.getLayoutParams();
            params5.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params6 = (RelativeLayout.LayoutParams) memoryViewHolder.divider2.getLayoutParams();
            params6.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params7 = (RelativeLayout.LayoutParams) memoryViewHolder.divider3.getLayoutParams();
            params7.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params8 = (RelativeLayout.LayoutParams) memoryViewHolder.divider4.getLayoutParams();
            params8.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params9 = (RelativeLayout.LayoutParams) memoryViewHolder.ibShowMarker.getLayoutParams();
            params9.addRule(RelativeLayout.BELOW, R.id.cvImage);
        }

        else if (mmo.getTitle().equals("")){
            memoryViewHolder.vTitle.setVisibility(View.GONE);
        }
        else if (mmo.getSnippet().equals("")){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) memoryViewHolder.btnDelete.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) memoryViewHolder.btnEdit.getLayoutParams();
            params2.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) memoryViewHolder.ibShare.getLayoutParams();
            params3.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) memoryViewHolder.vCounter.getLayoutParams();
            params4.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params5 = (RelativeLayout.LayoutParams) memoryViewHolder.divider1.getLayoutParams();
            params5.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params6 = (RelativeLayout.LayoutParams) memoryViewHolder.divider2.getLayoutParams();
            params6.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params7 = (RelativeLayout.LayoutParams) memoryViewHolder.divider3.getLayoutParams();
            params7.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params8 = (RelativeLayout.LayoutParams) memoryViewHolder.divider4.getLayoutParams();
            params8.addRule(RelativeLayout.BELOW, R.id.cvImage);
            RelativeLayout.LayoutParams params9 = (RelativeLayout.LayoutParams) memoryViewHolder.ibShowMarker.getLayoutParams();
            params9.addRule(RelativeLayout.BELOW, R.id.cvImage);
        }



        memoryViewHolder.mem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(mContext, ActivityDetailsSlider.class);
                    intent.putExtra("position", i);
                    mContext.startActivity(intent);

            }
        });


        memoryViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // muss dringend mit m.get(i) gemacht werden damit beim loeschen das richtige Element geloescht wird und nicht das letzte in der Liste
                mmo = m.get(i);
                showDeleteDialog(mmo);
            }
        });

        memoryViewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivityEdit.class);
                intent.putExtra("index", i);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                mContext.startActivity(intent);
            }
        });

        memoryViewHolder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mmo = m.get(i);
                Uri uri = Uri.parse("file://" + "/" + mmo.getPath());
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType("image/*");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mContext.startActivity(Intent.createChooser(share, "Share image File"));

            }
        });

        memoryViewHolder.ibShowMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mmo = m.get(i);
                if (mmo.getPosition().equals("position")){
//                    Snackbar.make()
                    Snackbar.make(v, R.string.no_loc_saved, Snackbar.LENGTH_LONG).show();
                } else {

                    String position = mmo.getPosition();
                    Bundle bundle = new Bundle();
                    bundle.putString("location_key", position);
                    GmapsFragment gmap = new GmapsFragment();
                    gmap.setArguments(bundle);
                    // Mit Activity cast weil es sich hier um eine Adapter Klasse handelt
                    FragmentManager fm = ((Activity) mContext).getFragmentManager();
                    // WICHTIG: in replace() muss gmap drin sein nicht new GMapFragment sonst werden die Argumente nicht weitergegeben
                    fm.beginTransaction().replace(R.id.content_main, gmap, "GMAP_TAG").addToBackStack(null).commit();
                }
            }
        });

        CharSequence elapsedTime = DateUtils.getRelativeTimeSpanString(mmo.getTimestamp(), System.currentTimeMillis(), 0);

        if (isSetToShowDate == true) {
            memoryViewHolder.vDate.setText(elapsedTime);
            isShowDate = true;
        }


        //Timestamp zum suchen von Bildern aus dem Storage
        SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
        String imageDate=formatterForImageSearch.format(new Date(mmo.getTimestamp()));


        File f = new File(mmo.getPath());

        //Glide uebernimmt das decoden und das Laden der Bilder im Hintergrund um laggs zu vermeiden
        //Context ueber constructor von main activity
//        Picasso.with(mContext).load(f).resize(1080,1080).centerCrop().into(memoryViewHolder.vImage);


        ImageView memImageView = memoryViewHolder.vImage;

        // Hier muss mit Target gearbeitet werden ansonsten werden bei Gifs lediglich placeholder angezeigt
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(memImageView);

        if (isSetToHighResolution == true){
            isHighResolution = true;
            Glide.with(mContext)
                    .load(f)
                    .error(R.drawable.cast_album_art_placeholder)
                    .override(1080,1080)
                    .into(imageViewTarget);
        }
        else {
            Glide.with(mContext)
                    .load(f)
                    .error(R.drawable.cast_album_art_placeholder)
                    .override(612,612)
                    .into(imageViewTarget);
        }
    }

    private void showDeleteDialog(final MyMarkerObj mmo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.delete_message)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        data = new MarkerDataSource(mContext);
                        data.open();
                        data.deleteMarker(mmo);
                        dialog.dismiss();
                        m = data.getMyMarkers(MainActivity.sortOrder);
                        updateAdapter(m); //Ruft notify auf

                        Intent i = new Intent(mContext.getApplicationContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(i);
                        mainActivity.finish();

                    }
                })
                .setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Decodes ImageFile und skalliert es um den Speicher zu entlasten
    // Wird nur noch fuer die Marker in der Gmaps benutzt um das Bild anzuzeigen
    public Bitmap decodeFile(File bmpFile){
        try {

            //Decode Image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(bmpFile),null,o);
            //Die neue Groesse des Bitmaps
            final int REQUIRED_SIZE = 200;

            //Den richtigen scale value finden. Sollte power of 2 sein
            int scale = 1;

            //While Schleife sollte umgangen werden..
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE){
                scale *=2;
            }

            //Decode mit inSampleFile
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeStream(new FileInputStream(bmpFile),null,o2);


        }

        catch (FileNotFoundException e){} catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gibt der MemoryViewHolder Klasse das Layout der View mit auf die geklickt wurde
     */
    @Override
    public MemoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_view, viewGroup, false);

        return new MemoryViewHolder(itemView);
    }

    /**
     * Kuemmert sich um die einzelnen Elemente der CardView und gibt eine View zurueck
     */
    public static class MemoryViewHolder extends RecyclerView.ViewHolder {

        protected TextView vTitle;
        protected TextView vDescription;
        protected TextView vCounter;
        protected TextView vDate;
        protected ImageView vImage;
        protected ImageButton btnDelete;
        protected ImageButton btnEdit;
        protected ImageButton ibShare;
        protected ImageButton ibShowMarker;
        protected View divider1;
        protected View divider2;
        protected View divider3;
        protected View divider4;
        protected View mem;

        public MemoryViewHolder(View v) {
            super(v);
            vTitle =  (TextView) v.findViewById(R.id.tvTitle);
            vDescription = (TextView)  v.findViewById(R.id.tvDescription);
            vCounter = (TextView) v.findViewById(R.id.tvCounter);
            vDate = (TextView) v.findViewById(R.id.tvDate);
            vImage = (ImageView) v.findViewById(R.id.cvImage);
            btnDelete = (ImageButton) v.findViewById(R.id.btnDelete);
            btnEdit = (ImageButton) v.findViewById(R.id.btnEdit);
            ibShare = (ImageButton) v.findViewById(R.id.ibShare);
            divider1 = v.findViewById(R.id.divider);
            divider2 = v.findViewById(R.id.divider2);
            divider3 = v.findViewById(R.id.divider3);
            divider4 = v.findViewById(R.id.divider4);
            ibShowMarker = (ImageButton) v.findViewById(R.id.ibShowMarker);
            mem = v;
        }
    }

    @Override
    public int getItemCount() {
        return m.size();    }
}
