package com.dmi.util.cache

import java.io.InputStream
import java.io.OutputStream

interface DataCache {
    fun openRead(key: String, dataWriter: DataWriter): InputStream

    interface DataWriter {
        fun write(outputStream: OutputStream)
    }
}
