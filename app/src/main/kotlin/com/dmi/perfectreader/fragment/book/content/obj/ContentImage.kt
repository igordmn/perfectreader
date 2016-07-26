package com.dmi.perfectreader.fragment.book.content.obj

import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredSize
import com.dmi.perfectreader.fragment.book.content.obj.param.ContentSize
import com.dmi.perfectreader.fragment.book.content.obj.param.LayoutConfig
import com.dmi.perfectreader.fragment.book.location.LocationRange

class ContentImage(
        val size: ContentSize,
        val src: String?,
        range: LocationRange
) : ContentObject(range) {
    override val length = 32.0

    override fun configure(config: LayoutConfig) = ConfiguredImage(
            size.configure(config),
            src,
            range
    )
}

class ConfiguredImage(
        val size: ConfiguredSize,
        val src: String?,
        range: LocationRange
) : ConfiguredObject(range)