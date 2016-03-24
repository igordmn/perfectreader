package com.dmi.perfectreader.layout.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dmi.perfectreader.layout.common.ResourceLoader

class DecodeBitmapLoader(private val resourceLoader: ResourceLoader) : BitmapLoader {
    override fun loadDimensions(src: String): BitmapLoader.Dimensions {
        resourceLoader.load(src).use {
            val bitmapOptions = BitmapFactory.Options()
            bitmapOptions.inJustDecodeBounds = true
            BitmapFactory.decodeStream(it, null, bitmapOptions)
            return BitmapLoader.Dimensions(
                    bitmapOptions.outWidth.toFloat(),
                    bitmapOptions.outHeight.toFloat()
            )
        }
    }

    override fun load(src: String, inSampleSize: Int): Bitmap {
        resourceLoader.load(src).use {
            val bitmapOptions = BitmapFactory.Options()
            bitmapOptions.inSampleSize = inSampleSize
            return BitmapFactory.decodeStream(it, null, bitmapOptions)
        }
    }
}
