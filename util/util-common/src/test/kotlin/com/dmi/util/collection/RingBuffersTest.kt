package com.dmi.util.collection

import com.dmi.test.shouldEqual
import org.junit.Test

class RingBuffersTest {
    @Test
    fun testFloatRingBuffer() {
        val buffer = FloatRingBuffer(maxSize = 3)

        buffer.size shouldEqual 0

        buffer.add(4F)
        buffer.size shouldEqual 1
        buffer[0] shouldEqual 4F

        buffer.add(2F)
        buffer.size shouldEqual 2
        buffer[0] shouldEqual 2F
        buffer[1] shouldEqual 4F

        buffer.add(22F)
        buffer.size shouldEqual 3
        buffer[0] shouldEqual 22F
        buffer[1] shouldEqual 2F
        buffer[2] shouldEqual 4F

        buffer.add(23F)
        buffer.size shouldEqual 3
        buffer[0] shouldEqual 23F
        buffer[1] shouldEqual 22F
        buffer[2] shouldEqual 2F

        buffer.add(24F)
        buffer.size shouldEqual 3
        buffer[0] shouldEqual 24F
        buffer[1] shouldEqual 23F
        buffer[2] shouldEqual 22F

        buffer.add(25F)
        buffer.size shouldEqual 3
        buffer[0] shouldEqual 25F
        buffer[1] shouldEqual 24F
        buffer[2] shouldEqual 23F

        buffer.add(26F)
        buffer.size shouldEqual 3
        buffer[0] shouldEqual 26F
        buffer[1] shouldEqual 25F
        buffer[2] shouldEqual 24F

        buffer.add(27F)
        buffer.size shouldEqual 3
        buffer[0] shouldEqual 27F
        buffer[1] shouldEqual 26F
        buffer[2] shouldEqual 25F

        buffer.clear()
        buffer.size shouldEqual 0
    }
}