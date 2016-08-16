package com.dmi.perfectreader.fragment.book.pagination.part

import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.perfectreader.fragment.book.location.LocatedSequence
import com.dmi.perfectreader.fragment.book.location.flatMap
import com.dmi.util.range.indexOfNearestRange
import com.dmi.util.collection.SequenceEntry as Entry

fun LayoutPartSequence(layoutSequence: LocatedSequence<LayoutObject>) = layoutSequence.flatMap(
        transform = {
            splitIntoParts(it)
        },
        indexOf = {
            indexOfNearestRange({ range }, it)
        }
)