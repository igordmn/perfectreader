package com.dmi.util.android.ext

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.dmi.util.graphic.SizeF
import org.jetbrains.anko.layoutInflater

fun View.dip2Px(value: Float): Float = value * resources.displayMetrics.density
fun View.dip2Px(size: SizeF): SizeF = size * resources.displayMetrics.density
fun View.px2dip(px: Float): Float = px / resources.displayMetrics.density
fun View.px2dip(size: SizeF): SizeF = size / resources.displayMetrics.density

inline fun <reified T> Context.inflate(id: Int): T = layoutInflater.inflate(id, null, false) as T

fun <T : View> ViewGroup.addOrRemoveView(condition: Boolean, current: T?, create: () -> T): T? {
    if (condition && current == null) {
        val view = create()
        addView(view)
        return view
    }

    if (!condition && current != null) {
        removeView(current)
        return null
    }

    return current
}