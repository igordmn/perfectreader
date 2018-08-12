package com.dmi.perfectreader.book.pagination.part

import com.dmi.perfectreader.book.content.location.LocatedSequence
import com.dmi.perfectreader.book.content.location.flatMap
import com.dmi.perfectreader.book.layout.obj.LayoutObject
import com.dmi.util.range.indexOfNearestRange
import kotlinx.coroutines.CommonPool
import kotlinx.coroutines.withContext
import com.dmi.util.collection.SequenceEntry as Entry

fun LocatedSequence<LayoutObject>.parts() = flatMap(
        transform = {
            withContext(CommonPool) {
                splitIntoParts(it)
            }
        },
        indexOf = {
            indexOfNearestRange({ range }, it)
        }
)