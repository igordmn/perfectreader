package com.dmi.perfectreader.fragment.book.pagination.page

import com.dmi.perfectreader.fragment.book.location.LocatedSequence
import com.dmi.perfectreader.fragment.book.pagination.column.LayoutColumn
import com.dmi.perfectreader.fragment.book.location.map
import com.dmi.util.collection.SequenceEntry as Entry

fun PageSequence(
        contentPageSequence: LocatedSequence<LayoutColumn>,
        pageConfig: PageConfig
) = contentPageSequence.map {
    Page(it, pageConfig.contentSize, pageConfig.margins)
}