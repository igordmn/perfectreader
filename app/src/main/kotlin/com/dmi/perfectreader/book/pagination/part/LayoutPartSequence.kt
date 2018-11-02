package com.dmi.perfectreader.book.pagination.part

import com.dmi.perfectreader.book.content.location.LocatedSequence
import com.dmi.perfectreader.book.content.location.flatMap
import com.dmi.perfectreader.book.layout.obj.LayoutObject
import com.dmi.util.range.definitelySearchRangeIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.dmi.util.collection.SequenceEntry as Entry

fun LocatedSequence<LayoutObject>.parts() = flatMap(
        transform = {
            withContext(Dispatchers.Default) {
                splitIntoParts(it)
            }
        },
        indexOf = {
            definitelySearchRangeIndex({ range }, it)
        }
)