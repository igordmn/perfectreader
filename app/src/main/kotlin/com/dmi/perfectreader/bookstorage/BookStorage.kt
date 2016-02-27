package com.dmi.perfectreader.bookstorage

import java.io.IOException
import java.io.InputStream

interface BookStorage {
    val segmentURLs: Array<String>

    val segmentSizes: IntArray

    @Throws(IOException::class, SecurityException::class)
    fun readURL(url: String): InputStream
}
