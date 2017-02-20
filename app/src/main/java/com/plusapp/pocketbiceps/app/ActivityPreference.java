package com.plusapp.pocketbiceps.app;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.prefs.PreferenceChangeListener;

/**
 * Created by Steffi on 18.02.2017.
 */

public class ActivityPreference extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference darkTheme = findPreference(getString(R.string.preference_key_darktheme));

        darkTheme.setOnPreferenceChangeListener(this);


        final CheckBoxPreference checkBoxPreference= (CheckBoxPreference) getPreferenceManager().findPreference(getString(R.string.preference_key_darktheme));
        checkBoxPreference.
                setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("true")) {
                            Toast.makeText(getApplicationContext(), "CB: " + "true",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "CB: " + "false",
                                    Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });



    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}