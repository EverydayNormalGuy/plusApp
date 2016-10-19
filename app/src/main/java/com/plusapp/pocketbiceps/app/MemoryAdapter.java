package com.plusapp.pocketbiceps.app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;

import java.util.List;

/**
 * Created by Metin on 19.10.2016.
 */

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>  {


    public MarkerDataSource data;

    // Liste der Titels und Snippets
    private List<MemoryInfo> memoryList;
    private List<MyMarkerObj> m;


//    public MemoryAdapter(List<MemoryInfo> memoyList) {
//        this.memoryList = memoyList;
//    }

    public MemoryAdapter(List<MyMarkerObj> markerList){
        this.m = markerList;
    }




    @Override
    public void onBindViewHolder(MemoryViewHolder contactViewHolder, int i) {




//        List<MyMarkerObj> m = data.getMyMarkers();
        MyMarkerObj mmo = m.get(i);
        contactViewHolder.vTitle.setText(mmo.getTitle());
        contactViewHolder.vDescription.setText(mmo.getSnippet());


//        MemoryInfo ci = memoryList.get(i);
//        contactViewHolder.vName.setText(ci.name);
//        contactViewHolder.vSurname.setText(ci.surname);

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


        public MemoryViewHolder(View v) {
            super(v);
            vTitle =  (TextView) v.findViewById(R.id.cvTitle);
            vDescription = (TextView)  v.findViewById(R.id.cvDescription);

        }
    }

    @Override
    public int getItemCount() {
        return m.size();    }
}
