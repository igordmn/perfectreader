package com.dmi.perfectreader.book.content

import com.dmi.perfectreader.book.content.obj.ContentObject
import com.dmi.perfectreader.book.content.obj.param.ContentConfig
import com.dmi.perfectreader.book.location.LocatedSequence
import com.dmi.perfectreader.book.location.map
import com.dmi.util.collection.SequenceEntry as Entry

fun ConfiguredSequence(
        contentSequence: LocatedSequence<ContentObject>,
        layoutConfig: ContentConfig
) = contentSequence.map {
    it.configure(layoutConfig)
}