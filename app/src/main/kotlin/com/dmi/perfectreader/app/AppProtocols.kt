package com.dmi.perfectreader.app

import android.os.Environment.getExternalStorageDirectory
import java.io.File

class AppProtocols {
    fun fileFor(path: String) = when {
        path.startsWith("externalStorage://") -> File(getExternalStorageDirectory(), path.removePrefix("externalStorage://"))
        else -> File(path)
    }
}