package com.dmi.perfectreader.fragment.book.layout.pagination

import com.dmi.perfectreader.fragment.book.layout.layouter.Layouter
import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.obj.content.ComputedObject
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutObject
import com.dmi.util.graphic.SizeF
import com.dmi.util.collection.SequenceEntry as Entry

fun LayoutSequence(
        computedSequence: LocatedSequence<ComputedObject>,
        layouter: Layouter<ComputedObject, LayoutObject>,
        pageSize: SizeF
) = computedSequence.map {
    layouter.layout(it, LayoutSpace.root(pageSize))
}