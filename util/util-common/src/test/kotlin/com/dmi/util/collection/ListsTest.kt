package com.dmi.util.collection

import com.dmi.test.shouldBe
import org.junit.Test

class ListsTest {
    @Test
    fun removeAt() {
        listOf(0, 1, 2, 3, 4).removeAt(setOf(1, 3, 4)) shouldBe listOf(0, 2)
        listOf(0, 1, 2, 3, 4).removeAt(setOf(0)) shouldBe listOf(1, 2, 3, 4)
        listOf(0, 1, 2, 3, 4).removeAt(setOf(4)) shouldBe listOf(0, 1, 2, 3)
        listOf(0, 1, 2, 3, 4).removeAt(emptySet()) shouldBe listOf(0, 1, 2, 3, 4)
        listOf(0, 1, 2, 3, 4).removeAt(setOf(0, 1, 2, 3, 4)) shouldBe emptyList()
    }
}