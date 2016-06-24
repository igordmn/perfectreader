package com.dmi.perfectreader.fragment.book.layout.pagination

import com.dmi.util.collection.SequenceEntry as Entry

fun PageSequence(
        contentPageSequence: LocatedSequence<LayoutColumn>,
        pageConfig: PageConfig
) = contentPageSequence.map {
    Page(it, pageConfig.contentSize, pageConfig.margins)
}