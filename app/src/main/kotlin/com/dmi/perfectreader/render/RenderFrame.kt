package com.dmi.perfectreader.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Path

class RenderFrame(
        width: Float,
        height: Float,
        val borders: Borders,
        child: RenderChild
) : RenderObject(width, height, listOf(child)) {
    companion object {
        private val paint = Paint()
        private val path = Path()
    }

    override fun canPartiallyPainted() = true

    override fun paintItself(canvas: Canvas) {
        paintBorder(canvas, borders.left, { borderWidth ->
            moveTo(0F, 0F)
            lineTo(0F, width)
            lineTo(borderWidth, width - borderWidth)
            lineTo(borderWidth, borderWidth)
        })
        paintBorder(canvas, borders.right, { borderWidth ->
            moveTo(width, 0F)
            lineTo(width, width)
            lineTo(width - borderWidth, width - borderWidth)
            lineTo(width - borderWidth, borderWidth)
        })
        paintBorder(canvas, borders.top, { borderWidth ->
            moveTo(0F, 0F)
            lineTo(width, 0F)
            lineTo(width - borderWidth, borderWidth)
            lineTo(borderWidth, borderWidth)
        })
        paintBorder(canvas, borders.bottom, { borderWidth ->
            moveTo(0F, width)
            lineTo(width, width)
            lineTo(width - borderWidth, width - borderWidth)
            lineTo(borderWidth, width - borderWidth)
        })
    }

    private inline fun paintBorder(canvas: Canvas, border: Border, setPath: Path.(Float) -> Unit) {
        if (border.width > 0) {
            canvas.drawPath(
                    path.apply {
                        reset()
                        setPath(border.width)
                        close()
                    },
                    paint.apply {
                        color = border.color
                        style = Style.FILL
                    }
            )
        }
    }

    class Border(val width: Float, val color: Int)
    class Borders(val left: Border, val right: Border, val top: Border, val bottom: Border)
}
