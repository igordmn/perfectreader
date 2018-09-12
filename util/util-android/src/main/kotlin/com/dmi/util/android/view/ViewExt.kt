package com.dmi.util.android.view

import android.graphics.Rect
import android.os.Parcelable
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import com.dmi.util.graphic.Size

typealias ViewState = SparseArray<Parcelable>

val View.size: Size get() = Size(width, height)

operator fun View.contains(event: MotionEvent): Boolean {
    val rect = Rect()
    getGlobalVisibleRect(rect)
    return rect.contains(event.rawX.toInt(), event.rawY.toInt())
}

interface Bindable<M> {
    fun bind(model: M)
}