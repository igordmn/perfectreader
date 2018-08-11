package com.dmi.perfectreader.book.animation

import com.dmi.util.android.system.doubleSeconds
import java.lang.Math.*

data class PagesAnimator(
        val config: Config,
        val nanoTime: Long,
        val currentPage: Double,
        val targetPage: Double,
        val velocity: Double
) {
    companion object {
        fun zero(config: Config, nanoTime: Long) = PagesAnimator(config, nanoTime, 0.0, 0.0, 0.0)
    }

    val willChange: Boolean get() = currentPage != targetPage

    fun reset() = copy(currentPage = 0.0, targetPage = 0.0, velocity = 0.0)
    fun currentPage(currentPage: Double) = copy(currentPage = currentPage)
    fun targetPage(targetPage: Double) = copy(targetPage = targetPage)
    fun velocity(velocity: Double) = copy(velocity = velocity)

    /**
     * Для перелистывания одной страницы при начальной нулевой скорости, движение подчиняется формуле s = -2t^3 + 3t^2, v = s' = -6t^2 + 6t
     * где t - нормализованное время в секундах, прошедшее от начала движения, t от 0 до 1, s - пройденное расстояние
     *
     * Если нужно перелистнуть другое количество страниц или скорость отлична от 0, то меняем формулу: s = -2t^3 + bt^2 + ct,
     * подстраивая b и c так, чтобы при t = tMax, s = sTarget, где tMax - второй корень нового уравнения v, sTarget - количество страниц
     *
     * График s для одной страницы подобран из одномерной кубической кривой безье по точкам (0, 0, 1, 1),
     * для других расстояний и других начальных скоростей нужно просто подобрать кэффициенты перед t^2 и t так, чтобы:
     * - начальная скорость равнялась v0
     * - конечная скорость была равна 0
     * - общее расстояние при конечной скорости равно s0
     */
    fun update(nanoTime: Long): PagesAnimator {
        require(nanoTime >= this.nanoTime)
        if (currentPage != targetPage) {
            val singlePageSeconds = doubleSeconds(config.singlePageNanoTime)
            val offset = currentPage - targetPage
            val sign = signum(offset)
            val q = 1 / (singlePageSeconds * singlePageSeconds * singlePageSeconds)
            val s = sign * offset.toDouble() / 2 / q
            val v = max(0.0, -sign * velocity / 6 / q)
            val z = cbrt(s + sqrt(s * s + v * v * v))
            val d = z - v / z

            val remainingTime = d
            val a = sign * q * 2F
            val b = -sign * q * 3 * (d - v / d)
            val c = -sign * q * 6 * v

            val t = doubleSeconds(nanoTime - this.nanoTime)
            val velocity = if (t < remainingTime) 3 * a * t * t + 2 * b * t + c else 0.0
            val currentPage = if (t < remainingTime) currentPage + a * t * t * t + b * t * t + c * t else targetPage

            return copy(nanoTime = nanoTime, velocity = velocity, currentPage = currentPage)
        } else {
            return copy(nanoTime = nanoTime)
        }
    }

    data class Config(val singlePageNanoTime: Long)
}