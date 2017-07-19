package com.linked_sys.hns.core;


import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleUtils {
    public static void changeLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, null);
    }
}
