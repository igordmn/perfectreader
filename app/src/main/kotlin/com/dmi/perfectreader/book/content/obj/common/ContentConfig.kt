package com.dmi.perfectreader.book.content.obj.common

import android.content.Context
import com.dmi.perfectreader.MainContext
import com.dmi.perfectreader.book.content.common.Align
import com.dmi.perfectreader.book.content.common.DefaultHangingConfig
import com.dmi.perfectreader.book.content.common.NoneHangingConfig
import com.dmi.perfectreader.book.content.common.TextAlign
import com.dmi.perfectreader.book.content.obj.ContentImage
import com.dmi.perfectreader.settings.OtherSettings
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
        private val footerTextSizePercent: Float,
        private val defaultStyle: ContentStyle
) {
    constructor(
            context: MainContext,
            androidContext: Context = context.android,
            settings: Settings = context.settings
    ) : this(
            density = context.android.displayMetrics.density,
            fonts = context.resources.fonts,
            defaultLocale = defaultLocale(androidContext, settings.other),
            ignoreDeclaredLocale = settings.other.ignoreDeclaredLanguage,
            imageScale = imageScale(settings.other),
            footerTextSizePercent = settings.screen.footerTextSizePercent,
            defaultStyle = ContentStyle(
                    boxAlign = Align.LEFT,
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

                    textFontFamily = settings.font.fontFamily,
                    textFontIsBold = settings.font.isBold,
                    textFontIsItalic = settings.font.isItalic,

                    textSizeDip = settings.font.sizeDip,
                    textScaleX = settings.font.scaleX,
                    textSkewX = settings.font.skewX,
                    textStrokeWidthEm = settings.font.strokeWidthEm,
                    textColor = Color(settings.theme.textColor),
                    textAntialiasing = settings.font.antialiasing,
                    textHinting = settings.font.hinting,
                    textSubpixelPositioning = settings.font.subpixelPositioning,

                    textShadowEnabled = settings.theme.textShadowEnabled,
                    textShadowAngleDegrees = settings.theme.textShadowAngleDegrees,
                    textShadowOffsetEm = settings.theme.textShadowOffsetEm,
                    textShadowSizeEm = settings.theme.textShadowSizeEm,
                    textShadowBlurEm = settings.theme.textShadowBlurEm,
                    textShadowColor = Color(settings.theme.textShadowColor),

                    selectionColor = Color(settings.theme.selectionColor)
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
        ContentClass.H3_BLOCK -> this
        ContentClass.H4_BLOCK -> this
        ContentClass.H5_BLOCK -> this
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
        ContentClass.IMAGE_BLOCK -> copy(
                boxAlign = Align.CENTER
        )
        ContentClass.FOOTER -> copy(
                firstLineIndentEm = 0F,
                textSizeDip = textSizeDip * footerTextSizePercent
        )
    }

    private infix fun ContentMargins.multiplyHorizontal(multiplier: Float) = copy(
            top = top * multiplier,
            bottom = bottom * multiplier
    )
}

private fun defaultLocale(context: Context, settings: OtherSettings) =
        if (settings.defaultLanguageIsSystem) {
            systemLocale(context)
        } else {
            Locale(settings.defaultLanguage)
        }

private fun systemLocale(context: Context) = Locale(context.resources.configuration.locale.language)

private fun imageScale(settings: OtherSettings) = if (settings.scaleByDpi) {
    ContentImage.Scale.ByDPI(settings.scaleByDpiInteger, settings.scaleIncFiltered, settings.scaleDecFiltered)
} else {
    ContentImage.Scale.Fixed(settings.scaleFixed, settings.scaleIncFiltered, settings.scaleDecFiltered)
}