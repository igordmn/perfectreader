package com.dmi.util.collection

import com.dmi.test.shouldBe
import org.junit.Test

class SearchTest {
    @Test
    fun listBinarySearchLower() {
        emptyList<Int>().binarySearchLower(9) shouldBe -1
        listOf(1).binarySearchLower(9) shouldBe 0
        listOf(1).binarySearchLower(1) shouldBe -1
        listOf(0).binarySearchLower(1) shouldBe 0
        listOf(4, 7).binarySearchLower(8) shouldBe 1
        listOf(4, 7).binarySearchLower(7) shouldBe 0
        listOf(4, 7).binarySearchLower(5) shouldBe 0
        listOf(4, 7).binarySearchLower(4) shouldBe -1
        listOf(4, 7, 8).binarySearchLower(9) shouldBe 2
        listOf(4, 7, 8).binarySearchLower(8) shouldBe 1
        listOf(4, 7, 8).binarySearchLower(5) shouldBe 0
        listOf(4, 7, 8).binarySearchLower(4) shouldBe -1
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(338) shouldBe 6
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(337) shouldBe 5
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(334) shouldBe 5
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(333) shouldBe 4
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(57) shouldBe 4
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(56) shouldBe 3
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(16) shouldBe 3
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(15) shouldBe 2
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(9) shouldBe 2
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(8) shouldBe 1
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(7) shouldBe 0
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(5) shouldBe 0
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchLower(4) shouldBe -1
    }
    @Test
    fun listBinarySearchGreater() {
        emptyList<Int>().binarySearchGreater(9) shouldBe 0
        listOf(1).binarySearchGreater(-9) shouldBe 0
        listOf(1).binarySearchGreater(1) shouldBe 1
        listOf(0).binarySearchGreater(-1) shouldBe 0
        listOf(4, 7).binarySearchGreater(3) shouldBe 0
        listOf(4, 7).binarySearchGreater(4) shouldBe 1
        listOf(4, 7).binarySearchGreater(6) shouldBe 1
        listOf(4, 7).binarySearchGreater(7) shouldBe 2
        listOf(4, 7, 8).binarySearchGreater(3) shouldBe 0
        listOf(4, 7, 8).binarySearchGreater(4) shouldBe 1
        listOf(4, 7, 8).binarySearchGreater(7) shouldBe 2
        listOf(4, 7, 8).binarySearchGreater(8) shouldBe 3
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(3) shouldBe 0
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(4) shouldBe 1
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(6) shouldBe 1
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(7) shouldBe 2
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(8) shouldBe 3
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(14) shouldBe 3
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(15) shouldBe 4
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(55) shouldBe 4
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(56) shouldBe 5
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(332) shouldBe 5
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(333) shouldBe 6
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(336) shouldBe 6
        listOf(4, 7, 8, 15, 56, 333, 337).binarySearchGreater(337) shouldBe 7
    }
    
    @Test
    fun floatBinarySearchLower() {
        floatArrayOf().binarySearchLower(9F) shouldBe -1
        floatArrayOf(1F).binarySearchLower(9F) shouldBe 0
        floatArrayOf(1F).binarySearchLower(1F) shouldBe -1
        floatArrayOf(0F).binarySearchLower(1F) shouldBe 0
        floatArrayOf(4F, 7F).binarySearchLower(8F) shouldBe 1
        floatArrayOf(4F, 7F).binarySearchLower(7F) shouldBe 0
        floatArrayOf(4F, 7F).binarySearchLower(5F) shouldBe 0
        floatArrayOf(4F, 7F).binarySearchLower(4F) shouldBe -1
        floatArrayOf(4F, 7F, 8F).binarySearchLower(9F) shouldBe 2
        floatArrayOf(4F, 7F, 8F).binarySearchLower(8F) shouldBe 1
        floatArrayOf(4F, 7F, 8F).binarySearchLower(5F) shouldBe 0
        floatArrayOf(4F, 7F, 8F).binarySearchLower(4F) shouldBe -1
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(338F) shouldBe 6
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(337F) shouldBe 5
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(334F) shouldBe 5
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(333F) shouldBe 4
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(57F) shouldBe 4
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(56F) shouldBe 3
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(16F) shouldBe 3
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(15F) shouldBe 2
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(9F) shouldBe 2
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(8F) shouldBe 1
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(7F) shouldBe 0
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(5F) shouldBe 0
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(4F) shouldBe -1
    }
    @Test
    fun floatBinarySearchGreater() {
        floatArrayOf().binarySearchGreater(9F) shouldBe 0
        floatArrayOf(1F).binarySearchGreater(-9F) shouldBe 0
        floatArrayOf(1F).binarySearchGreater(1F) shouldBe 1
        floatArrayOf(0F).binarySearchGreater(-1F) shouldBe 0
        floatArrayOf(4F, 7F).binarySearchGreater(3F) shouldBe 0
        floatArrayOf(4F, 7F).binarySearchGreater(4F) shouldBe 1
        floatArrayOf(4F, 7F).binarySearchGreater(6F) shouldBe 1
        floatArrayOf(4F, 7F).binarySearchGreater(7F) shouldBe 2
        floatArrayOf(4F, 7F, 8F).binarySearchGreater(3F) shouldBe 0
        floatArrayOf(4F, 7F, 8F).binarySearchGreater(4F) shouldBe 1
        floatArrayOf(4F, 7F, 8F).binarySearchGreater(7F) shouldBe 2
        floatArrayOf(4F, 7F, 8F).binarySearchGreater(8F) shouldBe 3
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(3F) shouldBe 0
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(4F) shouldBe 1
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(6F) shouldBe 1
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(7F) shouldBe 2
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(8F) shouldBe 3
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(14F) shouldBe 3
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(15F) shouldBe 4
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(55F) shouldBe 4
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(56F) shouldBe 5
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(332F) shouldBe 5
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(333F) shouldBe 6
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(336F) shouldBe 6
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(337F) shouldBe 7
    }
}