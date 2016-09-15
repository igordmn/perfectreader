package com.dmi.util.collection

import com.dmi.test.shouldEqual
import org.junit.Test

class SearchTest {
    @Test
    fun listBinarySearchLower() {
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
    fun listBinarySearchGreater() {
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
    
    @Test
    fun floatBinarySearchLower() {
        floatArrayOf().binarySearchLower(9F) shouldEqual -1
        floatArrayOf(1F).binarySearchLower(9F) shouldEqual 0
        floatArrayOf(1F).binarySearchLower(1F) shouldEqual -1
        floatArrayOf(0F).binarySearchLower(1F) shouldEqual 0
        floatArrayOf(4F, 7F).binarySearchLower(8F) shouldEqual 1
        floatArrayOf(4F, 7F).binarySearchLower(7F) shouldEqual 0
        floatArrayOf(4F, 7F).binarySearchLower(5F) shouldEqual 0
        floatArrayOf(4F, 7F).binarySearchLower(4F) shouldEqual -1
        floatArrayOf(4F, 7F, 8F).binarySearchLower(9F) shouldEqual 2
        floatArrayOf(4F, 7F, 8F).binarySearchLower(8F) shouldEqual 1
        floatArrayOf(4F, 7F, 8F).binarySearchLower(5F) shouldEqual 0
        floatArrayOf(4F, 7F, 8F).binarySearchLower(4F) shouldEqual -1
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(338F) shouldEqual 6
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(337F) shouldEqual 5
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(334F) shouldEqual 5
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(333F) shouldEqual 4
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(57F) shouldEqual 4
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(56F) shouldEqual 3
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(16F) shouldEqual 3
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(15F) shouldEqual 2
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(9F) shouldEqual 2
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(8F) shouldEqual 1
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(7F) shouldEqual 0
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(5F) shouldEqual 0
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchLower(4F) shouldEqual -1
    }
    @Test
    fun floatBinarySearchGreater() {
        floatArrayOf().binarySearchGreater(9F) shouldEqual 0
        floatArrayOf(1F).binarySearchGreater(-9F) shouldEqual 0
        floatArrayOf(1F).binarySearchGreater(1F) shouldEqual 1
        floatArrayOf(0F).binarySearchGreater(-1F) shouldEqual 0
        floatArrayOf(4F, 7F).binarySearchGreater(3F) shouldEqual 0
        floatArrayOf(4F, 7F).binarySearchGreater(4F) shouldEqual 1
        floatArrayOf(4F, 7F).binarySearchGreater(6F) shouldEqual 1
        floatArrayOf(4F, 7F).binarySearchGreater(7F) shouldEqual 2
        floatArrayOf(4F, 7F, 8F).binarySearchGreater(3F) shouldEqual 0
        floatArrayOf(4F, 7F, 8F).binarySearchGreater(4F) shouldEqual 1
        floatArrayOf(4F, 7F, 8F).binarySearchGreater(7F) shouldEqual 2
        floatArrayOf(4F, 7F, 8F).binarySearchGreater(8F) shouldEqual 3
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(3F) shouldEqual 0
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(4F) shouldEqual 1
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(6F) shouldEqual 1
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(7F) shouldEqual 2
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(8F) shouldEqual 3
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(14F) shouldEqual 3
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(15F) shouldEqual 4
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(55F) shouldEqual 4
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(56F) shouldEqual 5
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(332F) shouldEqual 5
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(333F) shouldEqual 6
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(336F) shouldEqual 6
        floatArrayOf(4F, 7F, 8F, 15F, 56F, 333F, 337F).binarySearchGreater(337F) shouldEqual 7
    }
}