package com.dmi.perfectreader.fragment.book.obj.layout

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.content.param.ComputedFontStyle
import java.util.*

class LayoutSpaceText(width: Float,
                      height: Float,
                      text: CharSequence,
                      locale: Locale,
                      baseline: Float,
                      charOffsets: FloatArray,
                      style: ComputedFontStyle,
                      range: LocationRange
) : LayoutText(width, height, text, locale, baseline, charOffsets, style, range)