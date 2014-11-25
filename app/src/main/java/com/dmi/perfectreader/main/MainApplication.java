package com.dmi.perfectreader.main;

import android.app.Application;

import com.dmi.perfectreader.asset.AssetsCopier;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;

@EApplication
public class MainApplication extends Application {
    @Bean
    protected AssetsCopier assetsCopier;

    @Override
    public void onCreate() {
        assetsCopier.copyAssets();
        super.onCreate();
    }
}
