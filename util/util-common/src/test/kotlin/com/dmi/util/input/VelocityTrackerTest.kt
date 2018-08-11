package com.dmi.util.input

import com.dmi.test.shouldBe
import com.dmi.util.graphic.PositionF
import org.junit.Test
import java.lang.Math.round

class VelocityTrackerTest {
    @Test
    fun zeroVelocity() {
        velocityFor() shouldBe PositionF(0.0F, 0.0F)
        velocityFor(
                Movement(4.0, 6.0, 0.01)
        ) shouldBe PositionF(0.0F, 0.0F)
        velocityFor(
                Movement(4.0, 5.0, 0.02),
                Movement(4.0, 5.0, 0.03)
        ) shouldBe PositionF(0.0F, 0.0F)
    }

    @Test
    fun twoMovements() {
        velocityFor(
                Movement(8.0, 6.0, 0.04),
                Movement(10.0, 7.0, 0.05)
        ) shouldBe PositionF(2.0F / 0.01F, 1.0F / 0.01F)
    }

    @Test
    fun constVelocity() {
        velocityFor(
                Movement(2.0, 3.0, 0.01),
                Movement(4.0, 4.0, 0.02),
                Movement(6.0, 5.0, 0.03),
                Movement(8.0, 6.0, 0.04),
                Movement(10.0, 7.0, 0.05)
        ) shouldBe PositionF(2.0F / 0.01F, 1.0F / 0.01F)
    }

    @Test
    fun acceleratingVelocity() {
        velocityFor(
                Movement(2.0, 1.0, 0.01),
                Movement(3.0, 2.0, 0.02),
                Movement(5.0, 4.0, 0.03),
                Movement(8.0, 7.0, 0.04),
                Movement(12.0, 11.0, 0.05)
        ) shouldBe PositionF(4.5F / 0.01F, 4.5F / 0.01F)
    }

    /**
     * Можно проверить на Wolfram alpha:
     * quadratic fit {0, 11.1}, {0.01, 7.1}, {0.02, 4.3}, {0.03, 2.0}
     * quadratic fit {0, 9.9}, {0.01, 7.7}, {0.02, 4.7}, {0.03, 2.0}
     */
    @Test
    fun randomVelocity() {
        velocityFor(
                Movement(2.0, 2.0, 0.01),
                Movement(4.3, 4.7, 0.02),
                Movement(7.1, 7.7, 0.03),
                Movement(11.1, 9.9, 0.04)
        ) shouldBe PositionF(428.5F, 229.5F)
    }

    @Test
    fun peekOnlyLast100ms() {
        velocityFor(
                Movement(4.0, 4.0, 0.01),
                Movement(10.0, 10.0, 0.02),
                Movement(22.0, 22.0, 0.03),
                Movement(24.0, 24.0, 0.13)
        ) shouldBe PositionF(2F / 0.1F, 2F / 0.1F)
    }

    fun velocityFor(vararg movements: Movement): PositionF {
        val approximator = VelocityTracker()
        movements.forEach { approximator.addMovement(it.x, it.y, it.time) }
        return approximator.currentVelocity().testRound()
    }

    fun PositionF.testRound() = PositionF(round(x * 100F) / 100F, round(y * 100F) / 100F)

    class Movement(val x: Double, val y: Double, val time: Double)
}