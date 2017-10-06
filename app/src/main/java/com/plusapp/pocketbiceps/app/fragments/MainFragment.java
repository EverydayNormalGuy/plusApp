package com.plusapp.pocketbiceps.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.plusapp.pocketbiceps.app.MainActivity;
import com.plusapp.pocketbiceps.app.R;

public class MainFragment extends Fragment {

    TextView tvEmpty;
    int number;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        number = ((MainActivity) getActivity()).createList2().size();
        if (number < 1) {
            tvEmpty = (TextView) getActivity().findViewById(R.id.tvEmpty);
            tvEmpty.setVisibility(View.VISIBLE);
        } else
        {
            tvEmpty = (TextView) getActivity().findViewById(R.id.tvEmpty);
            tvEmpty.setVisibility(View.GONE);
        }
    }
}
