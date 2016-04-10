package com.dmi.perfectreader.book.page

import com.dmi.perfectreader.book.page.LocatedSequence.Entry
import com.dmi.perfectreader.layout.layoutobj.LayoutObject
import com.dmi.perfectreader.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.layout.layouter.Layouter
import com.dmi.perfectreader.location.BookLocation
import com.dmi.perfectreader.layout.renderobj.RenderObject

class BookRenderObjects(
        val layoutObjects: LocatedSequence<LayoutObject>,
        val layouter: Layouter<LayoutObject, RenderObject>,
        val pageWidth: Float,
        val pageHeight: Float
) : LocatedSequence<RenderObject> {
    override fun get(location: BookLocation): Entry<RenderObject> =
            RenderObjectEntry(layoutObjects[location])

    private fun layout(layoutObj: LayoutObject) = layouter.layout(
            layoutObj,
            LayoutSpace.root(pageWidth, pageHeight)
    )

    private inner class RenderObjectEntry(val layoutObj: Entry<LayoutObject>) : Entry<RenderObject> {
        override val item = layout(layoutObj.item)
        override val hasPrevious = layoutObj.hasPrevious
        override val hasNext = layoutObj.hasNext

        override val previous: Entry<RenderObject>
            get() = RenderObjectEntry(layoutObj.previous)

        override val next: Entry<RenderObject>
            get() = RenderObjectEntry(layoutObj.next)
    }
}
