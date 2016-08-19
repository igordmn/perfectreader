package com.dmi.perfectreader

import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredFontStyle
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.util.graphic.Color

fun fontStyle(wordSpacingMultiplier: Float = 1F) = ConfiguredFontStyle(
        size = 12F,
        letterSpacingEm = 0F,
        wordSpacingMultiplier = wordSpacingMultiplier,
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


fun range(intRange: IntRange) = range(intRange.first, intRange.last)
fun range(beginIndex: Int = 0, endIndex: Int = 0) = LocationRange(location(beginIndex), location(endIndex))
fun location(index: Int = 0) = Location(index.toDouble())

fun layoutObj(width: Float = 0F, height: Float= 0F) = object : LayoutObject(width, height, emptyList(), range()) {}