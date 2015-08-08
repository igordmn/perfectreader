package com.dmi.perfectreader.main;

import android.app.Application;

import com.dmi.perfectreader.BuildConfig;

import org.androidannotations.annotations.EApplication;

import timber.log.Timber;

@EApplication
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
