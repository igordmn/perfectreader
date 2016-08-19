package com.dmi.perfectreader.fragment.book.pagination.page

import android.content.Context
import com.dmi.perfectreader.data.UserSettings
import com.dmi.perfectreader.fragment.book.pagination.page.Page.Paddings
import com.dmi.util.graphic.SizeF
import org.jetbrains.anko.displayMetrics
import com.dmi.perfectreader.data.UserSettingKeys.Format as FormatKeys

class PageConfig(
        val density: Float,
        val size: SizeF,
        val paddingsDip: Paddings,
        val pageTextGammaCorrection: Float
)

fun settingsPageConfig(context: Context, size: SizeF, settings: UserSettings) = PageConfig(
        density = context.displayMetrics.density,
        size = size,
        paddingsDip = Paddings(
                settings[FormatKeys.pagePaddingLeftDip],
                settings[FormatKeys.pagePaddingRightDip],
                settings[FormatKeys.pagePaddingTopDip],
                settings[FormatKeys.pagePaddingBottomDip]
        ),
        pageTextGammaCorrection = settings[FormatKeys.pageTextGammaCorrection]
)