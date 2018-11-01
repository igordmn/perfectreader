package com.dmi.perfectreader.book.content.obj.common

import com.dmi.perfectreader.book.content.common.HangingConfig
import com.dmi.perfectreader.book.content.common.TextAlign
import com.dmi.util.graphic.Color

data class ContentStyle(
        val margins: ContentMargins,
        val firstLineIndentEm: Float,

        val textAlign: TextAlign,
        val letterSpacingEm: Float,
        val wordSpacingMultiplier: Float,
        val lineHeightMultiplier: Float,
        val hangingConfig: HangingConfig,
        val hyphenation: Boolean,

        val textFontFamily: String,
        val textFontIsBold: Boolean,
        val textFontIsItalic: Boolean,
        val textSizeDip: Float,
        val textScaleX: Float,
        val textSkewX: Float,
        val textStrokeWidthDip: Float,
        val textColor: Color,
        val textAntialiasing: Boolean,
        val textHinting: Boolean,
        val textSubpixelPositioning: Boolean,

        val textShadowEnabled: Boolean,
        val textShadowAngleDegrees: Float,
        val textShadowOffsetEm: Float,
        val textShadowSizeEm: Float,
        val textShadowBlurEm: Float,
        val textShadowColor: Color,

        val selectionColor: Color
)