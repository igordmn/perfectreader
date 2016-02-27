package com.dmi.perfectreader.book.animation

import java.lang.Math.*

class SlidePageAnimation(timeForOneSlideInSeconds: Float) : PageAnimation {
    private var pageWidth = 100f
    private val timeForOneSlideInSeconds = timeForOneSlideInSeconds

    private val state = com.dmi.perfectreader.book.animation.PageAnimationState()
    private var distance = 0f
    private var velocity = 0f

    override fun setPageWidth(pageWidth: Float) {
        this.pageWidth = pageWidth
    }

    override val isAnimate: Boolean
        get() = distance != 0f

    override fun reset() {
        distance = 0f
        velocity = 0f
    }

    override fun moveNext() {
        distance += pageWidth
        velocity = if (timeForOneSlideInSeconds > 0) abs(distance) / timeForOneSlideInSeconds else 1000000F
    }

    override fun movePreview() {
        distance -= pageWidth
        velocity = if (timeForOneSlideInSeconds > 0) abs(distance) / timeForOneSlideInSeconds else 1000000F
    }

    override fun update(dt: Float) {
        if (distance != 0f) {
            val oldDistance = distance

            if (distance > 0) {
                distance -= velocity * dt
            } else {
                distance += velocity * dt
            }

            if (oldDistance > 0 && distance <= 0 || oldDistance < 0 && distance >= 0) {
                reset()
            }
        }
        updateState()
    }

    private fun updateState() {
        state.clear()

        val distanceInPages = distance / pageWidth

        val firstRelativeIndex = floor((-distanceInPages).toDouble()).toInt()
        val firstDrawingPageX = (distanceInPages - ceil(distanceInPages.toDouble()).toFloat()) * pageWidth

        var relativeIndex = firstRelativeIndex
        var pageX = firstDrawingPageX
        while (pageX < pageWidth) {
            state.add(relativeIndex, pageX)
            relativeIndex++
            pageX += pageWidth
        }
    }

    override fun state(): com.dmi.perfectreader.book.animation.PageAnimationState {
        return state
    }
}
