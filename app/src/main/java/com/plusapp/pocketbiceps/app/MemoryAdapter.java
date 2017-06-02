package com.plusapp.pocketbiceps.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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



         mmo = m.get(i);
        // Setzt die Werte aus der Datenbank in die CardView Felder
        memoryViewHolder.vTitle.setText(mmo.getTitle());
        memoryViewHolder.vDescription.setText(mmo.getSnippet());
        memoryViewHolder.vCounter.setText("[ "+String.valueOf(mmo.getCounter())+" ]");



        memoryViewHolder.mem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putLong("Details_Id", mmo.getTimestamp());
                notifyDataSetChanged();

                //Da es eine Non Act. Klasse ist muss der Context weitergegeben werden
                //Intent intent =new Intent(mContext,DetailsActivity.class);
                //TODO: hier wurde intent auskommentiert
//                Intent intent = new Intent(mContext, DetailsAct_wo_pager.class);
//                intent.putExtra("index",i);
//                mContext.startActivity(intent);


                FragmentManager fragmentManager = ((Activity) mContext).getFragmentManager();


                Intent intent = new Intent(mContext, ActivityDetailsFullScreen.class);
                intent.putExtra("index",i);
                mContext.startActivity(intent);
            }
        });

        memoryViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, ""+mmo.getTimestamp(), Toast.LENGTH_SHORT).show();

                showDeleteDialog();


            }
        });



        //Konvertiert LongDate aus der DB in eine normale Date View
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String readablyDate = formatter.format(new Date(mmo.getTimestamp()));
        memoryViewHolder.vDate.setText(readablyDate);


        //Timestamp zum suchen von Bildern aus dem Storage
        SimpleDateFormat formatterForImageSearch = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
        String imageDate=formatterForImageSearch.format(new Date(mmo.getTimestamp()));

        File f = new File(MainActivity.IMAGE_PATH_URI+IMAGE_NAME_PREFIX+imageDate+".jpg");

        //Picasso uebernimmt das decoden und das Laden der Bilder im Hintergrund um laggs zu vermeiden
        //Context ueber constructor von main activity
        Picasso.with(mContext).load(f).resize(1080,1080).centerCrop().into(memoryViewHolder.vImage);


    }

    private void showDeleteDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Diesen Eintrag löschen?")
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        data = new MarkerDataSource(mContext);
                        data.open();
                        data.deleteMarker(mmo);
                        dialog.dismiss();
                        m = data.getMyMarkers(MainActivity.sortOrder);
                        updateAdapter(m); //Ruft notify auf

                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }







    // Decodes ImageFile und skalliert es um den Speicher zu entlasten
    // Wird nur noch in der AddActivity benutzt um das Bild in der Imageview anzuzeigen
    // TODO: decodeFile() in der AddActivity mit Picasso ersetzen
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
        protected Button btnDelete;
        protected Button btnEdit;
        protected View mem;

        public MemoryViewHolder(View v) {
            super(v);
            vTitle =  (TextView) v.findViewById(R.id.tvTitle);
            vDescription = (TextView)  v.findViewById(R.id.tvDescription);
            vCounter = (TextView) v.findViewById(R.id.tvCounter);
            vDate = (TextView) v.findViewById(R.id.tvDate);
            vImage = (ImageView) v.findViewById(R.id.cvImage);
            btnDelete = (Button) v.findViewById(R.id.btnDelete);
            btnEdit = (Button) v.findViewById(R.id.btnEdit);
            mem = v;
        }
    }

    @Override
    public int getItemCount() {
        return m.size();    }
}
