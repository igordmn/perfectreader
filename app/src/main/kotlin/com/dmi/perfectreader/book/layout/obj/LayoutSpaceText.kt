package com.dmi.perfectreader.book.layout.obj

import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.configure.common.ConfiguredFontStyle
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