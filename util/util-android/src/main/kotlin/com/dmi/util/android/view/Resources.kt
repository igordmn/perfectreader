package com.dmi.util.android.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.withAlpha

fun View.dipFloat(value: Float): Float = value * resources.displayMetrics.density
fun View.spFloat(value: Float): Float = value * resources.displayMetrics.scaledDensity
fun View.color(resID: Int) = ContextCompat.getColor(context, resID)
fun View.drawable(resID: Int): Drawable = DrawableCompat.wrap(VectorDrawableCompat.create(context.resources, resID, context.theme)!!)

fun View.drawable(resID: Int, tintColor: Int): Drawable = drawable(resID).apply {
    DrawableCompat.setTint(this, tintColor)
}

fun View.string(resID: Int): String = context.getString(resID)
fun View.string(resID: Int, vararg formatArgs: Any): String = context.getString(resID, *formatArgs)

fun View.attr(value: Int): TypedValue {
    val ret = TypedValue()
    context.theme.resolveAttribute(value, ret, true)
    return ret
}

inline fun <reified T> Context.inflate(id: Int): T = layoutInflater.inflate(id, null, false) as T

fun Int.withTransparency(transparency: Double) = withAlpha((transparency * 255).toInt())