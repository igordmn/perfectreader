package com.dmi.util.collection

interface SequenceEntry<T> {
    val item: T
    val hasPrevious: Boolean
    val hasNext: Boolean
    suspend fun previous(): SequenceEntry<T>
    suspend fun next(): SequenceEntry<T>

    suspend fun previousOrNull(): SequenceEntry<T>? = if (hasPrevious) previous() else null
    suspend fun nextOrNull(): SequenceEntry<T>? = if (hasNext) next() else null
}

class ListSequenceEntry<T>(private val list: List<T>, private val index: Int) : SequenceEntry<T> {
    init {
        require(0 <= index && index < list.size)
    }

    override val item = list[index]
    override val hasPrevious = index > 0
    override val hasNext = index < list.size - 1

    override suspend fun previous(): SequenceEntry<T> = ListSequenceEntry(list, index - 1)
    override suspend fun next(): SequenceEntry<T> = ListSequenceEntry(list, index + 1)
}

fun <T> List<T>.asSequenceEntry(index: Int): SequenceEntry<T> = ListSequenceEntry(this, index)