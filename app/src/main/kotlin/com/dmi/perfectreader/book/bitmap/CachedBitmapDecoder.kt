package com.dmi.perfectreader.book.bitmap

import com.dmi.util.cache.cache
import java.io.IOException

class CachedBitmapDecoder(private val bitmapDecoder: BitmapDecoder) : BitmapDecoder {
    private val dimensions = cache(softValues = true) { path: String ->
        bitmapDecoder.loadDimensions(path)
    }
    private val bitmaps = cache(softValues = true) { key: BitmapKey ->
        bitmapDecoder.decode(key.src, key.width, key.height, key.scaleFiltered)
    }

    override fun loadDimensions(src: String) = try {
        dimensions[src]
    } catch(e: Exception) {
        throw IOException(e)
    }

    override fun decode(src: String, width: Int, height: Int, scaleFiltered: Boolean) = try {
        bitmaps[BitmapKey(src, width, height, scaleFiltered)]
    } catch(e: Exception) {
        throw IOException(e)
    }

    private data class BitmapKey(val src: String, val width: Int, val height: Int, val scaleFiltered: Boolean)
}