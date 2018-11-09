package com.dmi.perfectreader.book.page

import com.dmi.perfectreader.book.Locations
import com.dmi.perfectreader.book.content.location.LocatedSequence
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.util.lang.unsupported
import com.dmi.util.lang.value
import com.dmi.util.scope.Disposable
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observable
import kotlin.math.max
import kotlin.math.min

class LoadingPages(
        private val pages: Pages,
        private val maxRelativeIndex: Int = 10,
        private val scope: Scope = Scope()
) : Disposable by scope {
    companion object {
        fun pages(sequence: LocatedSequence<Page>, locations: Locations, pages: LocatedPages) = object : LoadingPages.Pages {
            override var location: Location by value(pages::location)
            override var pageNumber: Int
                get() = locations.locationToPageNumber(pages.location)
                set(value) {
                    pages.location = locations.pageNumberToLocation(value)
                }
            override val numberOfPages: Int get() = locations.numberOfPages
            override val sequence: LocatedSequence<Page> get() = sequence
        }
    }

    private var buffer by observable(PageBuffer(maxRelativeIndex))

    val goIndices: IntRange by scope.cached { minGoIndex..maxGoIndex }
    private val minGoIndex get() = min(1 - pages.pageNumber, buffer.shiftIndices.start)
    private val maxGoIndex get() = max(pages.numberOfPages - pages.pageNumber, buffer.shiftIndices.endInclusive)

    private var load = nextLoad()
    private var loadJob = loadJob()

    private fun loadJob() = scope.launch {
        while (!buffer.isCompleted) {
            load.perform()
            load = nextLoad()
        }
    }

    private fun checkLoad() {
        val loadIsValid = !loadJob.isCompleted && load.isValid
        if (!buffer.isCompleted && !loadIsValid) {
            load = nextLoad()
            loadJob.cancel()
            loadJob = loadJob()
        }
    }

    private fun nextLoad() = when {
        !buffer.middleIsComplete -> MiddleLoad()
        buffer.leftCount >= buffer.rightCount -> RightLoad()
        buffer.leftCount < buffer.rightCount -> LeftLoad()
        else -> unsupported()
    }

    private interface PageLoad {
        suspend fun perform()
        val isValid: Boolean
    }

    private inner class MiddleLoad : PageLoad {
        val location = pages.location

        override suspend fun perform() {
            buffer = buffer.apply { setMiddle(pages.sequence.get(location)) }
        }

        override val isValid get() = location == pages.location
    }

    private inner class RightLoad : PageLoad {
        val rightEntry = buffer.right

        override suspend fun perform() {
            buffer = buffer.apply { addRight(rightEntry?.nextOrNull()) }
        }

        override val isValid get() = !buffer.rightIsComplete && buffer.right == rightEntry
    }

    private inner class LeftLoad : PageLoad {
        val leftEntry = buffer.left

        override suspend fun perform() {
            buffer = buffer.apply { addLeft(leftEntry?.previousOrNull()) }
        }

        override val isValid get() = !buffer.leftIsComplete && buffer.left == leftEntry
    }

    fun goLocation(location: Location) {
        if (pages.location == location) return
        pages.location = location
        buffer = buffer.apply { clear() }
        checkLoad()
    }

    private fun goPageNumber(pageNumber: Int) {
        if (pages.pageNumber == pageNumber) return
        pages.pageNumber = pageNumber
        buffer = buffer.apply { clear() }
        checkLoad()
    }

    fun goRelative(relativeIndex: Int) {
        require(relativeIndex in goIndices)
        if (relativeIndex == 0) return

        when {
            relativeIndex in buffer.shiftIndices -> shift(relativeIndex)
            relativeIndex > -maxRelativeIndex && buffer[relativeIndex - 1] != null -> {
                goLocation(buffer[relativeIndex - 1]!!.item.range.endInclusive)
            }
            else -> goPageNumber((pages.pageNumber + relativeIndex).coerceIn(1..pages.numberOfPages))
        }
    }

    private fun shift(relativeIndex: Int) {
        buffer = buffer.apply { shift(relativeIndex) }
        pages.location = buffer[0]!!.item.range.start
        if (!isNextContinuous())
            buffer = buffer.apply { clearRight() }
        checkLoad()
    }

    private fun isNextContinuous(): Boolean = buffer[0]?.item?.isNextContinuous ?: true

    operator fun get(index: Int): Page? = if (index in (-maxRelativeIndex..maxRelativeIndex)) buffer[index]?.item else null

    interface Pages {
        var location: Location
        var pageNumber: Int
        val numberOfPages: Int
        val sequence: LocatedSequence<Page>
    }
}