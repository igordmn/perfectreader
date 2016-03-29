package com.dmi.perfectreader.layout.image

import android.graphics.Bitmap
import com.dmi.util.libext.weakValuesCache
import java.io.IOException

class CachedBitmapLoader(private val bitmapLoader: BitmapLoader) : BitmapLoader {
    private val dimensions = weakValuesCache<String, BitmapLoader.Dimensions> {
        bitmapLoader.loadDimensions(it)
    }
    private val bitmaps = weakValuesCache<BitmapKey, Bitmap> {
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
