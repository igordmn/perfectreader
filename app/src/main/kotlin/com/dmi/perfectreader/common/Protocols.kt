package com.dmi.perfectreader.common

import android.os.Environment.getExternalStorageDirectory
import java.io.File

// todo combine with ProtocolURIHandler
class Protocols {
    fun fileFor(path: String) = when {
        path.startsWith("externalStorage://") -> File(getExternalStorageDirectory(), path.removePrefix("externalStorage://"))
        else -> File(path)
    }
}