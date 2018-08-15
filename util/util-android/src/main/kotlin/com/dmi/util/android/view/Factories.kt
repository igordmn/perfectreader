package com.dmi.util.android.view

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat

fun <T : View> Context.view(
        create: (Context) -> T,
        init: T.() -> Unit = {}
): T = create(this).apply {
    id = View.generateViewId()
    init(this)
}

fun <T : View> View.view(create: (Context) -> T, init: T.() -> Unit): T = context.view(create, init)

@Suppress("unused")
fun LinearLayoutCompat.params(
        width: Int,
        height: Int,
        weight: Float = 0F,
        leftMargin: Int = 0,
        topMargin: Int = 0,
        rightMargin: Int = 0,
        bottomMargin: Int = 0
) = LinearLayoutCompat.LayoutParams(width, height, weight).apply {
    this.leftMargin = leftMargin
    this.topMargin = leftMargin
    this.rightMargin = leftMargin
    this.bottomMargin = leftMargin
}

@Suppress("unused")
fun FrameLayout.params(
        width: Int,
        height: Int,
        gravity: Int = -1
) = FrameLayout.LayoutParams(width, height, gravity)

fun <T : View> LinearLayoutCompat.child(
        create: (Context) -> T,
        params: LinearLayoutCompat.LayoutParams,
        init: T.() -> Unit = {}
): T = child(context.view(create, init), params)

fun <T : View> LinearLayoutCompat.child(
        view: T,
        params: LinearLayoutCompat.LayoutParams
): T = view.apply {
    layoutParams = params
    addView(this)
}

fun <T : View> FrameLayout.child(
        create: (Context) -> T,
        params: FrameLayout.LayoutParams,
        init: T.() -> Unit = {}
): T = child(context.view(create, init), params)

fun <T : View> FrameLayout.child(
        view: T,
        params: FrameLayout.LayoutParams
): T = view.apply {
    layoutParams = params
    addView(this)
}