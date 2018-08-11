package com.dmi.perfectreader.book

import com.dmi.perfectreader.book.content.Content
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.obj.param.FormatConfig
import com.dmi.perfectreader.settings.Settings
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.intCeil
import com.dmi.util.lang.intFloor
import com.dmi.util.lang.intRound
import java.lang.Math.*

class Locations(
        private val content: Content,
        private val contentSize: SizeF,
        private val formatConfig: FormatConfig,
        settings: Settings
) {
    val numberOfPages: Int = approximateNumberOfPages(content, settings)

    init {
        require(numberOfPages > 0)
    }

    fun locationToPercent(location: Location): Double = content.locationToPercent(location)
    fun percentToLocation(percent: Double): Location = content.percentToLocation(percent)
    fun locationToPageNumber(location: Location): Int = percentToPageNumber(locationToPercent(location))
    fun pageNumberToLocation(pageNumber: Int): Location = percentToLocation(pageNumberToPercent(pageNumber))

    private fun percentToPageNumber(percent: Double): Int {
        require(percent in 0.0..1.0)
        return min(intRound(numberOfPages * percent) + 1, numberOfPages)
    }

    private fun pageNumberToPercent(pageNumber: Int): Double {
        require(pageNumber in 1..numberOfPages)
        return (pageNumber - 1).toDouble() / numberOfPages
    }

    private fun approximateNumberOfPages(content: Content, settings: Settings): Int {
        val pageLength: Int =
                if (settings.navigation.pageSymbolCountIsAuto) {
                    approximatePageLength()
                } else {
                    settings.navigation.pageSymbolCount
                }
        return intCeil(content.length / pageLength)
    }

    private fun approximatePageLength(): Int {
        val textHeight = formatConfig.textSizeDip * formatConfig.density
        val letterSpacing = max(-0.6F, formatConfig.letterSpacingEm) * textHeight
        val wordSpacingMultiplier = formatConfig.wordSpacingMultiplier
        val lineHeightMultiplier = formatConfig.lineHeightMultiplier
        val paragraphVerticalMargin = formatConfig.paragraphVerticalMarginEm * textHeight

        val wordLength = 5
        val paragraphLength = 200

        val charWidth = textHeight / 2 + letterSpacing
        val spaceWidth = charWidth
        val wordWidth = charWidth * wordLength + spaceWidth * wordSpacingMultiplier

        val lineHeight = textHeight * lineHeightMultiplier
        val lineWordCount: Int = max(intFloor(contentSize.width / wordWidth), 1)
        val lineLength: Int = lineWordCount * (wordLength + 1)

        val paragraphLineCount: Int = intCeil(paragraphLength.toDouble() / lineLength)
        val paragraphSpacing = paragraphVerticalMargin * 2
        val paragraphHeight = paragraphLineCount * lineHeight + paragraphSpacing
        val paragraphCountFractional: Float = contentSize.height / paragraphHeight

        return max(1, round(paragraphCountFractional * paragraphLength))
    }
}