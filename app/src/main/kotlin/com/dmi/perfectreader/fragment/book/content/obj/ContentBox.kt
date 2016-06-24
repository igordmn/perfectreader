package com.dmi.perfectreader.fragment.book.content.obj

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.content.obj.param.Align
import com.dmi.perfectreader.fragment.book.content.obj.param.LayoutConfig
import com.dmi.perfectreader.fragment.book.content.obj.param.ComputedSize
import com.dmi.perfectreader.fragment.book.content.obj.param.ContentSize
import com.dmi.perfectreader.fragment.book.content.obj.param.StyleType

class ContentBox(
        val styleType: StyleType,
        val size: ContentSize,
        val contentAlign: Align?,
        val children: List<ContentObject>,
        range: LocationRange
) : ContentObject(range) {
    override val length = children.sumByDouble { it.length }

    override fun configure(config: LayoutConfig) = ComputedBox(
            size.configure(),
            contentAlign ?: Align.LEFT,
            children.map { it.configure(config) },
            range
    )
}

class ComputedBox(
        val size: ComputedSize,
        val contentAlign: Align,
        val children: List<ComputedObject>,
        range: LocationRange
) : ComputedObject(range)