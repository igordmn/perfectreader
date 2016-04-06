package com.dmi.perfectreader.render

import android.graphics.*
import android.graphics.Paint.Style
import com.dmi.perfectreader.location.BookRange

class RenderFrame(
        width: Float,
        height: Float,
        val internalMargins: Margins,
        val borders: Borders,
        val background: Background,
        val child: RenderChild,
        range: BookRange
) : RenderObject(width, height, listOf(child), range) {
    companion object {
        private val backgroundPaint = Paint()
        private val borderPaint = Paint()
        private val path = Path()
    }

    override fun canPartiallyPaint() = true
    override fun internalMargins() = internalMargins

    override fun paintItself(canvas: Canvas) {
        canvas.translate(internalMargins.left, internalMargins.top)
        paintContents(canvas,
                width - internalMargins.left - internalMargins.right,
                height - internalMargins.top - internalMargins.bottom
        )
        canvas.translate(-internalMargins.left, -internalMargins.top)
    }

    private fun paintContents(canvas: Canvas, width: Float, height: Float) {
        paintBackground(canvas, width, height)
        paintBorder(canvas, borders.left, { borderWidth ->
            moveTo(0F, 0F)
            lineTo(0F, height)
            lineTo(borderWidth, height - borderWidth)
            lineTo(borderWidth, borderWidth)
        })
        paintBorder(canvas, borders.right, { borderWidth ->
            moveTo(width, 0F)
            lineTo(width, height)
            lineTo(width - borderWidth, height - borderWidth)
            lineTo(width - borderWidth, borderWidth)
        })
        paintBorder(canvas, borders.top, { borderWidth ->
            moveTo(0F, 0F)
            lineTo(width, 0F)
            lineTo(width - borderWidth, borderWidth)
            lineTo(borderWidth, borderWidth)
        })
        paintBorder(canvas, borders.bottom, { borderWidth ->
            moveTo(0F, height)
            lineTo(width, height)
            lineTo(width - borderWidth, height - borderWidth)
            lineTo(borderWidth, height - borderWidth)
        })
    }

    private fun paintBackground(canvas: Canvas, width: Float, height: Float) {
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
