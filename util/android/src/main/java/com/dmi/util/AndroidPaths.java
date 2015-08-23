package com.dmi.util;

import android.content.Context;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.getExternalStorageState;
import static android.os.Environment.isExternalStorageRemovable;

public abstract class AndroidPaths {
    public static File getCachedDir(Context context) {
        return MEDIA_MOUNTED.equals(getExternalStorageState()) || !isExternalStorageRemovable() ?
               context.getExternalCacheDir() :
               context.getCacheDir();
    }
}
