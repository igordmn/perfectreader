package com.dmi.perfectreader.fragment.book.obj.content

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.common.LayoutConfig
import com.dmi.perfectreader.fragment.book.obj.content.param.ContentSize
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutImage

class ContentImage(
        val size: ContentSize,
        val src: String?,
        range: LocationRange
) : ContentObject(range) {
    override val length = 32.0

    override fun configure(config: LayoutConfig) = LayoutImage(
            size.configure(),
            src,
            range
    )
}