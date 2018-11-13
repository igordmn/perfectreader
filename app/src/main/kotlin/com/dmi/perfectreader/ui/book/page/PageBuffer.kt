package com.dmi.perfectreader.ui.book.page

import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.util.collection.DuplexBuffer
import com.dmi.util.collection.SequenceEntry

class PageBuffer(
        private val maxRelativeIndex: Int
) {
    var middleIsComplete = false
        private set
    var leftCount = 0
        private set
    var rightCount = 0
        private set
    val leftIsComplete: Boolean get() = leftCount == maxRelativeIndex
    val rightIsComplete: Boolean get() = rightCount == maxRelativeIndex
    val isCompleted: Boolean get() = middleIsComplete && leftIsComplete && rightIsComplete

    val right: SequenceEntry<Page>? get() = entries[rightCount]
    val left: SequenceEntry<Page>? get() = entries[-leftCount]

    var shiftIndices: IntRange = 0..0
        private set

    private val entries = DuplexBuffer<SequenceEntry<Page>>(maxRelativeIndex)

    fun clear() {
        entries.clear()
        middleIsComplete = false
        leftCount = 0
        rightCount = 0
        shiftIndices = 0..0
    }

    fun clearRight() {
        for (i in 1..maxRelativeIndex) {
            entries[i] = null
        }
        rightCount = 0
        shiftIndices = shiftIndices.start..0
    }

    fun shift(relativeIndex: Int) {
        require(relativeIndex in shiftIndices)
        if (relativeIndex == 0) return

        entries.shift(-relativeIndex)
        leftCount = (leftCount + relativeIndex).coerceIn(0, maxRelativeIndex)
        rightCount = (rightCount - relativeIndex).coerceIn(0, maxRelativeIndex)
        val start = (shiftIndices.start - relativeIndex).coerceIn(-maxRelativeIndex, maxRelativeIndex)
        val endInclusive = (shiftIndices.endInclusive - relativeIndex).coerceIn(-maxRelativeIndex, maxRelativeIndex)
        shiftIndices = start..endInclusive
    }

    fun setMiddle(entry: SequenceEntry<Page>?) {
        require(!middleIsComplete && leftCount == 0 && rightCount == 0)
        middleIsComplete = true
        entries[0] = entry
    }

    fun addRight(entry: SequenceEntry<Page>?) {
        require(middleIsComplete && !rightIsComplete)
        rightCount++
        entries[rightCount] = entry
        if (entry != null)
            shiftIndices = shiftIndices.start..shiftIndices.endInclusive + 1
    }

    fun addLeft(entry: SequenceEntry<Page>?) {
        require(middleIsComplete && !leftIsComplete)
        leftCount++
        entries[-leftCount] = entry
        if (entry != null)
            shiftIndices = shiftIndices.start - 1..shiftIndices.endInclusive
    }

    operator fun get(relativeIndex: Int): SequenceEntry<Page>? = entries[relativeIndex]
}