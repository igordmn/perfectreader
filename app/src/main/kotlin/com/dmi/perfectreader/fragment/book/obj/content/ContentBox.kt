package com.dmi.perfectreader.fragment.book.obj.content

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.common.Align
import com.dmi.perfectreader.fragment.book.obj.common.LayoutConfig
import com.dmi.perfectreader.fragment.book.obj.content.param.ContentSize
import com.dmi.perfectreader.fragment.book.obj.content.param.StyleType
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutBox

class ContentBox(
        val styleType: StyleType,
        val size: ContentSize,
        val contentAlign: Align?,
        val children: List<ContentObject>,
        range: LocationRange
) : ContentObject(range) {
    override val length = children.sumByDouble { it.length }

    override fun configure(config: LayoutConfig) = LayoutBox(
            size.configure(),
            contentAlign ?: Align.LEFT,
            children.map { it.configure(config) },
            range
    )
}