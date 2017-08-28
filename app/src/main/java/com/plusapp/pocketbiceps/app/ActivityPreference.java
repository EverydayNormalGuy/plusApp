package com.plusapp.pocketbiceps.app;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.prefs.PreferenceChangeListener;

/**
 * Created by Steffi on 18.02.2017.
 */

public class ActivityPreference extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    boolean isDarkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String theme_key = getString(R.string.preference_key_darktheme);
        boolean isSetToDarkTheme = sPrefs.getBoolean(theme_key, false);

        if (isSetToDarkTheme == true) {
            setTheme(R.style.DarkTheme);
            isDarkTheme = true;
        }
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference darkTheme = findPreference(getString(R.string.preference_key_darktheme));

        darkTheme.setOnPreferenceChangeListener(this);

        MainActivity ma = new MainActivity();

        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceManager().findPreference(getString(R.string.preference_key_darktheme));
        checkBoxPreference.
                setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("true")) {

                        } else {

                        }
                        return true;
                    }
                });

        final CheckBoxPreference checkBoxPreferenceCoverphoto = (CheckBoxPreference) getPreferenceManager().findPreference(getString(R.string.preference_key_coverphoto));
        checkBoxPreferenceCoverphoto.
                setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("true")) {

                        } else {

                        }

                        return true;
                    }
                });


        // Open-Source Libs
        Preference openSource = findPreference("showOpenSourceLibs");
        openSource.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                displayLicensesAlertDialog();
                return false;
            }
        });

        // About Dialog
        Preference about = findPreference("about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                displayAboutAlertDialog();
                return false;
            }
        });
    }

    // Open-Source Dialog
    private void displayLicensesAlertDialog() {
        WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.dialog_licenses, null);
        view.loadUrl("file:///android_asset/open_source_licenses.html");
        AlertDialog mAlertDialog;
        mAlertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle("Licenses")
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }


    // About Dialog
    private void displayAboutAlertDialog() {

        AlertDialog mAlertDialog;
        mAlertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle("Über")
                .setIcon(R.drawable.iconwobg)
                .setMessage("Das ist eine kostenfreie App für die private Nutzung")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ActivityPreference.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}
