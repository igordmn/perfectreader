package com.dmi.perfectreader.fragment.book.obj.render

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutFontStyle
import java.util.*
import java.util.Collections.emptyList

open class RenderText(width: Float,
                      height: Float,
                      val text: CharSequence,
                      val locale: Locale,
                      val baseline: Float,
                      val style: LayoutFontStyle,
                      range: LocationRange
) : RenderObject(width, height, emptyList<RenderChild>(), range)