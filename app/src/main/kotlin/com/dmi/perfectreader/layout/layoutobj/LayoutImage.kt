package com.dmi.perfectreader.layout.layoutobj

import com.dmi.perfectreader.layout.layouter.common.LayoutSize
import com.dmi.perfectreader.location.BookRange

class LayoutImage(
        val size: LayoutSize,
        val src: String,
        range: BookRange
) : LayoutObject(range)
