package com.dmi.perfectreader.layout.renderobj

import android.graphics.Canvas
import com.dmi.perfectreader.location.BookRange
import com.dmi.perfectreader.layout.layoutobj.common.FontStyle
import java.util.*

class RenderSpace(width: Float,
                  height: Float,
                  text: CharSequence,
                  locale: Locale,
                  baseline: Float,
                  style: FontStyle,
                  val scaleX: Float,
                  range: BookRange
) : RenderText(width, height, text, locale, baseline, style, range) {
    override fun paintItself(canvas: Canvas) = Unit
}
