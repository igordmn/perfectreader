package com.dmi.util.android.paint

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import com.dmi.util.graphic.Color
import android.graphics.Canvas as AndroidCanvas

@Suppress("ProtectedInFinal")
class Canvas(bitmap: Bitmap) {
    protected val androidCanvas = AndroidCanvas(bitmap)
    protected val rectPaint = Paint().apply { isAntiAlias = true }
    protected val pathPaint = Paint().apply { isAntiAlias = true }
    protected val path = Path()

    protected val pathDrawer = PathDrawer()

    fun clear() {
        androidCanvas.drawColor(Color.TRANSPARENT.value, PorterDuff.Mode.CLEAR)
    }

    fun drawRect(left: Float, top: Float, right: Float, bottom: Float, color: Color) {
        rectPaint.color = color.value
        androidCanvas.drawRect(left, top, right, bottom, rectPaint)
    }

    inline fun drawPath(color: Color, draw: (PathDrawer) -> Unit) {
        pathPaint.color = color.value
        path.reset()
        draw(pathDrawer)
        path.close()
        androidCanvas.drawPath(path, pathPaint)
    }

    inner class PathDrawer internal constructor() {
        fun moveTo(x: Float, y: Float) = path.moveTo(x, y)
        fun lineTo(x: Float, y: Float) = path.lineTo(x, y)
    }
}