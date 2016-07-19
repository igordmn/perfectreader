package com.dmi.perfectreader.fragment.book.layout.obj

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredFontStyle
import java.util.*

class LayoutSpaceText(width: Float,
                      height: Float,
                      text: CharSequence,
                      locale: Locale,
                      baseline: Float,
                      charOffsets: FloatArray,
                      style: ConfiguredFontStyle,
                      range: LocationRange
) : LayoutText(width, height, text, locale, baseline, charOffsets, style, range)