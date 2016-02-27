package com.dmi.util

import android.content.Context
import android.os.Environment.*
import java.io.File

object AndroidPaths {
    fun getCachedDir(context: Context): File {
        return if (MEDIA_MOUNTED == getExternalStorageState() || !isExternalStorageRemovable())
            context.externalCacheDir
        else
            context.cacheDir
    }
}
