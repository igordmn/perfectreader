package com.dmi.perfectreader.book.page

import com.dmi.perfectreader.book.content.location.LocatedSequence
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.map
import com.dmi.perfectreader.book.pagination.page.Page
import com.dmi.test.shouldBe
import com.dmi.util.collection.SequenceEntry
import com.dmi.util.collection.asSequenceEntry
import com.dmi.util.coroutine.initThreadContext
import com.dmi.util.lang.intRound
import com.dmi.util.lang.max
import com.dmi.util.lang.min
import com.dmi.util.scope.EmittableEvent
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Test

class LoadingPagesTest {
    private val context = newSingleThreadContext("test")
    private val maxRelativeIndex = 2

    init {
        runBlocking(context) {
            initThreadContext(context)
        }
    }

    class TestPages(
            override val numberOfPages: Int,
            private val pageSize: Int,
            private val previousOverlap: Int   // previousPage.range.end = currentPage.range.begin + previousOverlap
    ) : LoadingPages.Pages {
        private var resumeLoad = EmittableEvent()
        private val end: Double = pageNumberToLocation(numberOfPages + 1).offset

        fun location(offset: Int) = Location(offset.toDouble())

        suspend fun load() {
            startLoad()
            finishLoad()
        }

        suspend fun startLoad() {
            yield()
        }

        private suspend fun finishLoad() {
            resumeLoad.emit()
            yield()
        }

        override var location = Location(0.0)
            set(value) {
                require(value.offset in 0.0..end)
                field = value
            }
        override var pageNumber
            get() = locationToPageNumber(location)
            set(value) {
                require(pageNumber in 1..numberOfPages)
                location = pageNumberToLocation(value)
            }
        override val sequence = object : LocatedSequence<Page> {
            override suspend fun get(location: Location) = TestSequenceEntry(location.offset)
        }.map { resumeLoad.wait(); it }

        private inner class TestSequenceEntry(private val start: Double) : SequenceEntry<Page> {
            val end = min(this@TestPages.end, start + pageSize)
            override val item: Page = testPage(start..end)
            override val hasPrevious: Boolean = start > 0.0
            override val hasNext: Boolean = end < this@TestPages.end
            override suspend fun previous() = TestSequenceEntry(max(0.0, start - pageSize + previousOverlap))
            override suspend fun next() = TestSequenceEntry(end)
        }

        fun locationToPageNumber(location: Location) = 1 + (location.offset / pageSize).toInt()
        fun pageNumberToLocation(pageNumber: Int) = Location(((pageNumber - 1) * pageSize).toDouble())
    }

    private val LoadingPages.pageRanges: List<IntRange?>
        get() {
            return (-maxRelativeIndex..maxRelativeIndex).map {
                this[it]?.range?.let {
                    intRound(it.start.offset)..intRound(it.endInclusive.offset)
                }
            }
        }

    @Test
    fun `single page`() = runBlocking(context) {
        val pages = object : LoadingPages.Pages {
            override var location = Location(0.0)
                set(value) {
                    require(value.offset in 0.0..10.0)
                    field = value
                }
            override var pageNumber
                get() = 1
                set(_) {
                    location = Location(0.0)
                }
            override val numberOfPages = 1
            override val sequence = object : LocatedSequence<Page> {
                override suspend fun get(location: Location) = listOf(testPage(0.0..10.0)).asSequenceEntry(0)
            }
        }
        val loadingPages = LoadingPages(pages, maxRelativeIndex)

        loadingPages.goIndices shouldBe 0..0
        loadingPages.pageRanges shouldBe listOf(null, null, null, null, null)

        yield()
        loadingPages.goIndices shouldBe 0..0
        loadingPages.pageRanges shouldBe listOf(null, null, 0..10, null, null)

        loadingPages.goLocation(Location(5.0))
        pages.location shouldBe Location(5.0)
        loadingPages.goIndices shouldBe 0..0
        loadingPages.pageRanges shouldBe listOf(null, null, null, null, null)

        yield()
        pages.location shouldBe Location(5.0)
        loadingPages.goIndices shouldBe 0..0
        loadingPages.pageRanges shouldBe listOf(null, null, 0..10, null, null)
    }

