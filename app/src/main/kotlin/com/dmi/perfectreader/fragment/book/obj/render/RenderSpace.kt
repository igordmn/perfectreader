package com.dmi.perfectreader.fragment.book.obj.render

import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.layout.param.LayoutFontStyle
import java.util.*

class RenderSpace(width: Float,
                  height: Float,
                  text: CharSequence,
                  locale: Locale,
                  baseline: Float,
                  style: LayoutFontStyle,
                  val scaleX: Float,
                  range: LocationRange
) : RenderText(width, height, text, locale, baseline, style, range)