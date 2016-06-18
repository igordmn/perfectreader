package com.dmi.perfectreader.fragment.book.bitmap

import com.dmi.util.ext.cache
import java.io.IOException

class CachedBitmapDecoder(private val bitmapDecoder: BitmapDecoder) : BitmapDecoder {
    private val dimensions = cache(softValues = true) { path: String ->
        bitmapDecoder.loadDimensions(path)
    }
    private val bitmaps = cache(softValues = true) { key: BitmapKey ->
        bitmapDecoder.decode(key.src, key.maxWidth, key.maxHeight)
    }

    override fun loadDimensions(src: String) = try {
        dimensions[src]
    } catch(e: Exception) {
        throw IOException(e)
    }

    override fun decode(src: String, maxWidth: Float, maxHeight: Float) = try {
        bitmaps[BitmapKey(src, maxWidth, maxHeight)]
    } catch(e: Exception) {
        throw IOException(e)
    }

    private data class BitmapKey(val src: String, val maxWidth: Float, val maxHeight: Float)
}