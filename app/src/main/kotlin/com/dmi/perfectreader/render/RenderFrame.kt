package com.dmi.perfectreader.render

import android.graphics.*
import android.graphics.Paint.Style

class RenderFrame(
        width: Float,
        height: Float,
        val borders: Borders,
        val background: Background,
        val child: RenderChild
) : RenderObject(width, height, listOf(child)) {
    companion object {
        private val backgroundPaint = Paint()
        private val borderPaint = Paint()
        private val path = Path()
    }

    override fun canPartiallyPainted() = true

    override fun paintItself(canvas: Canvas) {
        paintBackground(canvas)
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

    private fun paintBackground(canvas: Canvas) {
        if (background.color != Color.TRANSPARENT) {
            canvas.drawRect(
                    RectF(0F, 0F, width, height),
                    backgroundPaint.apply { color = background.color }
            )
        }
    }

    private inline fun paintBorder(canvas: Canvas, border: Border, setPath: Path.(Float) -> Unit) {
        if (border.width > 0) {
            canvas.drawPath(
                    path.apply {
                        reset()
                        setPath(border.width)
                        close()
                    },
                    borderPaint.apply {
                        color = border.color
                        style = Style.FILL
                    }
            )
        }
    }

    class Background(val color: Int)
    class Border(val width: Float, val color: Int)
    class Borders(val left: Border, val right: Border, val top: Border, val bottom: Border)
}
