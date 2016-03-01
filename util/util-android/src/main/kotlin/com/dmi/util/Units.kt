package com.dmi.util

import android.content.Context
import android.util.DisplayMetrics

object Units {
    private var density = 0F

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