package com.dmi.util.android.view

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat

@Suppress("unused")
fun LinearLayoutCompat.params(
        width: Int,
        height: Int,
        gravity: Int = -1,
        weight: Float = 0F,
        leftMargin: Int = 0,
        topMargin: Int = 0,
        rightMargin: Int = 0,
        bottomMargin: Int = 0
) = LinearLayoutCompat.LayoutParams(width, height, weight).apply {
    this.gravity = gravity
    this.leftMargin = leftMargin
    this.topMargin = topMargin
    this.rightMargin = rightMargin
    this.bottomMargin = bottomMargin
}

@Suppress("unused")
fun FrameLayout.params(
        width: Int,
        height: Int,
        gravity: Int = -1
) = FrameLayout.LayoutParams(width, height, gravity)

fun <T : View> LinearLayoutCompat.child(
        params: LinearLayoutCompat.LayoutParams,
        view: T
): T = view.apply {
    layoutParams = params
    addView(this)
}

fun <T : View> FrameLayout.child(
        params: FrameLayout.LayoutParams,
        view: T
): T = view.apply {
    layoutParams = params
    addView(this)
}