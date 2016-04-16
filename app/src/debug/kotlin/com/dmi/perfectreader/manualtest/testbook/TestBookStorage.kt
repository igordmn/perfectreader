package com.dmi.perfectreader.manualtest.testbook

import android.content.Context
import com.dmi.perfectreader.bookstorage.BookStorage
import java.io.InputStream
import java.util.*

class TestBookStorage(private val context: Context, override val segmentURLs: Array<String>) : BookStorage {
    override val segmentSizes: IntArray
        get() {
            val count = segmentURLs.size
            val sizes = IntArray(count)
            Arrays.fill(sizes, 10)
            return sizes
        }

    override fun readURL(url: String): InputStream {
        if (url.startsWith("assets://")) {
            return context.assets.open(url.substring("assets://".length))
        } else {
            throw SecurityException()
        }
    }
}