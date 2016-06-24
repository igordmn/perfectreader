package com.dmi.perfectreader.fragment.book.content

import com.dmi.perfectreader.fragment.book.content.obj.ContentObject
import com.dmi.perfectreader.fragment.book.content.obj.param.LayoutConfig
import com.dmi.perfectreader.fragment.book.location.LocatedSequence
import com.dmi.perfectreader.fragment.book.location.map
import com.dmi.util.collection.SequenceEntry as Entry

fun ComputedSequence(
        contentSequence: LocatedSequence<ContentObject>,
        layoutConfig: LayoutConfig
) = contentSequence.map {
    it.configure(layoutConfig)
}