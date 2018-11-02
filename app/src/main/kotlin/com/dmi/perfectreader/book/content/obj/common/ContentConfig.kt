package com.dmi.perfectreader.book.content.obj.common

import android.content.Context
import com.dmi.perfectreader.Main
import com.dmi.perfectreader.book.content.common.DefaultHangingConfig
import com.dmi.perfectreader.book.content.common.NoneHangingConfig
import com.dmi.perfectreader.book.content.common.TextAlign
import com.dmi.perfectreader.book.content.obj.ContentImage
import com.dmi.perfectreader.settings.AnalyzeSettings
import com.dmi.perfectreader.settings.ImageSettings
import com.dmi.perfectreader.settings.Settings
import com.dmi.util.cache.Cache
import com.dmi.util.cache.cache
import com.dmi.util.font.Fonts
import com.dmi.util.graphic.Color
import org.jetbrains.anko.displayMetrics
import java.util.*

data class ContentConfig(
        val density: Float,
        val fonts: Fonts,
        private val defaultLocale: Locale,
        private val ignoreDeclaredLocale: Boolean,
        val imageScale: ContentImage.Scale,
        private val defaultStyle: ContentStyle
) {
    constructor(
            main: Main,
            context: Context = main.applicationContext,
            settings: Settings = main.settings
    ) : this(
            density = main.applicationContext.displayMetrics.density,
            fonts = main.resources.fonts,
            defaultLocale = defaultLocale(context, settings.analyze),
            ignoreDeclaredLocale = settings.analyze.ignoreDeclaredLanguage,
            imageScale = imageScale(settings.image),
            defaultStyle = ContentStyle(
                    margins = ContentMargins(
                            ContentLength.Percent(0F),
                            ContentLength.Percent(0F),
                            ContentLength.Em(settings.format.paragraphVerticalMarginEm),
                            ContentLength.Em(settings.format.paragraphVerticalMarginEm)
                    ),
                    pageBreakBefore = false,
                    firstLineIndentEm = settings.format.paragraphFirstLineIndentEm,

                    textAlign = settings.format.textAlign,
                    letterSpacingEm = settings.format.letterSpacingEm,
                    wordSpacingMultiplier = settings.format.wordSpacingMultiplier,
                    lineHeightMultiplier = settings.format.lineHeightMultiplier,
                    hangingConfig = if (settings.format.hangingPunctuation) DefaultHangingConfig else NoneHangingConfig,
                    hyphenation = settings.format.hyphenation,

                    textFontFamily = settings.format.textFontFamily,
                    textFontIsBold = settings.format.textFontIsBold,
                    textFontIsItalic = settings.format.textFontIsItalic,

                    textSizeDip = settings.format.textSizeDip,
                    textScaleX = settings.format.textScaleX,
                    textSkewX = settings.format.textSkewX,
                    textStrokeWidthDip = settings.format.textStrokeWidthDip,
                    textColor = Color(settings.format.textColor),
                    textAntialiasing = settings.format.textAntialiasing,
                    textHinting = settings.format.textHinting,
                    textSubpixelPositioning = settings.format.textSubpixelPositioning,

                    textShadowEnabled = settings.format.textShadowEnabled,
                    textShadowAngleDegrees = settings.format.textShadowAngleDegrees,
                    textShadowOffsetEm = settings.format.textShadowOffsetEm,
                    textShadowSizeEm = settings.format.textShadowSizeEm,
                    textShadowBlurEm = settings.format.textShadowBlurEm,
                    textShadowColor = Color(settings.format.textShadowColor),

                    selectionColor = Color(settings.selection.color)
            )
    )

    fun locale(declared: Locale?) = if (ignoreDeclaredLocale || declared == null) defaultLocale else declared

    fun style(cls: ContentCompositeClass?): ContentStyle = cachedStyle(cls)
    private fun cachedStyle(cls: ContentCompositeClass?) = cls?.let(cachedStyle::get) ?: defaultStyle
    private val cachedStyle: Cache<ContentCompositeClass, ContentStyle> = cache(load = ::computeStyle)
    private fun computeStyle(cls: ContentCompositeClass) = cachedStyle(cls.parent).apply(cls.cls)

    private fun ContentStyle.apply(cls: ContentClass) = when (cls) {
        ContentClass.STRONG -> copy(textFontIsBold = true)
        ContentClass.EMPHASIS -> copy(textFontIsItalic = true)
        ContentClass.H0 -> copy(
                firstLineIndentEm = 0F,
                margins = margins multiplyHorizontal 1.34F,
                textFontIsBold = true,
                textAlign = TextAlign.CENTER,
                textSizeDip = textSizeDip * 2F
        )
        ContentClass.H1 -> copy(
                firstLineIndentEm = 0F,
                margins = margins multiplyHorizontal 1.66F,
                textFontIsBold = true,
                textAlign = TextAlign.CENTER,
                textSizeDip = textSizeDip * 1.5F
        )
        ContentClass.H2 -> copy(
                firstLineIndentEm = 0F,
                margins = margins multiplyHorizontal 2F,
                textFontIsBold = true,
                textAlign = TextAlign.CENTER,
                textSizeDip = textSizeDip * 1.17F
        )
        ContentClass.H3 -> copy(
                firstLineIndentEm = 0F,
                margins = margins multiplyHorizontal 2.66F,
                textFontIsBold = true,
                textSizeDip = textSizeDip * 1F,
                pageBreakBefore = true
        )
        ContentClass.H4 -> copy(
                firstLineIndentEm = 0F,
                margins = margins multiplyHorizontal 3.34F,
                textFontIsBold = true,
                textSizeDip = textSizeDip * 0.83F
        )
        ContentClass.H5 -> copy(
                firstLineIndentEm = 0F,
                margins = margins multiplyHorizontal 4.66F,
                textFontIsBold = true,
                textSizeDip = textSizeDip * 0.67F
        )
        ContentClass.H0_BLOCK -> copy(
                pageBreakBefore = true
        )
        ContentClass.H1_BLOCK -> copy(
                pageBreakBefore = true
        )
        ContentClass.H2_BLOCK -> copy(
                pageBreakBefore = true
        )
        ContentClass.CODE_BLOCK -> copy(
                textAlign = TextAlign.LEFT,
                textSizeDip = textSizeDip * 0.8F
        )
        ContentClass.CODE_LINE -> copy(
                firstLineIndentEm = 0F,
                margins = ContentMargins.Zero,
                textFontFamily = "Monospace"
        )
        ContentClass.POEM_STANZA -> copy(
                textAlign = TextAlign.LEFT,
                margins = margins.copy(
                        left = ContentLength.Percent(0.05F),
                        right = ContentLength.Percent(0.05F)
                )
        )
        ContentClass.POEM_LINE -> copy(
                margins = ContentMargins.Zero,
                textFontIsItalic = true,
                lineHeightMultiplier = 1.2F
        )
        ContentClass.EPIGRAPH -> copy(
                margins = margins.copy(
                        left = ContentLength.Percent(0.05F),
                        right = ContentLength.Percent(0.05F)
                ),
                textFontIsItalic = true
        )
        ContentClass.AUTHOR -> copy(
                firstLineIndentEm = 0F,
                margins = margins.copy(
                        left = ContentLength.Percent(0.05F),
                        right = ContentLength.Percent(0.05F)
                ),
                textAlign = TextAlign.RIGHT
        )
        else -> this
    }

    private infix fun ContentMargins.multiplyHorizontal(multiplier: Float) = copy(
            top = top * multiplier,
            bottom = bottom * multiplier
    )
}

private fun defaultLocale(context: Context, settings: AnalyzeSettings) =
        if (settings.defaultLanguageIsSystem) {
            systemLocale(context)
        } else {
            Locale(settings.defaultLanguage)
        }

private fun systemLocale(context: Context) = Locale(context.resources.configuration.locale.language)

private fun imageScale(settings: ImageSettings) = if (settings.scaleByDpi) {
    ContentImage.Scale.ByDPI(settings.scaleByDpiInteger, settings.scaleIncFiltered, settings.scaleDecFiltered)
} else {
    ContentImage.Scale.Fixed(settings.scaleFixed, settings.scaleIncFiltered, settings.scaleDecFiltered)
}