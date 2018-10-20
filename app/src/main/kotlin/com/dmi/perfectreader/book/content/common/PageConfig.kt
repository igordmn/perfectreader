package com.dmi.perfectreader.book.content.common

import com.dmi.perfectreader.Main
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.perfectreader.settings.Settings
import com.dmi.util.graphic.SizeF
import com.dmi.util.graphic.shrink
import org.jetbrains.anko.displayMetrics

class PageConfig(
        main: Main,
        val size: SizeF,
        settings: Settings = main.settings,
        density: Float = main.applicationContext.displayMetrics.density,
        val paddings: Page.Paddings = Page.Paddings(
                settings.format.pagePaddingLeftDip,
                settings.format.pagePaddingRightDip,
                settings.format.pagePaddingTopDip,
                settings.format.pagePaddingBottomDip
        ) * density,
        val contentSize: SizeF = size.shrink(paddings.left + paddings.right, paddings.top + paddings.bottom),
        val textGammaCorrection: Float = settings.format.pageTextGammaCorrection
)