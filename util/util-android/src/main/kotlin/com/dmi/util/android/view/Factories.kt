package com.dmi.util.android.view

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

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

fun FrameLayout.params(
        width: Int,
        height: Int,
        gravity: Int = -1,
        leftMargin: Int = 0,
        topMargin: Int = 0,
        rightMargin: Int = 0,
        bottomMargin: Int = 0
) = FrameLayout.LayoutParams(width, height, gravity).apply {
    this.leftMargin = leftMargin
    this.topMargin = topMargin
    this.rightMargin = rightMargin
    this.bottomMargin = bottomMargin
}

fun ConstraintLayout.params(
        width: Int,
        height: Int,
        leftMargin: Int = 0,
        topMargin: Int = 0,
        rightMargin: Int = 0,
        bottomMargin: Int = 0
) = ConstraintLayout.LayoutParams(width, height).apply {
    this.leftMargin = leftMargin
    this.topMargin = topMargin
    this.rightMargin = rightMargin
    this.bottomMargin = bottomMargin
}

fun CoordinatorLayout.params(
        width: Int,
        height: Int,
        gravity: Int = Gravity.NO_GRAVITY,
        behavior: CoordinatorLayout.Behavior<*>? = null
) = CoordinatorLayout.LayoutParams(width, height).apply {
    this.gravity = gravity
    this.behavior = behavior
}

fun AppBarLayout.params(
        width: Int,
        height: Int,
        scrollFlags: Int = 1
) = AppBarLayout.LayoutParams(width, height).apply {
    this.scrollFlags = scrollFlags
}

fun CollapsingToolbarLayout.params(
        width: Int,
        height: Int,
        mode: Int = 0,
        parallaxMultiplier: Float = 0F
) = CollapsingToolbarLayout.LayoutParams(width, height).apply {
    this.collapseMode = mode
    this.parallaxMultiplier = parallaxMultiplier
}

fun ViewGroup.params(
        width: Int,
        height: Int
) = ViewGroup.LayoutParams(width, height)

fun <T : View> ViewGroup.child(
        params: ViewGroup.LayoutParams,
        view: T
): T = view.apply {
    layoutParams = params
    addView(this)
}