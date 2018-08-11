package com.dmi.util.input

import com.dmi.util.collection.DoubleRingBuffer
import com.dmi.util.graphic.PositionF

private val MAX_LAST_POINTS = 20
private val MAX_AGE_SECONDS = 0.1

class VelocityTracker {
    private val lastXs = DoubleRingBuffer(MAX_LAST_POINTS)
    private val lastYs = DoubleRingBuffer(MAX_LAST_POINTS)
    private val lastSeconds = DoubleRingBuffer(MAX_LAST_POINTS)

    fun clear() {
        lastXs.clear()
        lastYs.clear()
        lastSeconds.clear()
    }

    fun addMovement(velocityX: Double, velocityY: Double, seconds: Double) {
        if (lastSeconds.size == 0 || seconds > lastSeconds[lastSeconds.size - 1]) {
            lastXs.add(velocityX)
            lastYs.add(velocityY)
            lastSeconds.add(seconds)
        }
    }

    /**
     * Вектор скорости в единицах в секунду в указанный момент времени
     */
    fun currentVelocity(): PositionF {
        val firstSeconds = if (lastSeconds.size > 0) lastSeconds[0] else 0.0

        val ages = Reusables.ages()
        for (i in lastSeconds.size - 1 downTo 0) {
            val age = firstSeconds - lastSeconds[i]
            if (age <= MAX_AGE_SECONDS) {
                ages.add(age)
            }
        }

        return when (ages.size) {
            0, 1 -> PositionF(0F, 0F)
            2 -> {
                val velocityX = -(lastXs[1] - lastXs[0]) / (ages[1] - ages[0])
                val velocityY = -(lastYs[1] - lastYs[0]) / (ages[1] - ages[0])
                PositionF(velocityX.toFloat(), velocityY.toFloat())
            }
            else -> {
                // нужно найти формулы x(age) = c * age^2 + b * age + a и y(age) = ...; -b - будет нужная нам скорость в точке age = 0
                val velocityX = -quadraticRegressionBCoeff(ages, lastXs, ages.size)
                val velocityY = -quadraticRegressionBCoeff(ages, lastYs, ages.size)
                PositionF(velocityX.toFloat(), velocityY.toFloat())
            }
        }
    }

    /**
     * Найти по точкам (x, y) формулу y = cx^2 + bx + a, и вернуть коэффициент b.
     * Применяется алгоритм http://mathworld.wolfram.com/LeastSquaresFittingPolynomial.html (см. формулы 12 и 16)
     */
    private fun quadraticRegressionBCoeff(x: DoubleRingBuffer, y: DoubleRingBuffer, size: Int): Double {
        require(size >= 2)

        fun powerSum(x: DoubleRingBuffer, pow: (Double) -> Double): Double {
            var sum = 0.0
            for (i in 0 until size) {
                sum += pow(x[i])
            }
            return sum
        }

        fun powerSumDot(x: DoubleRingBuffer, pow: (Double) -> Double, y: DoubleRingBuffer): Double {
            var sum = 0.0
            for (i in 0 until size) {
                sum += pow(x[i]) * y[i]
            }
            return sum
        }

        // Элементы матрицы X_transpose * X
        val t0 = powerSum(x, pow0)
        val t1 = powerSum(x, pow1)
        val t2 = powerSum(x, pow2)
        val t3 = powerSum(x, pow3)
        val t4 = powerSum(x, pow4)

        // Элементы вектора X_transpose * y
        val s0 = powerSumDot(x, pow0, y)
        val s1 = powerSumDot(x, pow1, y)
        val s2 = powerSumDot(x, pow2, y)

        val determinant = t0 * t2 * t4 - t0 * t3 * t3 - t1 * t1 * t4 + 2 * t1 * t2 * t3 - t2 * t2 * t2
        val addition12 = -(t1 * t4 - t2 * t3)
        val addition22 = t0 * t4 - t2 * t2
        val addition32 = -(t0 * t3 - t1 * t2)

        return if (determinant != 0.0) (addition12 * s0 + addition22 * s1 + addition32 * s2) / determinant else 0.0
    }

    private object Reusables {
        private val ages = DoubleRingBuffer(MAX_LAST_POINTS)

        fun ages() = ages.apply { clear() }
    }

    private val pow0 = { _: Double -> 1.0 }
    private val pow1 = { x: Double -> x }
    private val pow2 = { x: Double -> x * x }
    private val pow3 = { x: Double -> x * x * x }
    private val pow4 = { x: Double -> x * x * x * x }
}