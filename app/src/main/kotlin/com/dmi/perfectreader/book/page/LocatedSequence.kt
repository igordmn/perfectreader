package com.dmi.perfectreader.book.page

import com.dmi.perfectreader.location.BookLocation

interface LocatedSequence<T> {
    operator fun get(location: BookLocation): Entry<T>

    interface Entry<T> : SequenceEntry<T> {
        override val previous: Entry<T>
        override val next: Entry<T>
    }
}

interface SequenceEntry<T> {
    val item: T
    val hasPrevious: Boolean
    val hasNext: Boolean
    val previous: SequenceEntry<T>
    val next: SequenceEntry<T>
}
