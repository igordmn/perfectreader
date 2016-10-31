package com.dmi.util.collection

import com.dmi.test.shouldEqual
import org.junit.Test

class RingBuffersTest {
    @Test
    fun testDoubleRingBuffer() {
        val buffer = DoubleRingBuffer(maxSize = 3)

        buffer.size shouldEqual 0

        buffer.add(4.0)
        buffer.size shouldEqual 1
        buffer[0] shouldEqual 4.0

        buffer.add(2.0)
        buffer.size shouldEqual 2
        buffer[0] shouldEqual 2.0
        buffer[1] shouldEqual 4F

        buffer.add(22.0)
        buffer.size shouldEqual 3
        buffer[0] shouldEqual 22.0
        buffer[1] shouldEqual 2.0
        buffer[2] shouldEqual 4F

        buffer.add(23.0)
        buffer.size shouldEqual 3
        buffer[0] shouldEqual 23F
        buffer[1] shouldEqual 22.0
        buffer[2] shouldEqual 2.0

        buffer.add(24.0)
        buffer.size shouldEqual 3
        buffer[0] shouldEqual 24F
        buffer[1] shouldEqual 23F
        buffer[2] shouldEqual 22.0

        buffer.add(25.0)
        buffer.size shouldEqual 3
        buffer[0] shouldEqual 25F
        buffer[1] shouldEqual 24F
        buffer[2] shouldEqual 23F

        buffer.add(26.0)
        buffer.size shouldEqual 3
        buffer[0] shouldEqual 26F
        buffer[1] shouldEqual 25F
        buffer[2] shouldEqual 24F

        buffer.add(27.0)
        buffer.size shouldEqual 3
        buffer[0] shouldEqual 27F
        buffer[1] shouldEqual 26F
        buffer[2] shouldEqual 25F

        buffer.clear()
        buffer.size shouldEqual 0
    }
}