package com.dmi.util.collection

import com.dmi.test.shouldEqual
import org.junit.Test

class SearchTest {
    @Test
    fun binarySearchLower() {
        emptyList<Int>().binarySearchLower(9) shouldEqual -1
        listOf(1).binarySearchLower(9) shouldEqual 0
        listOf(1).binarySearchLower(1) shouldEqual -1
        listOf(0).binarySearchLower(1) shouldEqual 0
        listOf(4, 7).binarySearchLower(8) shouldEqual 1
        listOf(4, 7).binarySearchLower(7) shouldEqual 0
        listOf(4, 7).binarySearchLower(5) shouldEqual 0
        listOf(4, 7).binarySearchLower(4) shouldEqual -1
        listOf(4, 7, 8).binarySearchLower(9) shouldEqual 2
        listOf(4, 7, 8).binarySearchLower(8) shouldEqual 1
        listOf(4, 7, 8).binarySearchLower(5) shouldEqual 0
        listOf(4, 7, 8).binarySearchLower(4) shouldEqual -1
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(338) shouldEqual 6
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(337) shouldEqual 5
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(334) shouldEqual 5
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(333) shouldEqual 4
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(57) shouldEqual 4
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(56) shouldEqual 3
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(16) shouldEqual 3
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(15) shouldEqual 2
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(9) shouldEqual 2
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(8) shouldEqual 1
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(7) shouldEqual 0
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(5) shouldEqual 0
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(4) shouldEqual -1
    }
    @Test
    fun binarySearchGreater() {
        emptyList<Int>().binarySearchGreater(9) shouldEqual 0
        listOf(1).binarySearchGreater(-9) shouldEqual 0
        listOf(1).binarySearchGreater(1) shouldEqual 1
        listOf(0).binarySearchGreater(-1) shouldEqual 0
        listOf(4, 7).binarySearchGreater(3) shouldEqual 0
        listOf(4, 7).binarySearchGreater(4) shouldEqual 1
        listOf(4, 7).binarySearchGreater(6) shouldEqual 1
        listOf(4, 7).binarySearchGreater(7) shouldEqual 2
        listOf(4, 7, 8).binarySearchGreater(3) shouldEqual 0
        listOf(4, 7, 8).binarySearchGreater(4) shouldEqual 1
        listOf(4, 7, 8).binarySearchGreater(7) shouldEqual 2
        listOf(4, 7, 8).binarySearchGreater(8) shouldEqual 3
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(3) shouldEqual 0
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(4) shouldEqual 1
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(6) shouldEqual 1
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(7) shouldEqual 2
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(8) shouldEqual 3
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(14) shouldEqual 3
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(15) shouldEqual 4
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(55) shouldEqual 4
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(56) shouldEqual 5
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(332) shouldEqual 5
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(333) shouldEqual 6
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(336) shouldEqual 6
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(337) shouldEqual 7
    }
}