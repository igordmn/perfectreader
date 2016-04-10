package com.dmi.perfectreader.layout.layoutobj

import com.dmi.perfectreader.layout.layouter.common.LayoutSize
import com.dmi.perfectreader.location.BookRange
import com.dmi.perfectreader.layout.layoutobj.common.Align

class LayoutBox(
        val size: LayoutSize,
        val contentAlign: Align,
        val children: List<LayoutObject>,
        range: BookRange
) : LayoutObject(range)
