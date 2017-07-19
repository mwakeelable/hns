package com.linked_sys.hns.ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import com.linked_sys.hns.R;
import com.linked_sys.hns.core.LocaleUtils;
import com.linked_sys.hns.ui.Fragments.SettingsFragment;


public class SettingsActivity extends ParentActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences prefs;
    String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.settingsContainer, new SettingsFragment())
                    .commit();
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_settings;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Intent thisActivity = getIntent();
        if (key.equals("lang")) {
            lang = prefs.getString("lang", "1");
            if (lang.equals("1")) {
                LocaleUtils.changeLocale(SettingsActivity.this, "ar");
                manager.setLanguage(SettingsActivity.this, "ar");
                restartActivity(thisActivity);
            } else {
                LocaleUtils.changeLocale(SettingsActivity.this, "en");
                manager.setLanguage(SettingsActivity.this, "en");
                finish();
                restartActivity(thisActivity);
            }
        }
    }

    @Override
    public void onBackPressed() {
        openActivity(MainActivity.class);
        super.onBackPressed();
    }
}
