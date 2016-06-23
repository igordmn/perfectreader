package com.dmi.perfectreader.fragment.book.layout.painter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Path
import android.graphics.RectF
import com.dmi.perfectreader.fragment.book.obj.render.RenderFrame
import com.dmi.perfectreader.fragment.book.obj.render.RenderFrame.Background
import com.dmi.perfectreader.fragment.book.obj.render.RenderFrame.Border
import com.dmi.util.graphic.Color

class FramePainter : ObjectPainter<RenderFrame> {
    private val backgroundPaint = Paint()
    private val borderPaint = Paint()
    private val path = Path()

    override fun paintItself(obj: RenderFrame, canvas: Canvas, context: PaintContext) {
        with (obj) {
            canvas.translate(internalMargins.left, internalMargins.top)
            paintContents(obj, canvas,
                    width - internalMargins.left - internalMargins.right,
                    height - internalMargins.top - internalMargins.bottom
            )
            canvas.translate(-internalMargins.left, -internalMargins.top)
        }
    }

    private fun paintContents(obj: RenderFrame, canvas: Canvas, width: Float, height: Float) {
        with (obj) {
            paintBackground(canvas, background, width, height)
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
    }

    private fun paintBackground(canvas: Canvas, background: Background, width: Float, height: Float) {
        if (background.color != Color.TRANSPARENT) {
            canvas.drawRect(
                    RectF(0F, 0F, width, height),
                    backgroundPaint.apply { color = background.color.value }
            )
        }
    }

    private inline fun paintBorder(canvas: Canvas, border: Border, setPath: Path.(borderWidth: Float) -> Unit) {
        if (border.width > 0) {
            canvas.drawPath(
                    path.apply {
                        reset()
                        setPath(border.width)
                        close()
                    },
                    borderPaint.apply {
                        color = border.color.value
                        style = Style.FILL
                    }
            )
        }
    }
}