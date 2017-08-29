package com.plusapp.pocketbiceps.app.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.plusapp.pocketbiceps.app.MainActivity;
import com.plusapp.pocketbiceps.app.MemoryAdapter;
import com.plusapp.pocketbiceps.app.R;
import com.plusapp.pocketbiceps.app.database.MarkerDataSource;
import com.plusapp.pocketbiceps.app.database.MyMarkerObj;
import com.plusapp.pocketbiceps.app.database.MySqlHelper;

import java.util.List;

/**
 * Created by Steffi on 19.02.2017.
 */

public class SortDialogFragment extends DialogFragment {

    SharedPreferences sp;
    MainActivity ma = new MainActivity();
    MainActivity mainActivity;

    /**
     * Erstellt den Sortierungsdialog. Nach der Auswahl wird die refresh() der MainActivity aufgerufen.
     * Um hier die Methode richtig aufrufen zu können, muss man den Context getActivity mit (MainActivity) casten
     *
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Sortieren nach ")
                .setItems(R.array.sortDialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                saveSortOrder(0); //neuste
                                break;
                            case 1:
                                saveSortOrder(1); //aelteste
                                break;
                            case 2:
                                saveSortOrder(2); //meistgesehen
                                break;
                            case 3:
                                saveSortOrder(3); //am wenigsten gesehen
                                break;
                            default:
                                break;
                        }
                        mainActivity = ((MainActivity) getActivity());
                        dialog.dismiss();
                        mainActivity.refresh();

                    }
                });
        return builder.create();

    }


    public void saveSortOrder(int sortModeX) {
        sp = getActivity().getSharedPreferences("prefs_sort", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("sort_mode", sortModeX);
        editor.apply();
    }

}