package com.dmi.perfectreader.book.pagination.part

import com.dmi.perfectreader.book.layout.obj.LayoutObject
import com.dmi.perfectreader.book.location.LocatedSequence
import com.dmi.perfectreader.book.location.flatMap
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