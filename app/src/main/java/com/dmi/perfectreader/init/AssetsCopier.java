package com.dmi.perfectreader.init;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;

import com.dmi.perfectreader.asset.AssetPaths;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// todo добавить версионность. при копировании проверять, совпадает ли версия. как вариант, добавить спец. файл version в папку assets
@EBean(scope = EBean.Scope.Singleton)
public class AssetsCopier {
    @RootContext
    protected Context context;
    @Bean
    protected AssetPaths assetPaths;

    public void copyAssets() {
        try {
            tryCopyAssets();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("NewApi")
    private void tryCopyAssets() throws IOException {
        AssetManager assets = context.getAssets();
        String[] fontFiles = assets.list("font");
        File fontsPath = assetPaths.fontsPath();
        for (String fontFile : fontFiles) {
            try (InputStream in = assets.open("font/" + fontFile)) {
                File newFile = new File(fontsPath, fontFile);
                if (!newFile.exists()) {
                    Files.createParentDirs(newFile);
                    try (OutputStream out = new FileOutputStream(newFile)) {
                        ByteStreams.copy(in, out);
                    }
                }
            }
        }
    }
}
