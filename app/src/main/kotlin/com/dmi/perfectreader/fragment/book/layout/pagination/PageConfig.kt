package com.dmi.perfectreader.fragment.book.layout.pagination

import com.dmi.perfectreader.data.UserSettings
import com.dmi.perfectreader.fragment.book.layout.pagination.Page.Margins
import com.dmi.util.graphic.SizeF
import java.lang.Math.max
import com.dmi.perfectreader.data.UserSettingKeys.Format as FormatKeys

class PageConfig(
        val contentSize: SizeF,
        val margins: Margins
)

fun settingsPageConfig(pageSize: SizeF, settings: UserSettings): PageConfig {
    val marginLeft = settings[FormatKeys.pageMarginLeft]
    val marginRight = settings[FormatKeys.pageMarginRight]
    val marginTop = settings[FormatKeys.pageMarginTop]
    val marginBottom = settings[FormatKeys.pageMarginBottom]
    val marginTotalHorizontal = marginLeft + marginRight
    val marginTotalVertical = marginTop + marginBottom
    return PageConfig(
            contentSize = pageSize.shrink(marginTotalHorizontal, marginTotalVertical),
            margins = Margins(marginLeft, marginRight, marginTop, marginBottom)
    )
}

private fun SizeF.shrink(width: Float, height: Float) = SizeF(
        max(0F, this.width - width),
        max(0F, this.height - height)
)