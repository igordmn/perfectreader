package com.dmi.perfectreader.fragment.book.obj.render

import com.dmi.perfectreader.fragment.book.location.LocationRange

class RenderLine(
        width: Float,
        height: Float,
        children: List<RenderChild>,
        range: LocationRange
) : RenderObject(width, height, children, range)