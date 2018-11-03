package com.dmi.perfectreader.settings

import com.dmi.perfectreader.book.content.common.TextAlign
import com.dmi.util.graphic.Color
import com.dmi.util.persist.ValueStore
import com.dmi.util.persist.value

class FormatSettings(store: ValueStore) {
    var textAlign by store.value(TextAlign.JUSTIFY)
    var letterSpacingEm by store.value(0F)
    var wordSpacingMultiplier by store.value(1F)
    var lineHeightMultiplier by store.value(1F)
    var paragraphFirstLineIndentEm by store.value(1F)
    var paragraphVerticalMarginEm by store.value(0.5F)
    var hangingPunctuation by store.value(true)
    var hyphenation by store.value(true)

    var textFontFamily by store.value("")
    var textFontIsBold by store.value(false)
    var textFontIsItalic by store.value(false)
    var textSizeDip by store.value(20F)
    var textScaleX by store.value(1.0F)
    var textSkewX by store.value(0.0F)
    var textStrokeWidthDip by store.value(0.0F)
    var textColor by store.value(Color.BLACK.value)
    var textAntialiasing by store.value(true)
    var textHinting by store.value(true)
    var textSubpixelPositioning by store.value(true)

    var textShadowEnabled by store.value(false)
    var textShadowAngleDegrees by store.value(0F)
    var textShadowOffsetEm by store.value(0F)
    var textShadowSizeEm by store.value(0.1F)
    var textShadowBlurEm by store.value(0.05F)
    var textShadowColor by store.value(Color.GRAY.withAlpha(128).value)

    var pageAnimationPath by store.value("assets:///resources/animations/curl.xml")
    var pageBackgroundIsImage by store.value(false)
    var pageBackgroundColor by store.value(Color.WHITE.value)
    var pageBackgroundPath by store.value("assets:///resources/backgrounds/0004.png")
    var pageBackgroundContentAwareResize by store.value(true)
    var pageTextGammaCorrection by store.value(1F)
    var pagePaddingLeftDip by store.value(20F)
    var pagePaddingRightDip by store.value(20F)
    var pagePaddingTopDip by store.value(20F)
    var pagePaddingBottomDip by store.value(20F)

    var pageFooter by store.value(true)
    var pageFooterTextSizePercent by store.value(0.8F)
    var pageFooterHeightEm by store.value(1.4F)
    var pageFooterPaddingBottomPercent by store.value(-0.2F)
    var pageFooterPageNumber by store.value(true)
    var pageFooterNumberOfPages by store.value(true)
    var pageFooterChapter by store.value(true)
}