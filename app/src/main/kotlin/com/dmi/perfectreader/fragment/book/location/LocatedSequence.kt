package com.dmi.perfectreader.fragment.book.location

import com.dmi.util.collection.SequenceEntry

interface LocatedSequence<T> {
    operator fun get(location: Location): SequenceEntry<T>
}

fun <T, R> LocatedSequence<T>.map(transform: (T) -> R): LocatedSequence<R> =
        object : LocatedSequence<R> {
            override fun get(location: Location) = TransformEntry(this@map[location])

            private inner class TransformEntry(val original: SequenceEntry<T>) : SequenceEntry<R> {
                override val item = transform(original.item)
                override val hasPrevious = original.hasPrevious
                override val hasNext = original.hasNext

                override val previous: SequenceEntry<R> get() = TransformEntry(original.previous)
                override val next: SequenceEntry<R> get() = TransformEntry(original.next)
            }
        }