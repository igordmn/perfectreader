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
    override fun layout(obj: ConfiguredImage, space: LayoutSpace): LayoutImage = object {
        fun layout(): LayoutImage {
            val sourceSize = if (obj.src != null) {
                try {
                    bitmapDecoder.loadSize(obj.src).scaled()
                } catch (e: IOException) {
                    Size(1, 1)
                }
            } else {
                Size(1, 1)
            }
            val (width, height) = sourceSize.factSize()
            val filter = if (width < sourceSize.width || height < sourceSize.height) obj.scale.decFilter else obj.scale.incFilter
            return LayoutImage(width.toFloat(), height.toFloat(), width, height, filter, obj.src, obj.range)
        }

        fun Size.scaled(): Size = (this * obj.scale.value).toInt()

        fun Size.factSize(): Size {
            val source = this
            with(obj.size) {
                val imageRatio = source.width / source.height.toFloat()
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
                        factWidth = width.compute(source.width.toFloat(), space.width.percentBase)
                        factHeight = height.compute(factWidth / imageRatio, space.height.percentBase)
                    }
                    else -> throw IllegalStateException()
                }

                return Size(round(factWidth), round(factHeight))
            }
        }
    }.layout()
}