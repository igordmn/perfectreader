package com.dmi.util.input

import com.dmi.test.shouldEqual
import com.dmi.util.graphic.PositionF
import org.junit.Test
import java.lang.Math.round

class VelocityApproximatorTest {
    @Test
    fun zeroVelocity() {
        velocityAt(0.0) shouldEqual PositionF(0F, 0F)
        velocityAt(0.01,
                Info(0F, 0F, 0.01)
        ) shouldEqual PositionF(0F, 0F)
        velocityAt(0.03,
                Info(0F, 0F, 0.02),
                Info(0F, 0F, 0.03)
        ) shouldEqual PositionF(0F, 0F)
    }

    @Test
    fun constVelocity() {
        velocityAt(0.05,
                Info(2F, 2F, 0.01),
                Info(2F, 2F, 0.02),
                Info(2F, 2F, 0.03),
                Info(2F, 2F, 0.04),
                Info(2F, 2F, 0.05)
        ) shouldEqual PositionF(2F, 2F)

        velocityAt(0.08,
                Info(2F, 2F, 0.01),
                Info(2F, 2F, 0.02),
                Info(2F, 2F, 0.03),
                Info(2F, 2F, 0.04),
                Info(2F, 2F, 0.05)
        ) shouldEqual PositionF(2F, 2F)
    }

    @Test
    fun acceleratingVelocity() {
        velocityAt(0.05,
                Info(2F, 2F, 0.01),
                Info(3F, 3F, 0.02),
                Info(4F, 4F, 0.03),
                Info(5F, 5F, 0.04),
                Info(6F, 6F, 0.05)
        ) shouldEqual PositionF(6F, 6F)

        velocityAt(0.06,
                Info(2F, 2F, 0.01),
                Info(3F, 3F, 0.02),
                Info(4F, 4F, 0.03),
                Info(5F, 5F, 0.04),
                Info(6F, 6F, 0.05)
        ) shouldEqual PositionF(7F, 7F)
    }

    /**
     * Можно проверить на Wolfram alpha:
     * linear fit {0, 4}, {0.01, 2.8}, {0.02, 2.3}, {0.03, 2}
     * linear fit {0, 3.2}, {0.01, 3.0}, {0.02, 2.7}, {0.03, 2}
     */
    @Test
    fun randomVelocity() {
        velocityAt(0.04,
                Info(2F, 2F, 0.01),
                Info(2.3F, 2.7F, 0.02),
                Info(2.8F, 3.0F, 0.03),
                Info(4.0F, 3.2F, 0.04)
        ) shouldEqual PositionF(3.75F, 3.31F)
    }

    @Test
    fun peekOnlyLast100ms() {
        velocityAt(0.121,
                Info(4F, 4F, 0.01),
                Info(4F, 4F, 0.02),
                Info(2F, 2F, 0.03),
                Info(2F, 2F, 0.13)
        ) shouldEqual PositionF(2F, 2F)
    }

    fun velocityAt(seconds: Double, vararg infos: Info): PositionF {
        val approximator = VelocityApproximator()
        infos.forEach { approximator.add(it.velocityX, it.velocityY, it.time) }
        return approximator.approximateAt(seconds).testRound()
    }

    fun PositionF.testRound() = PositionF(round(x * 100F) / 100F, round(y * 100F) / 100F)

    class Info(val velocityX: Float, val velocityY: Float, val time: Double)
}