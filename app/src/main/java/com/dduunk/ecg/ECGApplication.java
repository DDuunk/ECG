package com.dduunk.ecg;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class ECGApplication extends Application {

    // Log
    private final static String TAG = ECGApplication.class.getSimpleName();

    // Data
    private static boolean mIsActivityVisible;
    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        // Setup LeakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        mRefWatcher = LeakCanary.install(this);
    }

    public static boolean isActivityVisible() {
        return mIsActivityVisible;
    }

    public static void activityResumed() {
        mIsActivityVisible = true;
    }

    public static void activityPaused() {
        mIsActivityVisible = false;
    }
}
