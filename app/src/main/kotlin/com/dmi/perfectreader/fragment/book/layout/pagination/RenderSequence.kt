package com.dmi.perfectreader.fragment.book.layout.pagination

import com.dmi.perfectreader.fragment.book.layout.layouter.Layouter
import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutObject
import com.dmi.perfectreader.fragment.book.obj.render.RenderObject
import com.dmi.util.graphic.SizeF
import com.dmi.util.collection.SequenceEntry as Entry

fun RenderSequence(
        layoutSequence: LocatedSequence<LayoutObject>,
        layouter: Layouter<LayoutObject, RenderObject>,
        pageSize: SizeF
) = layoutSequence.map {
    layouter.layout(it, LayoutSpace.root(pageSize))
}