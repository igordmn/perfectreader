package com.dmi.perfectreader.book.pagination.page

import com.dmi.perfectreader.book.content.location.LocatedSequence
import com.dmi.perfectreader.book.content.location.map
import com.dmi.perfectreader.book.content.obj.param.FormatConfig
import com.dmi.perfectreader.book.pagination.column.LayoutColumn
import com.dmi.util.graphic.Size
import com.dmi.util.graphic.SizeF
import com.dmi.util.collection.SequenceEntry as Entry

fun LocatedSequence<LayoutColumn>.pages(
        size: SizeF,
        formatConfig: FormatConfig
): LocatedSequence<Page> = map {
    Page(it, size, formatConfig.pagePaddingsDip * formatConfig.density, formatConfig.pageTextGammaCorrection)
}