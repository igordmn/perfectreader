package com.dmi.perfectreader.fragment.book.pagination.page

import com.dmi.perfectreader.fragment.book.location.LocatedSequence
import com.dmi.perfectreader.fragment.book.location.map
import com.dmi.perfectreader.fragment.book.pagination.column.LayoutColumn
import com.dmi.util.graphic.SizeF
import com.dmi.util.collection.SequenceEntry as Entry

fun PageSequence(
        createColumnSequence: (contentSize: SizeF) -> LocatedSequence<LayoutColumn>,
        pageConfig: PageConfig
) : LocatedSequence<Page> {
    val columnSequence = createColumnSequence(pageConfig.contentSize)
    return columnSequence.map {
        Page(it, pageConfig.size, pageConfig.paddings, pageConfig.pageTextGammaCorrection)
    }
}