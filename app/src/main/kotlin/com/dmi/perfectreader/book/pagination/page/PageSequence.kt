package com.dmi.perfectreader.book.pagination.page

import com.dmi.perfectreader.book.content.common.PageConfig
import com.dmi.perfectreader.book.content.location.LocatedSequence
import com.dmi.perfectreader.book.content.location.map
import com.dmi.perfectreader.book.pagination.column.LayoutColumn
import com.dmi.util.collection.SequenceEntry as Entry

fun LocatedSequence<LayoutColumn>.pages(config: PageConfig): LocatedSequence<Page> {
    return map {
        Page(it, config.size, config.paddings, config.textGammaCorrection)
    }
}