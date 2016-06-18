package com.dmi.util.collection

interface SequenceEntry<T> {
    val item: T
    val hasPrevious: Boolean
    val hasNext: Boolean
    val previous: SequenceEntry<T>
    val next: SequenceEntry<T>

    val previousOrNull: SequenceEntry<T>? get() = if (hasPrevious) previous else null
    val nextOrNull: SequenceEntry<T>? get() = if (hasNext) next else null
}

class ListSequenceEntry<T>(private val list: List<T>, private val index: Int) : SequenceEntry<T> {
    init {
        require(0 <= index && index < list.size)
    }

    override val item = list[index]
    override val hasPrevious = index > 0
    override val hasNext = index < list.size - 1

    override val previous: SequenceEntry<T> get() = ListSequenceEntry(list, index - 1)
    override val next: SequenceEntry<T> get() = ListSequenceEntry(list, index + 1)
}