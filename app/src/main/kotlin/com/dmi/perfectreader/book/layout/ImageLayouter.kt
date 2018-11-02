package com.dmi.perfectreader.book.layout

import com.dmi.perfectreader.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.book.content.configure.ConfiguredImage
import com.dmi.perfectreader.book.content.configure.common.ConfiguredSize.Dimension.Auto
import com.dmi.perfectreader.book.content.configure.common.ConfiguredSize.Dimension.Fixed
import com.dmi.perfectreader.book.layout.common.LayoutSpace
import com.dmi.perfectreader.book.layout.obj.LayoutImage
import com.dmi.util.graphic.Size
import kotlinx.io.IOException
import java.lang.Math.round

class ImageLayouter(
        private val bitmapDecoder: BitmapDecoder
) : ObjectLayouter<ConfiguredImage, LayoutImage> {
    override fun layout(obj: ConfiguredImage, space: LayoutSpace): LayoutImage {
        return object {
            fun layout(): LayoutImage {
                val (imageWidth, imageHeight) = if (obj.src != null) {
                    try {
                        (bitmapDecoder.loadDimensions(obj.src) * obj.sourceScale).toInt()
                    } catch (e: IOException) {
                        Size(1, 1)
                    }
                } else {
                    Size(1, 1)
                }
                val (width, height) = configureDimensions(imageHeight, imageWidth)
                return LayoutImage(width.toFloat(), height.toFloat(), width, height, obj.scaleFiltered, obj.src, obj.range)
            }

            private fun configureDimensions(imageHeight: Int, imageWidth: Int): Size {
                with (obj.size) {
                    val imageRatio = imageWidth / imageHeight.toFloat()
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
                            factWidth = width.compute(imageWidth.toFloat(), space.width.percentBase)
                            factHeight = height.compute(factWidth / imageRatio, space.height.percentBase)
                        }
                        else -> throw IllegalStateException()
                    }

                    return Size(round(factWidth), round(factHeight))
                }
            }
        }.layout()
    }
}