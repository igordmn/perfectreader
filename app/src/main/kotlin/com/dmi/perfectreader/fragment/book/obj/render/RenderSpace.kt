package com.dmi.perfectreader.fragment.book.obj.render

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.content.param.ComputedFontStyle
import java.util.*

class RenderSpace(width: Float,
                  height: Float,
                  text: CharSequence,
                  locale: Locale,
                  baseline: Float,
                  charOffsets: FloatArray,
                  style: ComputedFontStyle,
                  range: LocationRange
) : RenderText(width, height, text, locale, baseline, charOffsets, style, range)