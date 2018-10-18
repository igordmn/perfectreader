package com.dmi.perfectreader.search

import com.dmi.test.shouldBe
import com.dmi.test.shouldThrow
import org.junit.Test

class SearchUITest {
    @Test
    fun `test indicesOf`() {
        shouldThrow<IllegalArgumentException> {
            "aa bbb aa bbb bbb a b".indicesOf("")
        }
        "aa bbb aa bbb bbb a b".indicesOf("a").toList() shouldBe listOf(0..0, 1..1, 7..7, 8..8, 18..18)
        "aa bbb aa bbb bbb a b".indicesOf("aa").toList() shouldBe listOf(0..1, 7..8)
        "aa bbb aa bbb bbb a b".indicesOf("aaa").toList() shouldBe emptyList()
        "aa bbb aa bbb bbb a b".indicesOf("b").toList() shouldBe listOf(3..3, 4..4, 5..5, 10..10, 11..11, 12..12, 14..14, 15..15, 16..16, 20..20)
        "aa bbb aa bbb bbb a b".indicesOf("bb").toList() shouldBe listOf(3..4, 4..5, 10..11, 11..12, 14..15, 15..16)
        "aa bbb aa bbb bbb a b".indicesOf("bbb").toList() shouldBe listOf(3..5, 10..12, 14..16)
        "aa bbb aa bbb bbb a b".indicesOf("bbbb").toList() shouldBe emptyList()
        "aa bbb aa bbb bbb a b".indicesOf("c").toList() shouldBe emptyList()
        "aa bbb aa bbb bbb a b".indicesOf("ccccccccccccccccccccccccccccccccccccccccccccccccccc").toList() shouldBe emptyList()
    }

    @Test
    fun `test adjacentText`() {
        adjacentText("aaaaAAAAbbb", 4..7, 0) shouldBe AdjacentText("…AAAA…", 1..4)
        adjacentText("aaaaAAAAbbb", 4..7, 1) shouldBe AdjacentText("…aAAAAb…", 2..5)
        adjacentText("aaaaAAAAbbb", 4..7, 2) shouldBe AdjacentText("…aaAAAAbb…", 3..6)
        adjacentText("aaaaAAAAbbb", 4..7, 3) shouldBe AdjacentText("…aaaAAAAbbb", 4..7)
        adjacentText("aaaaAAAAbbb", 4..7, 4) shouldBe AdjacentText("aaaaAAAAbbb", 4..7)
        adjacentText("aaaaAAAAbbb", 4..7, 5) shouldBe AdjacentText("aaaaAAAAbbb", 4..7)
        adjacentText("aaaaAAAAbbb", 4..7, 100) shouldBe AdjacentText("aaaaAAAAbbb", 4..7)
    }
}