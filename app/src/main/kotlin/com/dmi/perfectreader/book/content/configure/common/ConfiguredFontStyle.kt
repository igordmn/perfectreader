package com.dmi.perfectreader.book.content.configure.common

import com.dmi.util.font.StyledFont
import com.dmi.util.graphic.Color

class ConfiguredFontStyle(
        val styledFont: StyledFont,

        val size: Float,
        val letterSpacing: Float,
        val wordSpacingMultiplier: Float,
        val scaleX: Float,
        val skewX: Float,
        val strokeWidth: Float,
        val color: Color,
        val antialiasing: Boolean,
        val hinting: Boolean,
        val subpixelPositioning: Boolean,

        val shadowEnabled: Boolean,
        val shadowAngle: Float,
        val shadowOffset: Float,
        val shadowStrokeWidth: Float,
        val shadowBlurRadius: Float,
        val shadowColor: Color,

        val selectionColor: Color
)