package com.plusapp.pocketbiceps.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plusapp.pocketbiceps.app.R;


public class DetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


    }

}
