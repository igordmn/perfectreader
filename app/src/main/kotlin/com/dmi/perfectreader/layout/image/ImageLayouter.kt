package com.dmi.perfectreader.layout.image

import android.graphics.Bitmap
import com.dmi.perfectreader.layout.LayoutImage
import com.dmi.perfectreader.layout.common.LayoutSize.Dimension.Auto
import com.dmi.perfectreader.layout.common.LayoutSize.Dimension.Fixed
import com.dmi.perfectreader.layout.common.LayoutSpace
import com.dmi.perfectreader.layout.common.Layouter
import com.dmi.perfectreader.layout.image.BitmapLoader.Companion.calculateInSampleSize
import com.dmi.perfectreader.render.RenderImage
import com.dmi.util.log.Log
import java.io.IOException

class ImageLayouter(private val bitmapLoader: BitmapLoader) : Layouter<LayoutImage, RenderImage> {
    companion object {
        private val errorBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    }

    override fun layout(obj: LayoutImage, space: LayoutSpace): RenderImage {
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
                with (obj.size) {
                    val imageRatio = imageWidth / imageHeight
                    val factWidth: Float
                    val factHeight: Float

                    when {
                        width is Fixed && height is Fixed -> {
                            factWidth = width.compute(space.width.percentBase)
                            factHeight = height.compute(space.height.percentBase)
                        }
                        width is Fixed && height is Auto -> {
                            factWidth = width.compute(space.width.percentBase)
                            factHeight = height.compute(factWidth / imageRatio, space.height.percentBase)
                        }
                        width is Auto && height is Fixed -> {
                            factHeight = height.compute(space.height.percentBase)
                            factWidth = width.compute(factHeight * imageRatio, space.width.percentBase)
                        }
                        width is Auto && height is Auto -> {
                            factWidth = width.compute(imageWidth, space.width.percentBase)
                            factHeight = height.compute(factWidth / imageRatio, space.height.percentBase)
                        }
                        else -> throw IllegalStateException()
                    }

                    return Pair(factWidth, factHeight)
                }
            }
        }.layout()
    }
}
