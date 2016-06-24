package com.dmi.perfectreader.fragment.book.content.obj

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.content.obj.param.LayoutConfig
import com.dmi.perfectreader.fragment.book.content.obj.param.ComputedSize
import com.dmi.perfectreader.fragment.book.content.obj.param.ContentSize

class ContentImage(
        val size: ContentSize,
        val src: String?,
        range: LocationRange
) : ContentObject(range) {
    override val length = 32.0

    override fun configure(config: LayoutConfig) = ComputedImage(
            size.configure(),
            src,
            range
    )
}

class ComputedImage(
        val size: ComputedSize,
        val src: String?,
        range: LocationRange
) : ComputedObject(range)