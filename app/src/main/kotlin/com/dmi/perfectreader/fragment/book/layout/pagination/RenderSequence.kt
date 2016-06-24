package com.dmi.perfectreader.fragment.book.layout.pagination

import com.dmi.perfectreader.fragment.book.layout.layouter.Layouter
import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.obj.content.ComputedObject
import com.dmi.perfectreader.fragment.book.obj.render.RenderObject
import com.dmi.util.graphic.SizeF
import com.dmi.util.collection.SequenceEntry as Entry

fun RenderSequence(
        computedSequence: LocatedSequence<ComputedObject>,
        layouter: Layouter<ComputedObject, RenderObject>,
        pageSize: SizeF
) = computedSequence.map {
    layouter.layout(it, LayoutSpace.root(pageSize))
}