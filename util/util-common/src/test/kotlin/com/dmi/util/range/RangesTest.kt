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

        fun nearestTo(point: Int): IntRange = ranges[
                ranges.indexOfNearestRange({ this }, point)
        ]

        // expect
        nearestTo(0) shouldBe 10..20
        nearestTo(10) shouldBe 10..20
        nearestTo(19) shouldBe 10..20
        nearestTo(20) shouldBe 10..20
        nearestTo(25) shouldBe 10..20
        nearestTo(29) shouldBe 10..20
        nearestTo(30) shouldBe 30..40
        nearestTo(39) shouldBe 30..40
        nearestTo(40) shouldBe 30..40
        nearestTo(99) shouldBe 30..40
        nearestTo(100) shouldBe 100..200
        nearestTo(199) shouldBe 100..200
        nearestTo(200) shouldBe 200..300
        nearestTo(299) shouldBe 200..300
        nearestTo(300) shouldBe 300..400
        nearestTo(9999) shouldBe 300..400
        nearestTo(10000) shouldBe 10000..10001
        nearestTo(10001) shouldBe 10001..20000
        nearestTo(10002) shouldBe 10001..20000
        nearestTo(49999) shouldBe 31000..50000
        nearestTo(50000) shouldBe 50000..90000
        nearestTo(89999) shouldBe 50000..90000
        nearestTo(90000) shouldBe 50000..90000
        nearestTo(900000) shouldBe 50000..90000
    }
}