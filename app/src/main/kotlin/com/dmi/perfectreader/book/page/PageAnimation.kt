package com.dmi.perfectreader.book.page

import com.dmi.util.system.Nanos
import com.dmi.util.system.toSeconds
import java.lang.Math.*
import kotlin.math.sign

data class PageAnimation(val time: Nanos, val currentPage: Double = 0.0, val targetPage: Double = 0.0, val velocity: Double = 0.0) {
    val isStill: Boolean get() = currentPage == targetPage
    fun reset() = copy(currentPage = 0.0, targetPage = 0.0, velocity = 0.0)
}

interface PageAnimator {
    fun update(animation: PageAnimation, time: Nanos): PageAnimation
}

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
class SmoothPageAnimator(private val singlePageTime: Nanos) : PageAnimator {
    override fun update(animation: PageAnimation, time: Nanos): PageAnimation {
        require(time >= animation.time)
        return if (animation.currentPage != animation.targetPage) {
            val singlePageSeconds = singlePageTime.toSeconds()
            val offset = animation.currentPage - animation.targetPage
            val sign = sign(offset)
            val q = 1 / (singlePageSeconds * singlePageSeconds * singlePageSeconds)
            val s = sign * offset / 2 / q
            val v = max(0.0, -sign * animation.velocity / 6 / q)
            val z = cbrt(s + sqrt(s * s + v * v * v))
            val d = z - v / z

            val remainingTime = d
            val a = sign * q * 2F
            val b = -sign * q * 3 * (d - v / d)
            val c = -sign * q * 6 * v

            val dt = (time - animation.time).toSeconds()
            val velocity = if (dt < remainingTime) 3 * a * dt * dt + 2 * b * dt + c else 0.0
            val currentPage = if (dt < remainingTime) animation.currentPage + a * dt * dt * dt + b * dt * dt + c * dt else animation.targetPage

            animation.copy(time = time, velocity = velocity, currentPage = currentPage)
        } else {
            animation.copy(time = time, velocity = 0.0)
        }
    }
}

class LinearPageAnimator(singlePageTime: Nanos) : PageAnimator {
    private val minVelocity = 1.0 / singlePageTime.toSeconds()

    override fun update(animation: PageAnimation, time: Nanos): PageAnimation {
        require(time >= animation.time)
        val dt = (time - animation.time).toSeconds()
        return if (animation.currentPage != animation.targetPage) {
            val velocity = if (animation.currentPage < animation.targetPage) {
                max(animation.velocity, minVelocity)
            } else {
                min(animation.velocity, -minVelocity)
            }
            val currentPage = animation.currentPage + dt * velocity
            val sign1 = sign(currentPage - animation.targetPage)
            val sign2 = sign(animation.currentPage - animation.targetPage)
            if (sign1 == sign2) {
                animation.copy(time = time, currentPage = currentPage, velocity = velocity)
            } else {
                animation.copy(time = time, currentPage = animation.targetPage, velocity = velocity)
            }
        } else {
            animation.copy(time = time, velocity = 0.0)
        }
    }
}