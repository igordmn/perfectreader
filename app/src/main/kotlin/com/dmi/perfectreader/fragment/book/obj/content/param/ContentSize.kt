package com.dmi.perfectreader.fragment.book.obj.content.param

import com.dmi.perfectreader.fragment.book.obj.common.Length
import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutSize
import java.io.Serializable

class ContentSize(val width: Dimension, val height: Dimension) : Serializable {
    fun configure() = LayoutSize(
            width.configure(),
            height.configure()
    )

    class Dimension(val value: Length?, val min: Length?, val max: Length?) {
        fun configure(): LayoutSize.Dimension {
            val limits = LayoutSize.Limits(
                    min ?: Length.Absolute(0F),
                    max ?: Length.Absolute(Float.MAX_VALUE)
            )
            return if (value == null) {
                LayoutSize.Dimension.Auto(limits)
            } else {
                LayoutSize.Dimension.Fixed(value, limits)
            }
        }
    }
}