package com.dmi.util.collection

import com.dmi.test.shouldBe
import org.junit.Test

class RingBuffersTest {
    @Test
    fun testDoubleRingBuffer() {
        val buffer = DoubleRingBuffer(maxSize = 3)

        buffer.size shouldBe 0

        buffer.add(4.0)
        buffer.size shouldBe 1
        buffer[0] shouldBe 4.0

        buffer.add(2.0)
        buffer.size shouldBe 2
        buffer[0] shouldBe 2.0
        buffer[1] shouldBe 4.0

        buffer.add(22.0)
        buffer.size shouldBe 3
        buffer[0] shouldBe 22.0
        buffer[1] shouldBe 2.0
        buffer[2] shouldBe 4.0

        buffer.add(23.0)
        buffer.size shouldBe 3
        buffer[0] shouldBe 23.0
        buffer[1] shouldBe 22.0
        buffer[2] shouldBe 2.0

        buffer.add(24.0)
        buffer.size shouldBe 3
        buffer[0] shouldBe 24.0
        buffer[1] shouldBe 23.0
        buffer[2] shouldBe 22.0

        buffer.add(25.0)
        buffer.size shouldBe 3
        buffer[0] shouldBe 25.0
        buffer[1] shouldBe 24.0
        buffer[2] shouldBe 23.0

        buffer.add(26.0)
        buffer.size shouldBe 3
        buffer[0] shouldBe 26.0
        buffer[1] shouldBe 25.0
        buffer[2] shouldBe 24.0

        buffer.add(27.0)
        buffer.size shouldBe 3
        buffer[0] shouldBe 27.0
        buffer[1] shouldBe 26.0
        buffer[2] shouldBe 25.0

        buffer.clear()
        buffer.size shouldBe 0
    }
}