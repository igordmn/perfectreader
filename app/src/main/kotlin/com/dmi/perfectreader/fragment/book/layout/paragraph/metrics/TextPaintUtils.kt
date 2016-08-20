package com.dmi.perfectreader.fragment.book.layout.paragraph.metrics

import android.graphics.Paint
import android.os.Build
import android.text.TextPaint
import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredFontStyle

fun configureTextPaint(paint: TextPaint, style: ConfiguredFontStyle) {
    paint.textSize = style.size
    paint.textScaleX = style.scaleX
    paint.textSkewX = style.skewX
    paint.strokeWidth = style.strokeWidth
    paint.style = if (style.strokeWidth == 0F) Paint.Style.FILL else Paint.Style.FILL_AND_STROKE
    paint.color = style.color.value
    paint.isAntiAlias = style.antialiasing
    paint.isSubpixelText = style.subpixelPositioning
    paint.hinting = if (style.hinting) Paint.HINTING_ON else Paint.HINTING_OFF
    paint.isLinearText = false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        paint.letterSpacing = style.letterSpacing / style.size
    }
}

fun configureTextShadowPaint(paint: TextPaint, style: ConfiguredFontStyle) {
    paint.textSize = style.size
    paint.textScaleX = style.scaleX
    paint.textSkewX = style.skewX
    paint.strokeWidth = style.strokeWidth + style.shadowStrokeWidth
    paint.style = if (style.strokeWidth + style.shadowStrokeWidth == 0F) Paint.Style.FILL else Paint.Style.FILL_AND_STROKE
    paint.color = style.shadowColor.value
    paint.isAntiAlias = style.antialiasing
    paint.isSubpixelText = style.subpixelPositioning
    paint.hinting = if (style.hinting) Paint.HINTING_ON else Paint.HINTING_OFF
    paint.isLinearText = false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        paint.letterSpacing = style.letterSpacing / style.size
    }
}