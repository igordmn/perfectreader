package com.dmi.perfectreader.cache;

import android.annotation.SuppressLint;
import android.content.Context;

import com.dmi.util.cache.DataCache;
import com.dmi.util.cache.DiskDataCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import timber.log.Timber;

import static com.dmi.perfectreader.BuildConfig.VERSION_CODE;
import static com.dmi.util.AndroidPaths.getCachedDir;
import static java.lang.String.format;

@Singleton
public class BookResourceCache implements DataCache {
    private static final String CACHE_DIR = "bookResource";
    private static final int MAX_SIZE = 32 * 1024 * 1024; // 32 MB

    private DiskDataCache diskDataCache;
    private File currentCacheDir;

    @Inject
    @Named("applicationContext")
    protected Context context;

    public void close() {
        if (diskDataCache != null) {
            try {
                diskDataCache.close();
                diskDataCache = null;
            } catch (IOException e) {
                Timber.e(e, "Book resource cache closing error");
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    public InputStream openRead(String key, DataWriter dataWriter) throws IOException {
        File cacheDir = getCacheDir();
        if (!cacheDir.equals(currentCacheDir) && diskDataCache != null) {
            diskDataCache.close();
            diskDataCache = null;
        }
        if (diskDataCache == null) {
            diskDataCache = new DiskDataCache(cacheDir, VERSION_CODE, MAX_SIZE);
        }
        currentCacheDir = cacheDir;
        return diskDataCache.openRead(key, dataWriter);
    }

    private File getCacheDir() {
        return new File(getCachedDir(context), CACHE_DIR);
    }

    public static String resourceKey(String bookFilePath, String innerResourcePath, long lastModified) {
        return format("file: %s; url: %s; lastModified: %s", bookFilePath, innerResourcePath, lastModified);
    }
}
