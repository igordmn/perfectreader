package com.dmi.perfectreader.layout.image

import android.graphics.Bitmap
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import java.io.IOException

class CachedBitmapLoader(private val bitmapLoader: BitmapLoader) : BitmapLoader {
    private val SIZE = 30L

    private val dimensions = CacheBuilder.newBuilder()
            .maximumSize(SIZE)
            .weakValues()
            .build(
                    CacheLoader.from<String, BitmapLoader.Dimensions> {
                        bitmapLoader.loadDimensions(it!!)
                    }
            );
    private val bitmaps = CacheBuilder.newBuilder()
            .maximumSize(SIZE)
            .weakValues()
            .build(
                    CacheLoader.from<BitmapKey, Bitmap> {
                        bitmapLoader.load(it!!.src, it.inSampleSize)
                    }
            );

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
