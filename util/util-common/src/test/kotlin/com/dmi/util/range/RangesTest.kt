package com.dmi.util.range

import org.amshove.kluent.shouldEqual
import org.junit.Test

@Suppress("IllegalIdentifier")
class RangesTest {
    @Test
    fun `find nearest to list of ranged items`() {
        // given
        val ranges = ranges(
                10..20,
                30..40,
                100..200,
                200..300,
                300..400,
                10000..10001,
                10001..10001,
                10001..20000,
                21000..30000,
                31000..50000,
                50000..90000
        )

        fun nearestTo(point: Int): IntRange {
            val index = ranges.indexOfNearestRange({ this }, point)
            return intRange(ranges[index])
        }

        // expect
        nearestTo(0) shouldEqual 10..20
        nearestTo(10) shouldEqual 10..20
        nearestTo(19) shouldEqual 10..20
        nearestTo(20) shouldEqual 10..20
        nearestTo(25) shouldEqual 10..20
        nearestTo(29) shouldEqual 10..20
        nearestTo(30) shouldEqual 30..40
        nearestTo(39) shouldEqual 30..40
        nearestTo(40) shouldEqual 30..40
        nearestTo(99) shouldEqual 30..40
        nearestTo(100) shouldEqual 100..200
        nearestTo(199) shouldEqual 100..200
        nearestTo(200) shouldEqual 200..300
        nearestTo(299) shouldEqual 200..300
        nearestTo(300) shouldEqual 300..400
        nearestTo(9999) shouldEqual 300..400
        nearestTo(10000) shouldEqual 10000..10001
        nearestTo(10001) shouldEqual 10001..20000
        nearestTo(10002) shouldEqual 10001..20000
        nearestTo(49999) shouldEqual 31000..50000
        nearestTo(50000) shouldEqual 50000..90000
        nearestTo(89999) shouldEqual 50000..90000
        nearestTo(90000) shouldEqual 50000..90000
        nearestTo(900000) shouldEqual 50000..90000
    }

    fun ranges(vararg intRanges: IntRange) = intRanges.map { range(it) }

    fun range(intRange: IntRange) = object: Range<Int> {
        override val begin = intRange.first
        override val end = intRange.last
    }

    fun intRange(range: Range<Int>) = IntRange(range.begin, range.end)
}