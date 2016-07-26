package com.dmi.perfectreader.fragment.book.content.obj

import com.dmi.perfectreader.fragment.book.content.obj.param.*
import com.dmi.perfectreader.fragment.book.location.LocationRange

class ContentBox(
        val styleType: StyleType,
        val size: ContentSize,
        val contentAlign: Align?,
        val children: List<ContentObject>,
        range: LocationRange
) : ContentObject(range) {
    override val length = children.sumByDouble { it.length }

    override fun configure(config: LayoutConfig) = ConfiguredBox(
            size.configure(config),
            contentAlign ?: Align.LEFT,
            children.map { it.configure(config) },
            range
    )
}

class ConfiguredBox(
        val size: ConfiguredSize,
        val contentAlign: Align,
        val children: List<ConfiguredObject>,
        range: LocationRange
) : ConfiguredObject(range)