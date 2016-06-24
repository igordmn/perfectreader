package com.dmi.perfectreader.fragment.book.obj.layout

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.content.param.ComputedFontStyle
import java.util.*
import java.util.Collections.emptyList

open class LayoutText(width: Float,
                      height: Float,
                      val text: CharSequence,
                      val locale: Locale,
                      val baseline: Float,
                      val charOffsets: FloatArray,
                      val style: ComputedFontStyle,
                      range: LocationRange
) : LayoutObject(width, height, emptyList<LayoutChild>(), range) {
    init {
        require(text.length == charOffsets.size)
    }
}