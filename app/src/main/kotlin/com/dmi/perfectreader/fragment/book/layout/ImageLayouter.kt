package com.dmi.perfectreader.fragment.book.layout

import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder
import com.dmi.perfectreader.fragment.book.layout.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.content.obj.ComputedImage
import com.dmi.perfectreader.fragment.book.content.obj.param.ComputedSize.Dimension.Auto
import com.dmi.perfectreader.fragment.book.content.obj.param.ComputedSize.Dimension.Fixed
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutImage
import com.dmi.util.graphic.SizeF

class ImageLayouter(
        private val bitmapDecoder: BitmapDecoder
) : ObjectLayouter<ComputedImage, LayoutImage> {
    override fun layout(obj: ComputedImage, space: LayoutSpace): LayoutImage {
        return object {
            fun layout(): LayoutImage {
                val (imageWidth, imageHeight) = if (obj.src != null) bitmapDecoder.loadDimensions(obj.src) else SizeF(0F, 0F)
                val (width, height) = computeDimensions(imageHeight, imageWidth)
                return LayoutImage(width, height, obj.range, obj.src)
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