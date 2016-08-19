package com.dmi.perfectreader.fragment.book.content.obj.param

import com.dmi.util.graphic.Color
import java.io.Serializable

class ContentFontStyle(
        val size: Float?,
        val color: Color?
) : Serializable

class ConfiguredFontStyle(
        val size: Float,
        val letterSpacing: Float,
        val scaleX: Float,
        val skewX: Float,
        val strokeWidth: Float,
        val color: Color,
        val antialiasing: Boolean,
        val hinting: Boolean,
        val subpixelPositioning: Boolean,

        val textShadowEnabled: Boolean,
        val shadowOffsetX: Float,
        val shadowOffsetY: Float,
        val shadowStrokeWidth: Float,
        val shadowBlurRadius: Float,
        val shadowColor: Color,

        val selectionColor: Color
)