    @Test
    fun `initial pages`() = runBlocking(context) {
        val pages = TestPages(numberOfPages = 6, pageSize = 10, previousOverlap = 1)
        val loadingPages = LoadingPages(pages, maxRelativeIndex)

        loadingPages.goIndices shouldBe 0..5
        loadingPages.pageRanges shouldBe listOf(null, null, null, null, null)

        pages.load()
        loadingPages.goIndices shouldBe 0..5
        loadingPages.pageRanges shouldBe listOf(null, null, 0..10, null, null)

        pages.load()
        loadingPages.goIndices shouldBe 0..5
        loadingPages.pageRanges shouldBe listOf(null, null, 0..10, 10..20, null)

        pages.load()
        loadingPages.goIndices shouldBe 0..5
        loadingPages.pageRanges shouldBe listOf(null, null, 0..10, 10..20, 20..30)

        pages.load()
        loadingPages.goIndices shouldBe 0..5
        loadingPages.pageRanges shouldBe listOf(null, null, 0..10, 10..20, 20..30)
    }

    @Test
    fun `go to end`() = runBlocking(context) {
        val pages = TestPages(numberOfPages = 6, pageSize = 10, previousOverlap = 1)
        val loadingPages = LoadingPages(pages, maxRelativeIndex)

        loadingPages.goLocation(pages.location(59))
        loadingPages.goIndices shouldBe -5..0
        loadingPages.pageRanges shouldBe listOf(null, null, null, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -5..0
        loadingPages.pageRanges shouldBe listOf(null, null, 59..60, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -5..0
        loadingPages.pageRanges shouldBe listOf(null, 50..60, 59..60, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -5..0
        loadingPages.pageRanges shouldBe listOf(41..51, 50..60, 59..60, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -5..0
        loadingPages.pageRanges shouldBe listOf(41..51, 50..60, 59..60, null, null)
    }

    @Test
    fun `go to middle`() = runBlocking(context) {
        val pages = TestPages(numberOfPages = 6, pageSize = 10, previousOverlap = 1)
        val loadingPages = LoadingPages(pages, maxRelativeIndex)

        pages.locationToPageNumber(pages.location(31)) shouldBe 4
        loadingPages.goLocation(pages.location(31))
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(null, null, null, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(null, null, 31..41, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(null, null, 31..41, 41..51, null)

        pages.load()
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(null, 22..32, 31..41, 41..51, null)

        pages.load()
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(null, 22..32, 31..41, 41..51, 51..60)

        pages.load()
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(13..23, 22..32, 31..41, 41..51, 51..60)

        pages.load()
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(13..23, 22..32, 31..41, 41..51, 51..60)
    }

    @Test
    fun `go during load`() = runBlocking(context) {
        val pages = TestPages(numberOfPages = 6, pageSize = 10, previousOverlap = 1)
        val loadingPages = LoadingPages(pages, maxRelativeIndex)

        loadingPages.goIndices shouldBe 0..5
        loadingPages.pageRanges shouldBe listOf(null, null, null, null, null)

        pages.load()
        loadingPages.goIndices shouldBe 0..5
        loadingPages.pageRanges shouldBe listOf(null, null, 0..10, null, null)

        pages.startLoad()
        pages.locationToPageNumber(pages.location(31)) shouldBe 4
        loadingPages.goLocation(pages.location(31))
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(null, null, null, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(null, null, 31..41, null, null)

        pages.load()
        pages.load()
        pages.load()
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(null, 22..32, 31..41, 41..51, 51..60)

        pages.startLoad()
        loadingPages.goLocation(pages.location(0))
        loadingPages.goIndices shouldBe 0..5
        loadingPages.pageRanges shouldBe listOf(null, null, null, null, null)

        pages.load()
        loadingPages.goIndices shouldBe 0..5
        loadingPages.pageRanges shouldBe listOf(null, null, 0..10, null, null)
    }

    @Test
    fun `go forward and backward fully loaded`() = runBlocking(context) {
        val pages = TestPages(numberOfPages = 6, pageSize = 10, previousOverlap = 1)
        val loadingPages = LoadingPages(pages, maxRelativeIndex)

        pages.load()
        pages.load()
        pages.load()
        loadingPages.goIndices shouldBe 0..5
        loadingPages.pageRanges shouldBe listOf(null, null, 0..10, 10..20, 20..30)

        loadingPages.goRelative(1)
        pages.location shouldBe Location(10.0)
        loadingPages.goIndices shouldBe -1..4
        loadingPages.pageRanges shouldBe listOf(null, 0..10, 10..20, 20..30, null)

        pages.load()
        loadingPages.goIndices shouldBe -1..4
        loadingPages.pageRanges shouldBe listOf(null, 0..10, 10..20, 20..30, 30..40)

        loadingPages.goRelative(2)
        pages.location shouldBe Location(30.0)
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(10..20, 20..30, 30..40, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(10..20, 20..30, 30..40, 40..50, null)

        pages.load()
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(10..20, 20..30, 30..40, 40..50, 50..60)

        loadingPages.goRelative(2)
        pages.location shouldBe Location(50.0)
        loadingPages.goIndices shouldBe -5..0
        loadingPages.pageRanges shouldBe listOf(30..40, 40..50, 50..60, null, null)

        loadingPages.goRelative(-1)
        loadingPages.goIndices shouldBe -4..1
        loadingPages.pageRanges shouldBe listOf(null, 30..40, 40..50, 50..60, null)

        pages.load()
        pages.location shouldBe Location(40.0)
        loadingPages.goIndices shouldBe -4..1
        loadingPages.pageRanges shouldBe listOf(21..31, 30..40, 40..50, 50..60, null)

        loadingPages.goRelative(-2)
        pages.location shouldBe Location(21.0)
        loadingPages.goIndices shouldBe -2..3
        loadingPages.pageRanges shouldBe listOf(null, null, 21..31, null, null)

        pages.load()
        pages.load()
        pages.load()
        pages.load()
        loadingPages.goIndices shouldBe -2..3
        loadingPages.pageRanges shouldBe listOf(3..13, 12..22, 21..31, 31..41, 41..51)

        loadingPages.goRelative(-1)
        pages.location shouldBe Location(12.0)
        loadingPages.goIndices shouldBe -1..4
        loadingPages.pageRanges shouldBe listOf(null, 3..13, 12..22, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -1..4
        loadingPages.pageRanges shouldBe listOf(null, 3..13, 12..22, 22..32, null)

        pages.load()
        loadingPages.goIndices shouldBe -1..4
        loadingPages.pageRanges shouldBe listOf(null, 3..13, 12..22, 22..32, 32..42)

        pages.load()
        pages.load()
        loadingPages.goIndices shouldBe -2..4
        loadingPages.pageRanges shouldBe listOf(0..10, 3..13, 12..22, 22..32, 32..42)

        loadingPages.goRelative(-2)
        pages.location shouldBe Location(0.0)
        loadingPages.goIndices shouldBe 0..5
        loadingPages.pageRanges shouldBe listOf(null, null, 0..10, null, null)

        pages.load()
        pages.load()
        loadingPages.goIndices shouldBe 0..5
        loadingPages.pageRanges shouldBe listOf(null, null, 0..10, 10..20, 20..30)
    }

    @Test
    fun `go to not loaded page`() = runBlocking(context) {
        val pages = TestPages(numberOfPages = 6, pageSize = 10, previousOverlap = 1)
        val loadingPages = LoadingPages(pages, maxRelativeIndex)

        pages.load()
        pages.load()
        loadingPages.goIndices shouldBe 0..5
        loadingPages.pageRanges shouldBe listOf(null, null, 0..10, 10..20, null)

        loadingPages.goRelative(2)
        pages.location shouldBe Location(20.0)
        loadingPages.goIndices shouldBe -2..3
        loadingPages.pageRanges shouldBe listOf(null, null, null, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -2..3
        loadingPages.pageRanges shouldBe listOf(null, null, 20..30, null, null)

        loadingPages.goRelative(2)
        pages.location shouldBe Location(40.0)
        loadingPages.goIndices shouldBe -4..1
        loadingPages.pageRanges shouldBe listOf(null, null, null, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -4..1
        loadingPages.pageRanges shouldBe listOf(null, null, 40..50, null, null)

        loadingPages.goLocation(Location(39.0))
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(null, null, null, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(null, null, 39..49, null, null)

        loadingPages.goRelative(1)
        pages.location shouldBe Location(49.0)
        loadingPages.goIndices shouldBe -4..1
        loadingPages.pageRanges shouldBe listOf(null, null, null, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -4..1
        loadingPages.pageRanges shouldBe listOf(null, null, 49..59, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -4..1
        loadingPages.pageRanges shouldBe listOf(null, null, 49..59, 59..60, null)

        loadingPages.goRelative(-1)
        pages.location shouldBe Location(30.0)
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(null, null, null, null, null)

        pages.load()
        loadingPages.goIndices shouldBe -3..2
        loadingPages.pageRanges shouldBe listOf(null, null, 30..40, null, null)
    }
}