package com.dmi.perfectreader.fragment.book.layout

import com.dmi.perfectreader.fragment.book.layout.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.location.LocatedSequence
import com.dmi.perfectreader.fragment.book.location.map
import com.dmi.perfectreader.fragment.book.content.obj.ComputedObject
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.util.graphic.SizeF
import com.dmi.util.collection.SequenceEntry as Entry

fun LayoutSequence(
        computedSequence: LocatedSequence<ComputedObject>,
        layouter: ObjectLayouter<ComputedObject, LayoutObject>,
        pageSize: SizeF
) = computedSequence.map {
    layouter.layout(it, LayoutSpace.root(pageSize))
}