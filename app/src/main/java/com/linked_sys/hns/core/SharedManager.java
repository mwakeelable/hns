package com.linked_sys.hns.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

public class SharedManager {
    private static final String PREF_LANGUAGE = "pref_language";
    private final static String DEFAULT_LANGUAGE = "ar";

    public String getLanguage(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getString(PREF_LANGUAGE, DEFAULT_LANGUAGE);
    }

    public void setLanguage(Context context, String language) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        prefs.edit().putString(PREF_LANGUAGE, language).commit();
    }
}