package com.dmi.util.cache

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

interface DataCache {
    @Throws(IOException::class)
    fun openRead(key: String, dataWriter: DataWriter): InputStream

    interface DataWriter {
        @Throws(IOException::class)
        fun write(outputStream: OutputStream)
    }
}
