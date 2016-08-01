package com.dmi.perfectreader.fragment.book.page

import com.dmi.util.lang.ceil
import com.dmi.util.lang.floor
import com.dmi.util.lang.notSameSign
import com.dmi.util.time.DeltaTimer
import java.lang.Math.abs
import java.util.*

class SlidePagesAnimation(private val pageWidth: Float, private val singleSlideSeconds: Float) {
    private var distance = 0F
    private var velocity = 0F
    private val deltaTimer = DeltaTimer()

    val isAnimating: Boolean get() = distance != 0F
    val isGoingNext: Boolean get() = distance > 0F
    val slides = ArrayList<Slide>()

    val hasSlides: Boolean get() = slides.size > 0
    val firstSlideIndex: Int get() = slides.first().relativeIndex
    val lastSlideIndex: Int get() = slides.last().relativeIndex

    init {
        require(singleSlideSeconds > 0F)
    }

    fun goPage() {
        distance = 0F
        velocity = 0F
        deltaTimer.reset()
    }

    fun goNextPage() {
        distance += pageWidth
        velocity = abs(distance) / singleSlideSeconds
        deltaTimer.reset()
    }

    fun goPreviousPage() {
        distance -= pageWidth
        velocity = abs(distance) / singleSlideSeconds
        deltaTimer.reset()
    }

    fun update() {
        updateDistance()
        updateSlides()
    }

    fun updateSlides() {
        slides.clear()

        val distanceInPages = distance / pageWidth

        val firstRelativeIndex = floor(-distanceInPages).toInt()
        val firstPageX = (distanceInPages - ceil(distanceInPages)) * pageWidth

        var relativeIndex = firstRelativeIndex
        var offsetX = firstPageX
        while (offsetX < pageWidth) {
            slides.add(Slide(relativeIndex, offsetX))
            relativeIndex++
            offsetX += pageWidth
        }
    }

    private fun updateDistance() {
        if (distance != 0F) {
            val oldDistance = distance
            val dt = deltaTimer.deltaSeconds()

            if (distance > 0) {
                distance -= velocity * dt
            } else {
                distance += velocity * dt
            }

            if (oldDistance notSameSign distance) {
                distance = 0F
                velocity = 0F
            }
        }
    }

    class Slide(val relativeIndex: Int, val offsetX: Float)
}