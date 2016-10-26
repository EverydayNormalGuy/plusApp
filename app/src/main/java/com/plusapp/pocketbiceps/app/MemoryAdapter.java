package com.plusapp.pocketbiceps.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
import com.plusapp.pocketbiceps.app.fragments.DetailsFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

/**
 * Created by Metin on 19.10.2016.
 */

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>  {


    public MarkerDataSource data;

    DetailsFragment detailsFrag = new DetailsFragment();


    // Liste der Titels und Snippets
    private List<MyMarkerObj> m;
    protected static final String IMAGE_NAME_PREFIX = "Moments_";
    private Context mContext;
    public MemoryAdapter(){

    }

    public MemoryAdapter(List<MyMarkerObj> markerList, Context context){
        this.m = markerList;
        this.mContext=context;

    }


    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final MemoryViewHolder memoryViewHolder, final int i) {



        final MyMarkerObj mmo = m.get(i);
        memoryViewHolder.vTitle.setText(mmo.getTitle());
        memoryViewHolder.vDescription.setText(mmo.getSnippet());
        memoryViewHolder.vCounter.setText("[ "+String.valueOf(mmo.getCounter())+" ]");



        memoryViewHolder.mem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putLong("Details_Id", mmo.getTimestamp());

                //Da es eine Non Act. Klasse ist muss der Context weitergegeben werden
                Intent intent =new Intent(mContext,DetailsActivity.class);
                intent.putExtra("index",i);
                mContext.startActivity(intent);


                FragmentManager fragmentManager = ((Activity) mContext).getFragmentManager();

            }
        });




        //Konvertiert LongDate aus der DB in eine normale Date View
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String readablyDate = formatter.format(new Date(mmo.getTimestamp()));
        memoryViewHolder.vDate.setText(readablyDate);



        SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
        String imageDate=formatterForImageSearch.format(new Date(mmo.getTimestamp()));


        File f = new File("sdcard/special_moments/"+IMAGE_NAME_PREFIX+imageDate+".jpg");


//        Bitmap bmp = decodeFile(f);
//        memoryViewHolder.vImage.setImageBitmap(bmp);

        //Picasso uebernimmt das decoden und das laden der Bilder im Hintergrund um laggs zu vermeiden
        //Context ueber constructor von main activity
        Picasso.with(mContext).load(f).resize(1080,1080).into(memoryViewHolder.vImage);



    }
    //Decodes ImageFile und skalliert es um den Speicher zu entlasten
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

        catch (FileNotFoundException e){}

        return null;
    }

    @Override
    public MemoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_view, viewGroup, false);



        return new MemoryViewHolder(itemView);
    }

    public static class MemoryViewHolder extends RecyclerView.ViewHolder {

        protected TextView vTitle;
        protected TextView vDescription;
        protected TextView vCounter;
        protected TextView vDate;
        protected ImageView vImage;
        protected View mem;

        public MemoryViewHolder(View v) {
            super(v);


            vTitle =  (TextView) v.findViewById(R.id.tvTitle);
            vDescription = (TextView)  v.findViewById(R.id.tvDescription);
            vCounter = (TextView) v.findViewById(R.id.tvCounter);
            vDate = (TextView) v.findViewById(R.id.tvDate);
            vImage = (ImageView) v.findViewById(R.id.cvImage);
            mem = v;
        }
    }

    @Override
    public int getItemCount() {
        return m.size();    }
}
