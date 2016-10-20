package com.plusapp.pocketbiceps.app;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Metin on 19.10.2016.
 */

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>  {


    public MarkerDataSource data;

    // Liste der Titels und Snippets
    private List<MemoryInfo> memoryList;
    private List<MyMarkerObj> m;
    protected static final String IMAGE_NAME_PREFIX = "Moments_";


    public MemoryAdapter(List<MyMarkerObj> markerList){
        this.m = markerList;
    }


    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(MemoryViewHolder memoryViewHolder, int i) {


        MyMarkerObj mmo = m.get(i);
        memoryViewHolder.vTitle.setText(mmo.getTitle());
        memoryViewHolder.vDescription.setText(mmo.getSnippet());
        memoryViewHolder.vCounter.setText(String.valueOf(mmo.getCounter()));

        //Konvertiert LongDate aus der DB in eine normale Date View
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String readablyDate = formatter.format(new Date(mmo.getTimestamp()));
        memoryViewHolder.vDate.setText(readablyDate);



        SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
        String imageDate=formatterForImageSearch.format(new Date(mmo.getTimestamp()));


        File f = new File("sdcard/special_moments/"+IMAGE_NAME_PREFIX+imageDate+".jpg");

        Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
        memoryViewHolder.vImage.setImageBitmap(bmp);




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


        public MemoryViewHolder(View v) {
            super(v);
            vTitle =  (TextView) v.findViewById(R.id.tvTitle);
            vDescription = (TextView)  v.findViewById(R.id.tvDescription);
            vCounter = (TextView) v.findViewById(R.id.tvCounter);
            vDate = (TextView) v.findViewById(R.id.tvDate);
            vImage = (ImageView) v.findViewById(R.id.cvImage);

        }
    }

    @Override
    public int getItemCount() {
        return m.size();    }
}
