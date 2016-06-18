package com.dmi.perfectreader.fragment.book.layout.pagination

import com.dmi.perfectreader.fragment.book.obj.common.LayoutConfig
import com.dmi.perfectreader.fragment.book.obj.content.ContentObject
import com.dmi.util.collection.SequenceEntry as Entry

fun LayoutSequence(
        contentSequence: LocatedSequence<ContentObject>,
        layoutConfig: LayoutConfig
) = contentSequence.map {
    it.configure(layoutConfig)
}