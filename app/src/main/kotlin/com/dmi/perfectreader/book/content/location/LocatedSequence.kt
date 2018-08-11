package com.dmi.perfectreader.book.content.location

import com.dmi.util.collection.SequenceEntry

interface LocatedSequence<T> {
    suspend fun get(location: Location): SequenceEntry<T>
}

fun <T, R> LocatedSequence<T>.map(transform: suspend (T) -> R) = object : LocatedSequence<R> {
    override suspend fun get(location: Location): SequenceEntry<R> = transformEntry(this@map.get(location))

    private suspend fun transformEntry(original: SequenceEntry<T>) = TransformEntry(original, transform(original.item))

    private inner class TransformEntry(val original: SequenceEntry<T>, override val item: R) : SequenceEntry<R> {
        override val hasPrevious = original.hasPrevious
        override val hasNext = original.hasNext

        override suspend fun previous(): SequenceEntry<R> = transformEntry(original.previous())
        override suspend fun next(): SequenceEntry<R> = transformEntry(original.next())
    }
}

fun <T, R> LocatedSequence<T>.flatMap(transform: suspend (T) -> List<R>, indexOf: List<R>.(Location) -> Int) = object : LocatedSequence<R> {
    override suspend fun get(location: Location): SequenceEntry<R> = childAt(this@flatMap.get(location), location)

    private suspend fun childAt(obj: SequenceEntry<T>, location: Location): ChildEntry {
        val children = transform(obj.item)
        val index = children.indexOf(location)
        return ChildEntry(children, index, obj)
    }

    private suspend fun childAtBegin(obj: SequenceEntry<T>): ChildEntry {
        val children = transform(obj.item)
        return ChildEntry(children, 0, obj)
    }

    private suspend fun childAtEnd(obj: SequenceEntry<T>): ChildEntry {
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

        override suspend fun previous(): SequenceEntry<R> {
            return if (index > 0) ChildEntry(children, index - 1, root) else childAtEnd(root.previous())
        }

        override suspend fun next(): SequenceEntry<R> {
            return if (index < children.size - 1) ChildEntry(children, index + 1, root) else childAtBegin(root.next())
        }
    }
}