package com.dmi.perfectreader.book.content.obj

import com.dmi.perfectreader.book.content.configure.ConfiguredImage
import com.dmi.perfectreader.book.content.configure.common.ConfiguredSize
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.obj.common.ContentConfig

class ContentImage(
        val src: String?,
        override val range: LocationRange
) : ContentObject {
    override val length = 32.0

    override fun configure(config: ContentConfig): ConfiguredImage {
        return ConfiguredImage(
                ConfiguredSize(ConfiguredSize.Dimension.Auto(), ConfiguredSize.Dimension.Auto()),
                src,
                config.imageSourceScale,
                config.imageScaleFiltered,
                range
        )
    }
}