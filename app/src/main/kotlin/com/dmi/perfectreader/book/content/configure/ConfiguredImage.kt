package com.dmi.perfectreader.book.content.configure

import com.dmi.perfectreader.book.content.configure.common.ConfiguredSize
import com.dmi.perfectreader.book.content.location.LocationRange

class ConfiguredImage(
        val size: ConfiguredSize,
        val src: String?,
        val scale: Scale,
        override val range: LocationRange
) : ConfiguredObject {
    override fun toString() = "<img:$src>"

    class Scale(
            val value: Float,
            val incFilter: Boolean,
            val decFilter: Boolean
    )
}