package com.dmi.perfectreader.layout.renderobj

import com.dmi.perfectreader.location.BookRange

class RenderLine(
        width: Float,
        height: Float,
        children: List<RenderChild>,
        range: BookRange
) : RenderObject(width, height, children, range)