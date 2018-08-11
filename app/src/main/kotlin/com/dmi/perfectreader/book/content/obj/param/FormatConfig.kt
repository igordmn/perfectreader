package com.dmi.perfectreader.book.content.obj.param

import android.content.Context
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.perfectreader.settings.Settings
import com.dmi.util.font.FontCollection
import com.dmi.util.graphic.Color
import org.jetbrains.anko.displayMetrics
import java.util.*

class FormatConfig(
        val density: Float,

        val defaultLocale: Locale,
        val ignoreDeclaredLocale: Boolean,

        val pageTextGammaCorrection: Float,
        val pagePaddingsDip: Page.Paddings,

        val firstLineIndentEm: Float,
        val textAlign: TextAlign,
        val letterSpacingEm: Float,
        val wordSpacingMultiplier: Float,
        val lineHeightMultiplier: Float,
        val paragraphVerticalMarginEm: Float,
        val hangingConfig: HangingConfig,
        val hyphenation: Boolean,

        val textFontFamily: String,
        val textFontStyle: String,
        val textSizeDip: Float,
        val textScaleX: Float,
        val textSkewX: Float,
        val textStrokeWidthDip: Float,
        val textColor: Color,
        val textAntialiasing: Boolean,
        val textHinting: Boolean,
        val textSubpixelPositioning: Boolean,

        val textShadowEnabled: Boolean,
        val textShadowOffsetXDip: Float,
        val textShadowOffsetYDip: Float,
        val textShadowStrokeWidthDip: Float,
        val textShadowBlurRadiusDip: Float,
        val textShadowColor: Color,

        val selectionColor: Color,

        val imageSourceScale: Float,
        val imageScaleFiltered: Boolean,

        val fontCollection: FontCollection
)

fun appFormatConfig(context: Context, settings: Settings, fontCollection: FontCollection) = FormatConfig(
        context.displayMetrics.density,

        defaultLocale = defaultLocale(context, settings),
        ignoreDeclaredLocale = settings.analyze.ignoreDeclaredLanguage,

        pagePaddingsDip = Page.Paddings(
                settings.format.pagePaddingLeftDip,
                settings.format.pagePaddingRightDip,
                settings.format.pagePaddingTopDip,
                settings.format.pagePaddingBottomDip
        ),
        pageTextGammaCorrection = settings.format.pageGammaCorrection,

        firstLineIndentEm = settings.format.firstLineIndentEm,
        textAlign = settings.format.textAlign,
        letterSpacingEm = settings.format.letterSpacingEm,
        wordSpacingMultiplier = settings.format.wordSpacingMultiplier,
        lineHeightMultiplier = settings.format.lineHeightMultiplier,
        paragraphVerticalMarginEm = settings.format.paragraphVerticalMarginEm,
        hangingConfig = if (settings.format.hangingPunctuation) DefaultHangingConfig else NoneHangingConfig,
        hyphenation = settings.format.hyphenation,

        textFontFamily = settings.format.textFontFamily,
        textFontStyle = settings.format.textFontStyle,

        textSizeDip = settings.format.textSizeDip,
        textScaleX = settings.format.textScaleX,
        textSkewX = settings.format.textSkewX,
        textStrokeWidthDip = settings.format.textStrokeWidthDip,
        textColor = Color(settings.format.textColor),
        textAntialiasing = settings.format.textAntialiasing,
        textHinting = settings.format.textHinting,
        textSubpixelPositioning = settings.format.textSubpixelPositioning,

        textShadowEnabled = settings.format.textShadowEnabled,
        textShadowOffsetXDip = settings.format.textShadowOffsetXDip,
        textShadowOffsetYDip = settings.format.textShadowOffsetYDip,
        textShadowStrokeWidthDip = settings.format.textShadowStrokeWidthDip,
        textShadowBlurRadiusDip = settings.format.textShadowBlurRadiusDip,
        textShadowColor = Color(settings.format.textShadowColor),

        selectionColor = Color(settings.selection.color),

        imageSourceScale = if (settings.image.sourceScaleByDpi) context.displayMetrics.density else settings.image.sourceScale,
        imageScaleFiltered = settings.image.scaleFiltered,

        fontCollection = fontCollection
)

private fun defaultLocale(context: Context, settings: Settings) =
        if (settings.analyze.defaultLanguageIsSystem) {
            systemLocale(context)
        } else {
            Locale(settings.analyze.defaultLanguage)
        }

private fun systemLocale(context: Context) = Locale(context.resources.configuration.locale.language)