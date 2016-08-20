package com.dmi.perfectreader.fragment.book.content.obj

import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredSize
import com.dmi.perfectreader.fragment.book.content.obj.param.ContentConfig
import com.dmi.perfectreader.fragment.book.content.obj.param.ContentSize
import com.dmi.perfectreader.fragment.book.location.LocationRange

class ContentImage(
        val size: ContentSize,
        val src: String?,
        textSize: Float?,
        range: LocationRange
) : ContentObject(range, textSize) {
    override val length = 32.0

    override fun configure(config: ContentConfig) = ConfiguredImage(
            size.configure(config),
            src,
            config.imageSourceScale,
            config.imageScaleFiltered,
            range
    )
}

class ConfiguredImage(
        val size: ConfiguredSize,
        val src: String?,
        val sourceScale: Float,
        val scaleFiltered: Boolean,
        range: LocationRange
) : ConfiguredObject(range)