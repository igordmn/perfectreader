package com.dmi.perfectreader.asset;

import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.File;

@EBean(scope = EBean.Scope.Singleton)
public class AssetPaths {
    private static final String SYSTEM_ASSETS = "system/";
    private static final String FONT_ASSETS = SYSTEM_ASSETS + "font/";

    @RootContext
    protected Context context;

    public File fontsPath() {
        return new File(context.getFilesDir(), FONT_ASSETS);
    }
}
