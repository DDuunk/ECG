package com.dduunk.ecg.utils;

import android.content.Context;

public class LocalizationManager {

    // Constants
    private final static String TAG = LocalizationManager.class.getSimpleName();

    private final static String kLanguageSettingsFile = "LanguageFile";

    // Singleton
    private static LocalizationManager sInstance = null;

    public static synchronized LocalizationManager getInstance() {
        if (sInstance == null) {
            sInstance = new LocalizationManager();
        }
        return sInstance;
    }

    private LocalizationManager() {
    }

    public String getString(Context context, String stringName) {
        int id = context.getResources().getIdentifier(stringName, "string", context.getPackageName());
        if (id == 0) return null;
        else return context.getResources().getString(id);
    }
}
