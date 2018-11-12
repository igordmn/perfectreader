package com.dmi.perfectreader.book.content.common

import com.dmi.perfectreader.MainContext
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.perfectreader.settings.Settings
import com.dmi.util.graphic.SizeF
import com.dmi.util.graphic.shrink
import org.jetbrains.anko.displayMetrics

class PageConfig(
        context: MainContext,
        val size: SizeF,
        settings: Settings = context.settings,
        density: Float = context.android.displayMetrics.density,
        val paddings: Page.Paddings = Page.Paddings(
                settings.format.paddingLeftDip,
                settings.format.paddingRightDip,
                settings.format.paddingTopDip,
                settings.format.paddingBottomDip
        ) * density,
        val footer: Footer? = if (settings.screen.footerEnabled) Footer(context) else null,
        footerExtraSpace: Float = if (footer != null) (footer.height + footer.paddingBottom) else 0F,
        val contentSize: SizeF = size.shrink(paddings.left + paddings.right, paddings.top + paddings.bottom + footerExtraSpace),
        val textGammaCorrection: Float = settings.theme.textGammaCorrection
) {
    class Footer(
            context: MainContext,
            settings: Settings = context.settings,
            density: Float = context.android.displayMetrics.density,
            val height: Float = settings.screen.footerHeightEm * settings.font.sizeDip * density,
            val itemDistance: Float = 20 * density,
            val paddingBottom: Float = settings.screen.footerPaddingBottomPercent * settings.format.paddingBottomDip * density,
            val pageNumber: Boolean = settings.screen.footerPageNumber,
            val numberOfPages: Boolean = settings.screen.footerNumberOfPages,
            val chapter: Boolean = settings.screen.footerChapter
    )
}