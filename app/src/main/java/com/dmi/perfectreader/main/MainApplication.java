package com.dmi.perfectreader.main;

import android.app.Application;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;

@EApplication
public class MainApplication extends Application {
    @Bean
    protected ApplicationInitializer applicationInitializer;

    @Override
    public void onCreate() {
        applicationInitializer.runInit();
        super.onCreate();
    }
}
