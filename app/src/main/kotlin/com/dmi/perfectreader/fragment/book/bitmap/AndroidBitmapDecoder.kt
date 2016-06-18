package com.dmi.perfectreader.fragment.book.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dmi.util.graphic.SizeF
import java.io.InputStream

class AndroidBitmapDecoder(private val openResource: (path: String) -> InputStream) : BitmapDecoder {
    override fun loadDimensions(src: String): SizeF {
        openResource(src).use {
            val bitmapOptions = BitmapFactory.Options()
            bitmapOptions.inJustDecodeBounds = true
            BitmapFactory.decodeStream(it, null, bitmapOptions)
            return SizeF(
                    bitmapOptions.outWidth.toFloat(),
                    bitmapOptions.outHeight.toFloat()
            )
        }
    }

    override fun decode(src: String, maxWidth: Float, maxHeight: Float): Bitmap {
        val (factWidth, factHeight) = loadDimensions(src)
        openResource(src).use {
            val bitmapOptions = BitmapFactory.Options()
            bitmapOptions.inSampleSize = calculateSampleSize(factWidth, factHeight, maxWidth, maxHeight)
            return BitmapFactory.decodeStream(it, null, bitmapOptions)
        }
    }

    private fun calculateSampleSize(factWidth: Float, factHeight: Float, desiredMaxWidth: Float, desiredMaxHeight: Float): Int {
        var sampleSize = 1

        if (factHeight > desiredMaxHeight || factWidth > desiredMaxWidth) {
            val halfHeight = factHeight / 2
            val halfWidth = factWidth / 2

            while ((halfWidth / sampleSize) > desiredMaxWidth &&
                   (halfHeight / sampleSize) > desiredMaxHeight
            ) {
                sampleSize *= 2
            }
        }

        return sampleSize
    }
}