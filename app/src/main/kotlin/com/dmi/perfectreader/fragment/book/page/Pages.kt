package com.dmi.perfectreader.fragment.book.page

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.util.collection.DuplexBuffer
import com.dmi.util.collection.SequenceEntry
import com.dmi.util.lang.clamp

class Pages(
        initialLocation: Location
) {
    companion object {
        val MAX_RELATIVE_INDEX = 10
    }

    var location: Location = initialLocation
        private set
    var isCurrentLoaded = false
        private set
    var leftLoadedCount = 0
        private set
    var rightLoadedCount = 0
        private set
    val isLeftLoaded: Boolean get() = leftLoadedCount == MAX_RELATIVE_INDEX
    val isRightLoaded: Boolean get() = rightLoadedCount == MAX_RELATIVE_INDEX
    val isLoaded: Boolean get() = isCurrentLoaded && isLeftLoaded && isRightLoaded

    val lastLoadedEntry: SequenceEntry<Page>? get() = currentEntries[rightLoadedCount]
    val firstLoadedEntry: SequenceEntry<Page>? get() = currentEntries[-leftLoadedCount]

    var minGoRelativeIndex = 0
    var maxGoRelativeIndex = 0

    private val currentEntries = DuplexBuffer<SequenceEntry<Page>>(MAX_RELATIVE_INDEX)

    fun goLocation(location: Location) {
        this.location = location
        currentEntries.clear()

        isCurrentLoaded = false
        leftLoadedCount = 0
        rightLoadedCount = 0
        minGoRelativeIndex = 0
        maxGoRelativeIndex = 0
    }

    fun goPage(relativeIndex: Int) {
        require(relativeIndex >= minGoRelativeIndex && relativeIndex <= maxGoRelativeIndex)
        if (relativeIndex == 0) return

        currentEntries.shift(-relativeIndex)
        leftLoadedCount = clamp(leftLoadedCount + relativeIndex, 0, MAX_RELATIVE_INDEX)
        rightLoadedCount = clamp(rightLoadedCount - relativeIndex, 0, MAX_RELATIVE_INDEX)
        minGoRelativeIndex = clamp(minGoRelativeIndex - relativeIndex, -MAX_RELATIVE_INDEX, MAX_RELATIVE_INDEX)
        maxGoRelativeIndex = clamp(maxGoRelativeIndex - relativeIndex, -MAX_RELATIVE_INDEX, MAX_RELATIVE_INDEX)
        location = get(0)!!.range.begin
    }

    fun isNextPagesValid(): Boolean {
        val current = get(0)
        val next = get(1)
        return current == null || next == null || current.range.end == next.range.begin
    }

    fun needReloadRight() {
        rightLoadedCount = 0
        maxGoRelativeIndex = 0
    }

    fun setLoadedCurrent(pageEntry: SequenceEntry<Page>?) {
        require(!isCurrentLoaded)
        isCurrentLoaded = true
        currentEntries[0] = pageEntry
    }

    fun addLoadedRight(pageEntry: SequenceEntry<Page>?) {
        require(!isRightLoaded)
        rightLoadedCount++
        currentEntries[rightLoadedCount] = pageEntry
        if (pageEntry != null)
            maxGoRelativeIndex++
    }

    fun addLoadedLeft(pageEntry: SequenceEntry<Page>?) {
        require(!isLeftLoaded)
        leftLoadedCount++
        currentEntries[-leftLoadedCount] = pageEntry
        if (pageEntry != null)
            minGoRelativeIndex--
    }

    operator fun get(relativeIndex: Int): Page? = currentEntries[relativeIndex]?.item
}