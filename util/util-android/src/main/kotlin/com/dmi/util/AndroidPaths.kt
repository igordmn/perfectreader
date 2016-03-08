package com.dmi.util

import android.content.Context
import android.os.Environment.*
import java.io.File

fun Context.availableCacheDir(): File {
    if (MEDIA_MOUNTED == getExternalStorageState() || !isExternalStorageRemovable()) {
        return externalCacheDir
    } else {
        return cacheDir
    }
}