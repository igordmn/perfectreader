package com.dmi.util.android.widget

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.annotation.SuppressLint

@SuppressLint("ObjectAnimatorBinding")
fun fadeTransition(milliseconds: Long) = LayoutTransition().apply {
    val appearingAnimation = ObjectAnimator.ofFloat(null, "alpha", 0F, 1F)
    val disappearingAnimation = ObjectAnimator.ofFloat(null, "alpha", 1F, 0F)

    setAnimator(LayoutTransition.APPEARING, appearingAnimation)
    setDuration(LayoutTransition.APPEARING, milliseconds)
    setStartDelay(LayoutTransition.APPEARING, 0)

    setAnimator(LayoutTransition.DISAPPEARING, disappearingAnimation)
    setDuration(LayoutTransition.DISAPPEARING, milliseconds)
    setStartDelay(LayoutTransition.DISAPPEARING, 0)
}