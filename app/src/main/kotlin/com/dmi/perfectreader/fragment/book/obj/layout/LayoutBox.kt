package com.dmi.perfectreader.fragment.book.obj.layout

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.common.Align
import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutSize

class LayoutBox(
        val size: LayoutSize,
        val contentAlign: Align,
        val children: List<LayoutObject>,
        range: LocationRange
) : LayoutObject(range)