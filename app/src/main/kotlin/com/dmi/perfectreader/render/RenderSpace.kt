package com.dmi.perfectreader.render

import android.graphics.Canvas

import com.dmi.perfectreader.style.FontStyle

import java.util.Locale

class RenderSpace(width: Float, height: Float, text: CharSequence, locale: Locale, baseline: Float, private val scaleX: Float, style: FontStyle) : RenderText(width, height, text, locale, baseline, style) {

    fun scaleX(): Float {
        return scaleX
    }

    override fun paintItself(canvas: Canvas) {
    }
}
