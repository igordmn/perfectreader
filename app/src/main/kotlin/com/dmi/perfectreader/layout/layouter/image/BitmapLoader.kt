package com.dmi.perfectreader.layout.layouter.image

import android.graphics.Bitmap

interface BitmapLoader {
    companion object {
        fun calculateInSampleSize(imageWidth: Float, imageHeight: Float, maxWidth: Float, maxHeight: Float): Int {
            var inSampleSize = 1

            if (imageHeight > maxHeight || imageWidth > maxWidth) {
                val halfHeight = imageHeight / 2
                val halfWidth = imageWidth / 2

                while ((halfWidth / inSampleSize) > maxWidth &&
                       (halfHeight / inSampleSize) > maxHeight) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }
    }

    fun loadDimensions(src: String): Dimensions
    fun load(src: String, inSampleSize: Int = 1): Bitmap

    data class Dimensions(val width: Float, val height: Float)
}
