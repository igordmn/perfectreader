package com.dmi.util.input

import com.dmi.util.collection.DoubleRingBuffer
import com.dmi.util.collection.FloatRingBuffer
import com.dmi.util.graphic.PositionF

private val MAX_LAST_POINTS = 20
private val MAX_AGE_SECONDS = 0.1F

class VelocityApproximator {
    private val lastVelocitiesXs = FloatRingBuffer(MAX_LAST_POINTS)
    private val lastVelocitiesYs = FloatRingBuffer(MAX_LAST_POINTS)
    private val lastSeconds = DoubleRingBuffer(MAX_LAST_POINTS)

    fun clear() {
        lastVelocitiesXs.clear()
        lastVelocitiesYs.clear()
        lastSeconds.clear()
    }

    fun add(velocityX: Float, velocityY: Float, seconds: Double) {
        if (lastSeconds.size > 0)
            require(seconds > lastSeconds[lastSeconds.size - 1])

        lastVelocitiesXs.add(velocityX)
        lastVelocitiesYs.add(velocityY)
        lastSeconds.add(seconds)
    }

    /**
     * Вектор скорости в единицах в секунду в указанный момент времени
     */
    fun approximateAt(seconds: Double): PositionF {
        if (lastSeconds.size > 0)
            require(seconds >= lastSeconds[lastSeconds.size - 1])

        val ages = Reusables.ages()
        for (i in lastSeconds.size - 1 downTo 0) {
            val age = (seconds - lastSeconds[i]).toFloat()
            if (age <= MAX_AGE_SECONDS) {
                ages.add(age)
            }
        }

        return when (ages.size) {
            0 -> PositionF(0F, 0F)
            1 -> PositionF(lastVelocitiesXs[0], lastVelocitiesYs[0])
            else -> {
                // нужно найти формулы Vx(age) = b * age + a и Vy(age) = b * age + a; подставляем age = 0; остается найти только коэффициенты a
                val velocityX = linearRegressionACoeff(ages, lastVelocitiesXs, ages.size)
                val velocityY = linearRegressionACoeff(ages, lastVelocitiesYs, ages.size)
                PositionF(velocityX, velocityY)
            }
        }
    }

    /**
     * Найти по точкам (x, y) формулу y = bx + a, и вернуть коеффициент a.
     * Применяется алгоритм http://mathworld.wolfram.com/LeastSquaresFitting.html
     */
    private fun linearRegressionACoeff(x: FloatRingBuffer, y: FloatRingBuffer, size: Int): Float {
        require(size >= 2)

        fun average(x: FloatRingBuffer): Float {
            var sum = 0F
            for (i in 0..size - 1) {
                sum += x[i]
            }
            return sum / size
        }

        fun dot(x: FloatRingBuffer, y: FloatRingBuffer): Float {
            var sum = 0F
            for (i in 0..size - 1) {
                sum += x[i] * y[i]
            }
            return sum
        }

        val dotXX = dot(x, x)
        val dotXY = dot(x, y)
        val averageX = average(x)
        val averageY = average(y)
        return (averageY * dotXX - averageX * dotXY) / (dotXX - size * averageX * averageX)
    }

    private object Reusables {
        private val ages = FloatRingBuffer(MAX_LAST_POINTS)

        fun ages() = ages.apply { clear() }
    }
}