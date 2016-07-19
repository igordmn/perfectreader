package com.dmi.perfectreader.fragment.book.layout

import com.dmi.perfectreader.fragment.book.layout.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.location.LocatedSequence
import com.dmi.perfectreader.fragment.book.location.map
import com.dmi.perfectreader.fragment.book.content.obj.ConfiguredObject
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.util.graphic.SizeF
import com.dmi.util.collection.SequenceEntry as Entry

fun LayoutSequence(
        configuredSequence: LocatedSequence<ConfiguredObject>,
        layouter: ObjectLayouter<ConfiguredObject, LayoutObject>,
        pageSize: SizeF
) = configuredSequence.map {
    layouter.layout(it, LayoutSpace.root(pageSize))
}