package com.dmi.util.graphic

import com.dmi.test.shouldBe
import org.junit.Test

class RectUtilsTest {
    @Test
    fun sqrDistanceToRect() {
        sqrDistanceToRect(-15F, -19F, -10F, -10F, 10F, 10F) shouldBe 25F + 81F
        sqrDistanceToRect(-9F, -19F, -10F, -10F, 10F, 10F) shouldBe 81F
        sqrDistanceToRect(18F, -19F, -10F, -10F, 10F, 10F) shouldBe 64F + 81F
        sqrDistanceToRect(-14F, 9F, -10F, -10F, 10F, 10F) shouldBe 16F
        sqrDistanceToRect(4F, 1F, -10F, -10F, 10F, 10F) shouldBe 0F
        sqrDistanceToRect(14F, 9F, -10F, -10F, 10F, 10F) shouldBe 16F
        sqrDistanceToRect(-14F, 49F, -10F, -10F, 10F, 10F) shouldBe 16F + 39F * 39F
        sqrDistanceToRect(1F, 12F, -10F, -10F, 10F, 10F) shouldBe 4F
        sqrDistanceToRect(11F, 12F, -10F, -10F, 10F, 10F) shouldBe 1F + 4F
    }
}