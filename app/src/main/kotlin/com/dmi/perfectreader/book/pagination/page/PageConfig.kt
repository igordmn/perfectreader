package com.dmi.perfectreader.book.pagination.page

import android.content.Context
import com.dmi.perfectreader.data.UserSettings
import com.dmi.perfectreader.book.pagination.page.Page.Paddings
import com.dmi.util.graphic.SizeF
import org.jetbrains.anko.displayMetrics
import com.dmi.perfectreader.data.UserSettingKeys.Format as FormatKeys

class PageConfig(
        val density: Float,
        val size: SizeF,
        val paddings: Paddings,
        val pageTextGammaCorrection: Float
) {
        val contentSize = size.shrink(paddings.left + paddings.right, paddings.top + paddings.bottom)
}

fun settingsPageConfig(context: Context, size: SizeF, settings: UserSettings) = PageConfig(
        density = context.displayMetrics.density,
        size = size,
        paddings = Paddings(
                settings[FormatKeys.pagePaddingLeftDip],
                settings[FormatKeys.pagePaddingRightDip],
                settings[FormatKeys.pagePaddingTopDip],
                settings[FormatKeys.pagePaddingBottomDip]
        ) * context.displayMetrics.density,
        pageTextGammaCorrection = settings[FormatKeys.pageTextGammaCorrection]
)

private fun SizeF.shrink(width: Float, height: Float) = SizeF(
        Math.max(0F, this.width - width),
        Math.max(0F, this.height - height)
)