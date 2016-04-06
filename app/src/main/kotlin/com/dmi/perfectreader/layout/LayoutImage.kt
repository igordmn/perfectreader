package com.dmi.perfectreader.layout

import com.dmi.perfectreader.layout.common.LayoutSize
import com.dmi.perfectreader.location.BookRange

class LayoutImage(
        val size: LayoutSize,
        val src: String,
        range: BookRange
) : LayoutObject(range)
