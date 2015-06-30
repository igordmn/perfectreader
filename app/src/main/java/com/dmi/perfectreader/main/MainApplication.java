package com.dmi.perfectreader.main;

import android.app.Application;

import com.dmi.perfectreader.init.ApplicationInitializer;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;

@EApplication
public class MainApplication extends Application {
    @Bean
    protected ApplicationInitializer applicationInitializer;

    @Override
    public void onCreate() {
        applicationInitializer.init();
        super.onCreate();
    }
}
