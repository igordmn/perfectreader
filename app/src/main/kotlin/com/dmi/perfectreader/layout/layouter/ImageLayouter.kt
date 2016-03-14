package com.dmi.perfectreader.layout.layouter

import android.graphics.Bitmap
import com.dmi.perfectreader.layout.LayoutImage
import com.dmi.perfectreader.layout.config.LayoutContext
import com.dmi.perfectreader.layout.config.LayoutSize
import com.dmi.perfectreader.layout.layouter.BitmapLoader.Companion.calculateInSampleSize
import com.dmi.perfectreader.render.RenderImage
import com.dmi.util.log.Log
import java.io.IOException

class ImageLayouter(private val bitmapLoader: BitmapLoader) : Layouter<LayoutImage, RenderImage> {
    companion object {
        private val errorBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    }

    override fun layout(obj: LayoutImage, context: LayoutContext): RenderImage {
        return object {
            fun layout(): RenderImage {
                try {
                    val (imageWidth, imageHeight) = bitmapLoader.loadDimensions(obj.src)
                    val (width, height) = computeDimensions(imageHeight, imageWidth)

                    val inSampleSize = calculateInSampleSize(imageWidth, imageHeight, width, height)
                    val bitmap = bitmapLoader.load(obj.src, inSampleSize)

                    return RenderImage(width, height, bitmap)
                } catch (e: IOException) {
                    Log.i(e, "image loading error: ${obj.src}")

                    val imageWidth = errorBitmap.width.toFloat()
                    val imageHeight = errorBitmap.height.toFloat()
                    val (width, height) = computeDimensions(imageHeight, imageWidth)

                    return RenderImage(width, height, errorBitmap)
                }
            }

            private fun computeDimensions(imageHeight: Float, imageWidth: Float): Pair<Float, Float> {
                val imageRatio = imageWidth / imageHeight

                val width: Float
                val height: Float

                if (isFixed(obj.size.width) && isFixed(obj.size.height)) {
                    width = obj.size.computeWidth(context, { imageWidth })
                    height = obj.size.computeHeight(context, { imageHeight })
                } else if (isFixed(obj.size.width)) {
                    width = obj.size.computeWidth(context, { imageWidth })
                    height = obj.size.computeHeight(context, { width / imageRatio })
                } else if (isFixed(obj.size.height)) {
                    height = obj.size.computeHeight(context, { imageHeight })
                    width = obj.size.computeWidth(context, { height * imageRatio })
                } else {
                    width = obj.size.computeWidth(context, { imageWidth })
                    height = obj.size.computeHeight(context, { width / imageRatio })
                }

                return Pair(width, height)
            }

            fun isFixed(value: LayoutSize.LimitedValue) = value.value !is LayoutSize.Value.WrapContent
        }.layout()
    }
}
