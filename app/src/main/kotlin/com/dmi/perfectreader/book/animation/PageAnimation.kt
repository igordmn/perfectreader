package com.dmi.perfectreader.book.animation

interface PageAnimation {
    fun setPageWidth(pageWidth: Float)

    val isAnimate: Boolean

    fun reset()

    fun moveNext()

    fun movePreview()

    fun update(dt: Float)

    fun state(): PageAnimationState
}