package com.dmi.perfectreader.fragment.book

import com.dmi.perfectreader.data.UserSettingKeys
import com.dmi.perfectreader.data.UserSettings
import com.dmi.perfectreader.fragment.book.content.Content
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.pagination.page.PageConfig
import com.dmi.util.lang.intCeil
import com.dmi.util.lang.intFloor
import com.dmi.util.lang.intRound
import java.lang.Math.*

class LocationConverter(
        private val content: Content,
        private val pageConfig: PageConfig,
        settings: UserSettings
) {
    val numberOfPages: Int = approximateNumberOfPages(content, settings)

    init {
        require(numberOfPages > 0)
    }

    fun locationToPercent(location: Location): Double = content.locationToPercent(location)
    fun percentToLocation(percent: Double): Location = content.percentToLocation(percent)
    fun locationToPageNumber(location: Location): Int = percentToPageNumber(locationToPercent(location))
    fun pageNumberToLocation(pageNumber: Int): Location = percentToLocation(pageNumberToPercent(pageNumber))

    fun percentToPageNumber(percent: Double): Int {
        require(percent >= 0.0 && percent <= 1.0)
        return min(intRound(numberOfPages * percent) + 1, numberOfPages)
    }

    fun pageNumberToPercent(pageNumber: Int): Double {
        require(pageNumber >= 1 && pageNumber <= numberOfPages)
        return (pageNumber - 1).toDouble() / numberOfPages
    }

    private fun approximateNumberOfPages(content: Content, settings: UserSettings): Int {
        val pageLength: Int =
                if (settings[UserSettingKeys.Navigation.pageSymbolCountIsAuto]) {
                    approximatePageLength(settings)
                } else {
                    settings[UserSettingKeys.Navigation.pageSymbolCount]
                }
        return intCeil(content.length / pageLength)
    }

    private fun approximatePageLength(settings: UserSettings): Int {
        val textHeight = settings[UserSettingKeys.Format.textSizeDip] * pageConfig.density
        val letterSpacing = max(-0.6F, settings[UserSettingKeys.Format.letterSpacingEm]) * textHeight
        val wordSpacingMultiplier = settings[UserSettingKeys.Format.wordSpacingMultiplier]
        val lineHeightMultiplier = settings[UserSettingKeys.Format.lineHeightMultiplier]
        val paragraphVerticalMargin = settings[UserSettingKeys.Format.paragraphVerticalMarginEm] * textHeight

        val wordLength = 5
        val paragraphLength = 200

        val charWidth = textHeight / 2 + letterSpacing
        val spaceWidth = charWidth
        val wordWidth = charWidth * wordLength + spaceWidth * wordSpacingMultiplier

        val lineHeight = textHeight * lineHeightMultiplier
        val lineWordCount: Int = max(intFloor(pageConfig.contentSize.width / wordWidth), 1)
        val lineLength: Int = lineWordCount * (wordLength + 1)

        val paragraphLineCount: Int = intCeil(paragraphLength.toDouble() / lineLength)
        val paragraphSpacing = paragraphVerticalMargin * 2
        val paragraphHeight = paragraphLineCount * lineHeight + paragraphSpacing
        val paragraphCountFractional: Float = pageConfig.contentSize.height / paragraphHeight

        return max(1, round(paragraphCountFractional * paragraphLength))
    }
}