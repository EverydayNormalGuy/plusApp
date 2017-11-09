package com.plusapp.pocketbiceps.app.helperclasses;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.plusapp.pocketbiceps.app.MainActivity;
import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by guemuesm on 28.07.2017.
 */

public class Photo implements Parcelable {

    private static MarkerDataSource data;
    private String mUrl;
    private String mTitle;
    private String mDescription;
    MyMarkerObj mmo;
    static List<MyMarkerObj> mList;

    public Photo(String url, String title) {
        mUrl = url;
        mTitle = title;
    }

    public Photo (String url, String title, String description){
        mUrl = url;
        mTitle = title;
        mDescription = description;
    }

    protected Photo(Parcel in) {
        mUrl = in.readString();
        mTitle = in.readString();
        mDescription = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public static  Photo[] getPhotos(Context context) {

        data = new MarkerDataSource(context);
        data.open();
        mList = data.getMyMarkers(MainActivity.sortOrder);

        Photo temp[] = new Photo[mList.size()];

        for (int i = 0; i < mList.size(); i++){
            temp[i] = new Photo(mList.get(i).getPath(), mList.get(i).getTitle(), mList.get(i).getSnippet());
        }

        return temp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mUrl);
        parcel.writeString(mTitle);
        parcel.writeString(mDescription);
    }
}