package com.dmi.util.graphic

import com.dmi.test.shouldEqual
import org.junit.Test

class RectUtilsTest {
    @Test
    fun sqrDistanceToRect() {
        sqrDistanceToRect(-15F, -19F, -10F, -10F, 10F, 10F) shouldEqual 25F + 81F
        sqrDistanceToRect(-9F, -19F, -10F, -10F, 10F, 10F) shouldEqual 81F
        sqrDistanceToRect(18F, -19F, -10F, -10F, 10F, 10F) shouldEqual 64F + 81F
        sqrDistanceToRect(-14F, 9F, -10F, -10F, 10F, 10F) shouldEqual 16F
        sqrDistanceToRect(4F, 1F, -10F, -10F, 10F, 10F) shouldEqual 0F
        sqrDistanceToRect(14F, 9F, -10F, -10F, 10F, 10F) shouldEqual 16F
        sqrDistanceToRect(-14F, 49F, -10F, -10F, 10F, 10F) shouldEqual 16F + 39F * 39F
        sqrDistanceToRect(1F, 12F, -10F, -10F, 10F, 10F) shouldEqual 4F
        sqrDistanceToRect(11F, 12F, -10F, -10F, 10F, 10F) shouldEqual 1F + 4F
    }
}