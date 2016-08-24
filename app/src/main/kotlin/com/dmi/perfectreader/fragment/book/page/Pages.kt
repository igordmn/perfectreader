package com.dmi.perfectreader.fragment.book.page

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.pagination.page.Page
import com.dmi.util.collection.DuplexBuffer
import com.dmi.util.collection.SequenceEntry
import java.lang.Math.min

class Pages(
        initialLocation: Location
) {
    companion object {
        val MAX_RELATIVE_INDEX = 5
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

    private val currentEntries = DuplexBuffer<SequenceEntry<Page>>(MAX_RELATIVE_INDEX)

    fun canGoNextPage() = rightLoadedCount > 0 && currentEntries[1] != null
    fun canGoPreviousPage() = leftLoadedCount > 0 && currentEntries[-1] != null

    fun goLocation(location: Location) {
        this.location = location
        currentEntries.clear()

        isCurrentLoaded = false
        leftLoadedCount = 0
        rightLoadedCount = 0
    }

    fun goNextPage() {
        require(canGoNextPage())
        currentEntries.shiftLeft()
        location = get(0)!!.range.begin

        require(rightLoadedCount > 0)
        leftLoadedCount = min(leftLoadedCount + 1, MAX_RELATIVE_INDEX)
        rightLoadedCount--
    }

    fun goPreviousPage() {
        require(canGoPreviousPage())
        currentEntries.shiftRight()
        location = get(0)!!.range.begin

        require(leftLoadedCount > 0)
        leftLoadedCount--
        rightLoadedCount = min(rightLoadedCount + 1, MAX_RELATIVE_INDEX)
    }

    fun isNextPagesValid(): Boolean {
        val current = get(0)
        val next = get(1)
        return current == null || next == null || current.range.end == next.range.begin
    }

    fun needReloadAll() {
        isCurrentLoaded = false
        leftLoadedCount = 0
        rightLoadedCount = 0
    }

    fun needReloadRight() {
        rightLoadedCount = 0
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
    }

    fun addLoadedLeft(pageEntry: SequenceEntry<Page>?) {
        require(!isLeftLoaded)
        leftLoadedCount++
        currentEntries[-leftLoadedCount] = pageEntry
    }

    operator fun get(relativeIndex: Int): Page? = currentEntries[relativeIndex]?.item
}