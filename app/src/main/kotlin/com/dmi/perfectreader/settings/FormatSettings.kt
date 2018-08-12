package com.dmi.perfectreader.settings

import com.dmi.perfectreader.book.content.obj.param.TextAlign
import com.dmi.util.graphic.Color
import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class FormatSettings(store: ValueStore) {
    var firstLineIndentEm by store.value(1F)
    var textAlign by store.value(TextAlign.JUSTIFY)
    var letterSpacingEm by store.value(0F)
    var wordSpacingMultiplier by store.value(1F)
    var lineHeightMultiplier by store.value(1F)
    var paragraphVerticalMarginEm by store.value(0.5F)
    var hangingPunctuation by store.value(true)
    var hyphenation by store.value(true)

    var textFontFamily by store.value("")
    var textFontStyle by store.value("Regular")
    var textSizeDip by store.value(20F)
    var textScaleX by store.value(1.0F)
    var textSkewX by store.value(0.0F)
    var textStrokeWidthDip by store.value(0.0F)
    var textColor by store.value(Color.BLACK.value)
    var textAntialiasing by store.value(true)
    var textHinting by store.value(true)
    var textSubpixelPositioning by store.value(true)

    var textShadowEnabled by store.value(false)
    var textShadowOffsetXDip by store.value(0F)
    var textShadowOffsetYDip by store.value(0F)
    var textShadowStrokeWidthDip by store.value(0F)
    var textShadowBlurRadiusDip by store.value(1F)
    var textShadowColor by store.value(Color.GRAY.value)

    var pageAnimationPath by store.value("assets:///resources/animations/curl.xml")
    var pageBackgroundIsColor by store.value(false)
    var pageBackgroundColor by store.value(Color.WHITE.value)
    var pageBackgroundPath by store.value("assets:///resources/backgrounds/0004.png")
    var pageGammaCorrection by store.value(1F)
    var pagePaddingLeftDip by store.value(20F)
    var pagePaddingRightDip by store.value(20F)
    var pagePaddingTopDip by store.value(20F)
    var pagePaddingBottomDip by store.value(20F)
}