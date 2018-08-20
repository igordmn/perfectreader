package com.dmi.util.lang

import com.dmi.test.shouldBe
import org.junit.Test

class StringsExtTest {
    @Test
    fun splitIntoTwoLines() {
        "".splitIntoTwoLines() shouldBe ("" to null)
        "1".splitIntoTwoLines() shouldBe ("1" to null)
        "12".splitIntoTwoLines() shouldBe ("12" to null)
        "123".splitIntoTwoLines() shouldBe ("123" to null)
        " ".splitIntoTwoLines() shouldBe ("" to null)
        "  ".splitIntoTwoLines() shouldBe ("" to null)
        " 1 ".splitIntoTwoLines() shouldBe ("1" to null)
        " 123 ".splitIntoTwoLines() shouldBe ("123" to null)
        " 12 3 ".splitIntoTwoLines() shouldBe ("12" to "3")
        " 1 23 ".splitIntoTwoLines() shouldBe ("1" to "23")
        "1 2".splitIntoTwoLines() shouldBe ("1" to "2")
        "1 2 3".splitIntoTwoLines() shouldBe ("1" to "2 3")
        "1 2223 5 3".splitIntoTwoLines() shouldBe ("1 2223" to "5 3")
    }
}