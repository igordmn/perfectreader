package com.dmi.perfectreader.bookstorage

import java.io.InputStream

interface BookStorage {
    val segmentURLs: Array<String>

    val segmentSizes: IntArray

    fun readURL(url: String): InputStream
}