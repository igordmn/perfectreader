package com.dmi.perfectreader.layout.layouter.image

import android.graphics.Bitmap
import com.dmi.util.libext.cache
import java.io.IOException

class CachedBitmapLoader(private val bitmapLoader: BitmapLoader) : BitmapLoader {
    private val dimensions = cache<String, BitmapLoader.Dimensions>(softValues = true) {
        bitmapLoader.loadDimensions(it)
    }
    private val bitmaps = cache<BitmapKey, Bitmap>(softValues = true) {
        bitmapLoader.load(it.src, it.inSampleSize)
    }

    override fun loadDimensions(src: String) = try {
        dimensions[src]
    } catch(e: Exception) {
        throw IOException(e)
    }

    override fun load(src: String, inSampleSize: Int) = try {
        bitmaps[BitmapKey(src, inSampleSize)]
    } catch(e: Exception) {
        throw IOException(e)
    }

    private data class BitmapKey(val src: String, val inSampleSize: Int)
}
