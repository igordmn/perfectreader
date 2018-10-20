package com.dmi.perfectreader.book.content.obj.common

import android.content.Context
import com.dmi.perfectreader.Main
import com.dmi.perfectreader.book.content.common.DefaultHangingConfig
import com.dmi.perfectreader.book.content.common.NoneHangingConfig
import com.dmi.perfectreader.book.content.common.TextAlign
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
        val defaultLocale: Locale,
        val ignoreDeclaredLocale: Boolean,
        val paragraphVerticalMarginEm: Float,
        val paragraphFirstLineIndentEm: Float,
        val style: ContentStyle
) {
    constructor(
            main: Main,
            context: Context = main.applicationContext,
            settings: Settings = main.settings
    ) : this(
            density = main.applicationContext.displayMetrics.density,
            fonts = main.resources.fonts,
            defaultLocale = defaultLocale(context, settings),
            ignoreDeclaredLocale = settings.analyze.ignoreDeclaredLanguage,
            paragraphVerticalMarginEm = settings.format.paragraphVerticalMarginEm,
            paragraphFirstLineIndentEm = settings.format.paragraphFirstLineIndentEm,
            style = ContentStyle(
                    margins = ContentMargins.Zero,
                    firstLineIndentEm = 0F,

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

                    selectionColor = Color(settings.selection.color),

                    imageSourceScale = if (settings.image.sourceScaleByDpi) context.displayMetrics.density else settings.image.sourceScale,
                    imageScaleFiltered = settings.image.scaleFiltered
            )
    )

    private fun style(cls: ContentClass?): ContentStyle = when (cls) {
        null -> style
        ContentClass.PARAGRAPH -> style.copy(
                margins = ContentMargins(
                        ContentLength.Percent(0F),
                        ContentLength.Percent(0F),
                        ContentLength.Em(paragraphVerticalMarginEm),
                        ContentLength.Em(paragraphVerticalMarginEm)
                ),
                firstLineIndentEm = paragraphFirstLineIndentEm
        )
        ContentClass.BOLD -> style.copy(textFontIsBold = true)
        ContentClass.ITALIC -> style.copy(textFontIsItalic = true)
        ContentClass.H1 -> style.copy(
                margins = hMargins(1.34F),
                textFontIsBold = true, textAlign = TextAlign.CENTER, textSizeDip = style.textSizeDip * 2F
        )
        ContentClass.H2 -> style.copy(
                margins = hMargins(1.66F),
                textFontIsBold = true, textAlign = TextAlign.LEFT, textSizeDip = style.textSizeDip * 1.5F
        )
        ContentClass.H3 -> style.copy(
                margins = hMargins(2F),
                textFontIsBold = true, textAlign = TextAlign.LEFT, textSizeDip = style.textSizeDip * 1.17F
        )
        ContentClass.H4 -> style.copy(
                margins = hMargins(2.66F),
                textFontIsBold = true, textAlign = TextAlign.LEFT, textSizeDip = style.textSizeDip * 1F
        )
        ContentClass.H5 -> style.copy(
                margins = hMargins(3.34F),
                textFontIsBold = true, textAlign = TextAlign.LEFT, textSizeDip = style.textSizeDip * 0.83F
        )
        ContentClass.H6 -> style.copy(
                margins = hMargins(4.66F),
                textFontIsBold = true, textAlign = TextAlign.LEFT, textSizeDip = style.textSizeDip * 0.67F
        )
        ContentClass.POEM_STANZA -> style.copy(
                margins = ContentMargins(
                        ContentLength.Percent(10F),
                        ContentLength.Percent(10F),
                        ContentLength.Em(paragraphVerticalMarginEm),
                        ContentLength.Em(paragraphVerticalMarginEm)
                ),
                textFontIsItalic = true
        )
        ContentClass.EPIGRAPH -> style.copy(
                margins = ContentMargins(
                        ContentLength.Percent(10F),
                        ContentLength.Percent(10F),
                        ContentLength.Em(paragraphVerticalMarginEm),
                        ContentLength.Em(paragraphVerticalMarginEm)
                ),
                textFontIsItalic = true
        )
        ContentClass.TEXT_AUTHOR -> style.copy(
                margins = ContentMargins(
                        ContentLength.Percent(10F),
                        ContentLength.Percent(10F),
                        ContentLength.Em(paragraphVerticalMarginEm * 0.5F),
                        ContentLength.Em(paragraphVerticalMarginEm)
                ),
                textAlign = TextAlign.RIGHT
        )
        ContentClass.CODE -> style.copy(textAlign = TextAlign.LEFT, textFontFamily = "Monospace")
    }

    private fun hMargins(multiplier: Float) = ContentMargins(
            ContentLength.Zero,
            ContentLength.Zero,
            ContentLength.Em(paragraphVerticalMarginEm * multiplier),
            ContentLength.Em(paragraphVerticalMarginEm * multiplier)
    )

    val styled: Cache<ContentClass?, ContentConfig> = cache { cls: ContentClass? ->
        copy(style = style(cls))
    }

    val inherited: ContentConfig by lazy {
        copy(style = style.inherit())
    }
}

private fun defaultLocale(context: Context, settings: Settings) =
        if (settings.analyze.defaultLanguageIsSystem) {
            systemLocale(context)
        } else {
            Locale(settings.analyze.defaultLanguage)
        }

private fun systemLocale(context: Context) = Locale(context.resources.configuration.locale.language)