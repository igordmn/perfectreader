package com.dmi.util.android.view

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import org.jetbrains.anko.wrapContent

// todo Refactor, because of this bug:
/*
child(::FrameLayout, params(wrapContent, wrapContent)) {
    child(::LinearLayoutCompat, params(wrapContent, wrapContent)) {
        // child will be added to FrameLayout here
        child(::TextView, params(wrapContent, wrapContent, Gravity.CENTER_HORIZONTAL))
    }
}
 */
// todo Maybe convert all code to Anko

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class TestDsl

fun <T : View> Context.view(
        create: (Context) -> T,
        init: @TestDsl T.() -> Unit = {}
): T = create(this).apply {
    id = View.generateViewId()
    init(this)
}

fun <T : View> View.view(create: (Context) -> T, init: T.() -> Unit): T = context.view(create, init)

@Suppress("unused")
fun @TestDsl LinearLayoutCompat.params(
        width: Int,
        height: Int,
        weight: Float = 0F,
        gravity: Int = -1,
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
fun @TestDsl LinearLayoutCompat.params(
        width: Int,
        height: Int,
        weight: Float = 0F,
        init: LinearLayoutCompat.LayoutParams.() -> Unit
) = LinearLayoutCompat.LayoutParams(width, height, weight).apply(init)

@Suppress("unused")
fun @TestDsl FrameLayout.params(
        width: Int,
        height: Int,
        gravity: Int = -1
) = FrameLayout.LayoutParams(width, height, gravity)

fun <T : View> @TestDsl LinearLayoutCompat.child(
        create: (Context) -> T,
        params: LinearLayoutCompat.LayoutParams = LinearLayoutCompat.LayoutParams(wrapContent, wrapContent),
        init: T.() -> Unit = {}
): T = child(context.view(create, init), params)

fun <T : View> LinearLayoutCompat.child(
        view: T,
        params: LinearLayoutCompat.LayoutParams
): T = view.apply {
    layoutParams = params
    addView(this)
}

fun <T : View> @TestDsl FrameLayout.child(
        create: (Context) -> T,
        params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(wrapContent, wrapContent),
        init: T.() -> Unit = {}
): T = child(context.view(create, init), params)

fun <T : View> @TestDsl FrameLayout.child(
        view: T,
        params: FrameLayout.LayoutParams
): T = view.apply {
    layoutParams = params
    addView(this)
}