package com.dmi.util.collection

import com.dmi.test.shouldBe
import com.dmi.test.shouldThrow
import org.junit.Test

class LinkedCacheTest {
    @Test
    fun `add and remove values`() {
        val cache = LinkedCache<Int>()
        cache.toList() shouldBe emptyList()

        val entry0 = cache.add(0)
        cache.toList() shouldBe listOf(0)

        entry0.remove()
        cache.toList() shouldBe emptyList()

        val entry1 = cache.add(1)
        val entry2 = cache.add(2)
        val entry3 = cache.add(3)
        val entry4 = cache.add(4)
        cache.toList() shouldBe listOf(1, 2, 3, 4)

        entry1.remove()
        cache.toList() shouldBe listOf(2, 3, 4)

        entry3.remove()
        cache.toList() shouldBe listOf(2, 4)

        shouldThrow<IllegalStateException> {
            entry3.remove()
        }

        val entry5 = cache.add(5)
        cache.toList() shouldBe listOf(2, 4, 5)

        entry2.remove()
        cache.toList() shouldBe listOf(4, 5)

        entry5.remove()
        cache.toList() shouldBe listOf(4)

        entry4.remove()
        cache.toList() shouldBe emptyList()
    }
}