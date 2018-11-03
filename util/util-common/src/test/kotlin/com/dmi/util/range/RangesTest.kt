package com.dmi.util.range

import com.dmi.test.shouldBe
import org.junit.Test

@Suppress("IllegalIdentifier")
class RangesTest {
    @Test
    fun `find nearest to list of ranged items`() {
        // given
        val ranges = listOf(
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

        fun searchOf(point: Int): IntRange = ranges[
                ranges.definitelySearchRangeIndex({ this }, location = point)
        ]

        // expect
        searchOf(0) shouldBe 10..20
        searchOf(10) shouldBe 10..20
        searchOf(19) shouldBe 10..20
        searchOf(20) shouldBe 30..40
        searchOf(25) shouldBe 30..40
        searchOf(29) shouldBe 30..40
        searchOf(30) shouldBe 30..40
        searchOf(39) shouldBe 30..40
        searchOf(40) shouldBe 100..200
        searchOf(99) shouldBe 100..200
        searchOf(100) shouldBe 100..200
        searchOf(199) shouldBe 100..200
        searchOf(200) shouldBe 200..300
        searchOf(299) shouldBe 200..300
        searchOf(300) shouldBe 300..400
        searchOf(400) shouldBe 10000..10001
        searchOf(9999) shouldBe 10000..10001
        searchOf(10000) shouldBe 10000..10001
        searchOf(10001) shouldBe 10001..20000
        searchOf(10002) shouldBe 10001..20000
        searchOf(19999) shouldBe 10001..20000
        searchOf(20000) shouldBe 21000..30000
        searchOf(20999) shouldBe 21000..30000
        searchOf(21000) shouldBe 21000..30000
        searchOf(29999) shouldBe 21000..30000
        searchOf(30000) shouldBe 31000..50000
        searchOf(49999) shouldBe 31000..50000
        searchOf(50000) shouldBe 50000..90000
        searchOf(89999) shouldBe 50000..90000
        searchOf(90000) shouldBe 50000..90000
        searchOf(900000) shouldBe 50000..90000
    }
}