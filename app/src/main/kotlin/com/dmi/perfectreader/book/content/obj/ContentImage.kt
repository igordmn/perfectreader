package com.dmi.perfectreader.book.content.obj

import com.dmi.perfectreader.book.content.obj.param.ConfiguredSize
import com.dmi.perfectreader.book.content.obj.param.FormatConfig
import com.dmi.perfectreader.book.content.obj.param.ContentSize
import com.dmi.perfectreader.book.content.location.LocationRange

class ContentImage(
        val size: ContentSize,
        val src: String?,
        textSize: Float?,
        range: LocationRange
) : ContentObject(range, textSize) {
    override val length = 32.0

    override fun configure(config: FormatConfig) = ConfiguredImage(
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