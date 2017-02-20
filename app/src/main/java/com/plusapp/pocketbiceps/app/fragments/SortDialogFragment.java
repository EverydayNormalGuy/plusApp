package com.plusapp.pocketbiceps.app.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.plusapp.pocketbiceps.app.R;

/**
 * Created by Steffi on 19.02.2017.
 */

public class SortDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sortieren nach ")
                .setItems(R.array.sortDialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

}
