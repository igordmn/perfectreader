package com.dmi.perfectreader.asset;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;

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
            // todo отображать ошибку
            throw new RuntimeException(e);
        }
    }

    private void tryCopyAssets() throws IOException {
        tryCopyAssets("fonts", assetPaths.fontsPath());
    }

    @SuppressLint("NewApi")
    private void tryCopyAssets(String path, File destination) throws IOException {
        AssetManager assets = context.getAssets();
        String[] files = assets.list(path);
        for (String file : files) {
            try (InputStream in = assets.open(path + "/" + file)) {
                File newFile = new File(destination, file);
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
