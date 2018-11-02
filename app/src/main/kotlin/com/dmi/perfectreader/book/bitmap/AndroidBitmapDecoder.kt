package com.dmi.perfectreader.book.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dmi.util.graphic.Size
import com.google.common.io.ByteSource

class AndroidBitmapDecoder(
        private val resource: (path: String) -> ByteSource
) : BitmapDecoder {
    override fun loadSize(src: String): Size {
        resource(src).openStream().use {
            val bitmapOptions = BitmapFactory.Options()
            bitmapOptions.inJustDecodeBounds = true
            BitmapFactory.decodeStream(it, null, bitmapOptions)
            return Size(bitmapOptions.outWidth, bitmapOptions.outHeight)
        }
    }

    override fun decode(src: String, width: Int, height: Int, scaleFiltered: Boolean): Bitmap {
        val (factWidth, factHeight) = loadSize(src)
        resource(src).openStream().use {
            val bitmapOptions = BitmapFactory.Options()
            bitmapOptions.inSampleSize = calculateSampleSize(factWidth, factHeight, width, height)
            val bitmap = BitmapFactory.decodeStream(it, null, bitmapOptions)!!
            return Bitmap.createScaledBitmap(bitmap, width, height, scaleFiltered)
        }
    }

    private fun calculateSampleSize(factWidth: Int, factHeight: Int, desiredWidth: Int, desiredHeight: Int): Int {
        var sampleSize = 1

        if (factHeight > desiredHeight || factWidth > desiredWidth) {
            val halfHeight = factHeight / 2
            val halfWidth = factWidth / 2

            while ((halfWidth / sampleSize) > desiredWidth && (halfHeight / sampleSize) > desiredHeight) {
                sampleSize *= 2
            }
            if (sampleSize > 1)
                sampleSize /= 2
        }

        return sampleSize
    }
}