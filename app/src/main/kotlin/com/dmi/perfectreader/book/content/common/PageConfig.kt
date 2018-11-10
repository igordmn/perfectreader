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
                settings.format.pagePaddingLeftDip,
                settings.format.pagePaddingRightDip,
                settings.format.pagePaddingTopDip,
                settings.format.pagePaddingBottomDip
        ) * density,
        val footer: Footer? = if (settings.format.pageFooterEnabled) Footer(context) else null,
        footerExtraSpace: Float = if (footer != null) (footer.height + footer.paddingBottom) else 0F,
        val contentSize: SizeF = size.shrink(paddings.left + paddings.right, paddings.top + paddings.bottom + footerExtraSpace),
        val textGammaCorrection: Float = settings.format.pageTextGammaCorrection
) {
    class Footer(
            context: MainContext,
            settings: Settings = context.settings,
            density: Float = context.android.displayMetrics.density,
            val height: Float = settings.format.pageFooterHeightEm * settings.format.textSizeDip * density,
            val itemDistance: Float = 20 * density,
            val paddingBottom: Float = settings.format.pageFooterPaddingBottomPercent * settings.format.pagePaddingBottomDip * density,
            val pageNumber: Boolean = settings.format.pageFooterPageNumber,
            val numberOfPages: Boolean = settings.format.pageFooterNumberOfPages,
            val chapter: Boolean = settings.format.pageFooterChapter
    )
}