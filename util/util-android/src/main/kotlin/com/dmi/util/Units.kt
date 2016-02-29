package com.dmi.util

import android.content.Context
import android.util.DisplayMetrics

object Units {
    private var density: Float = 0.toFloat()

    fun init(context: Context) {
        val displayMetrics = context.applicationContext.resources.displayMetrics
        density = displayMetrics.density
    }

    fun dipToPx(dip: Float): Float {
        return dip * density
    }

    fun pxToDip(px: Float): Float {
        return px / density
    }
}