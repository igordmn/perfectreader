package com.dmi.perfectreader.init;

import android.content.Context;

import com.dmi.perfectreader.asset.AssetsCopier;
import com.dmi.perfectreader.db.Databases;
import com.dmi.util.Units;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

@EBean(scope = EBean.Scope.Singleton)
public class ApplicationInitializer {
    @Bean
    protected AssetsCopier assetsCopier;
    @Bean
    protected Databases databases;
    @RootContext
    protected Context context;

    public void init() {
        assetsCopier.copyAssets();
        databases.createOrUpgrade();
        Units.init(context);
    }
}
