package com.dmi.perfectreader

import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredFontStyle
import com.dmi.util.graphic.Color

fun fontStyle() = ConfiguredFontStyle(
        size = 12F,
        scaleX = 1.0F,
        skewX = 0F,
        strokeWidth = 0F,
        color = Color.BLACK,
        antialiasing = true,
        hinting = true,
        subpixelPositioning = true,
        textShadowEnabled = false,
        shadowOffsetX = 0F,
        shadowOffsetY = 0F,
        shadowStrokeWidth = 0F,
        shadowBlurRadius = 0F,
        shadowColor = Color.BLACK,
        selectionColor = Color.RED
)