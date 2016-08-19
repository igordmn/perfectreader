package com.dmi.perfectreader.fragment.book.pagination.page

import com.dmi.perfectreader.fragment.book.location.LocatedSequence
import com.dmi.perfectreader.fragment.book.location.map
import com.dmi.perfectreader.fragment.book.pagination.column.LayoutColumn
import com.dmi.util.graphic.SizeF
import java.lang.Math.max
import com.dmi.util.collection.SequenceEntry as Entry

fun PageSequence(
        createColumnSequence: (contentSize: SizeF) -> LocatedSequence<LayoutColumn>,
        pageConfig: PageConfig
) : LocatedSequence<Page> {
    val margins = pageConfig.marginsDip * pageConfig.density
    val contentSize = pageConfig.size.shrink(margins.left + margins.right, margins.top + margins.bottom)
    val columnSequence = createColumnSequence(contentSize)
    return columnSequence.map {
        Page(it, pageConfig.size, margins, pageConfig.pageTextGammaCorrection)
    }
}

private fun SizeF.shrink(width: Float, height: Float) = SizeF(
        max(0F, this.width - width),
        max(0F, this.height - height)
)