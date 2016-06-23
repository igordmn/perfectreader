package com.dmi.perfectreader.fragment.book.obj.render

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutFontStyle
import java.util.*

class RenderSpace(width: Float,
                  height: Float,
                  text: CharSequence,
                  locale: Locale,
                  baseline: Float,
                  charOffsets: FloatArray,
                  style: LayoutFontStyle,
                  range: LocationRange
) : RenderText(width, height, text, locale, baseline, charOffsets, style, range)