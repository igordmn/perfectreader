package com.dmi.perfectreader.fragment.book.pagination.page

import android.content.Context
import com.dmi.perfectreader.data.UserSettings
import com.dmi.perfectreader.fragment.book.pagination.page.Page.Margins
import com.dmi.util.graphic.SizeF
import org.jetbrains.anko.displayMetrics
import com.dmi.perfectreader.data.UserSettingKeys.Format as FormatKeys

class PageConfig(
        val density: Float,
        val size: SizeF,
        val marginsDip: Margins,
        val pageTextGammaCorrection: Float
)

fun settingsPageConfig(context: Context, size: SizeF, settings: UserSettings) = PageConfig(
        density = context.displayMetrics.density,
        size = size,
        marginsDip = Margins(
                settings[FormatKeys.pageMarginLeftDip],
                settings[FormatKeys.pageMarginRightDip],
                settings[FormatKeys.pageMarginTopDip],
                settings[FormatKeys.pageMarginBottomDip]
        ),
        pageTextGammaCorrection = settings[FormatKeys.pageTextGammaCorrection]
)