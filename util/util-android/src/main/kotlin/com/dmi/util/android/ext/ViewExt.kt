package com.dmi.util.android.ext

import android.content.Context
import android.view.View
import com.dmi.util.graphic.SizeF
import org.jetbrains.anko.layoutInflater

fun View.dipToPx(value: Float): Float = value * resources.displayMetrics.density
fun View.dipToPx(size: SizeF): SizeF = size * resources.displayMetrics.density
fun View.px2dip(px: Float): Float = px / resources.displayMetrics.density
fun View.px2dip(size: SizeF): SizeF = size / resources.displayMetrics.density

inline fun <reified T> Context.inflate(id: Int): T = layoutInflater.inflate(id, null, false) as T