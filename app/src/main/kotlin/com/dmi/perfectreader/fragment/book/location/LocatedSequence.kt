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

fun <T, R> LocatedSequence<T>.flatMap(transform: (T) -> List<R>, indexOf: List<R>.(Location) -> Int): LocatedSequence<R> =
        object : LocatedSequence<R> {
            override fun get(location: Location): SequenceEntry<R> = childAt(this@flatMap[location], location)

            private fun childAt(obj: SequenceEntry<T>, location: Location): ChildEntry {
                val children = transform(obj.item)
                val index = children.indexOf(location)
                return ChildEntry(children, index, obj)
            }

            private fun childAtBegin(obj: SequenceEntry<T>): ChildEntry {
                val children = transform(obj.item)
                return ChildEntry(children, 0, obj)
            }

            private fun childAtEnd(obj: SequenceEntry<T>): ChildEntry {
                val children = transform(obj.item)
                return ChildEntry(children, children.size - 1, obj)
            }

            private inner class ChildEntry(
                    val children: List<R>,
                    val index: Int,
                    val root: SequenceEntry<T>
            ) : SequenceEntry<R> {
                override val item = children[index]
                override val hasPrevious = index > 0 || root.hasPrevious
                override val hasNext = index < children.size - 1 || root.hasNext

                override val previous: SequenceEntry<R>
                    get() = if (index > 0) ChildEntry(children, index - 1, root) else childAtEnd(root.previous)

                override val next: SequenceEntry<R>
                    get() = if (index < children.size - 1) ChildEntry(children, index + 1, root) else childAtBegin(root.next)
            }
        }