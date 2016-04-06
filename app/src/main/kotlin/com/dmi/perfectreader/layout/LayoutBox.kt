package com.dmi.perfectreader.layout

import com.dmi.perfectreader.layout.common.LayoutSize
import com.dmi.perfectreader.location.BookRange
import com.dmi.perfectreader.style.Align

class LayoutBox(
        val size: LayoutSize,
        val contentAlign: Align,
        val children: List<LayoutObject>,
        range: BookRange
) : LayoutObject(range)
