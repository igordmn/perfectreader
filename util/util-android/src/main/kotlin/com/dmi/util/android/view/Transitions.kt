package com.dmi.util.android.view

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.annotation.SuppressLint

@SuppressLint("ObjectAnimatorBinding")
fun fadeTransition(milliseconds: Long) = LayoutTransition().apply {
    setAnimator(LayoutTransition.APPEARING, ObjectAnimator.ofFloat(null, "alpha", 0F, 1F))
    setDuration(LayoutTransition.APPEARING, milliseconds)
    setStartDelay(LayoutTransition.APPEARING, 0)

    setAnimator(LayoutTransition.DISAPPEARING, ObjectAnimator.ofFloat(null, "alpha", 1F, 0F))
    setDuration(LayoutTransition.DISAPPEARING, milliseconds)
    setStartDelay(LayoutTransition.DISAPPEARING, 0)
}