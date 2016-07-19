package com.dmi.perfectreader.fragment.book.paint

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Path
import android.graphics.RectF
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutFrame
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutFrame.Background
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutFrame.Border
import com.dmi.perfectreader.fragment.book.pagination.page.PageContext
import com.dmi.util.graphic.Color

class FramePainter : ObjectPainter<LayoutFrame> {
    private val backgroundPaint = Paint().apply { isAntiAlias = true }
    private val borderPaint = Paint().apply { isAntiAlias = true }
    private val path = Path()

    override fun paintItself(obj: LayoutFrame, context: PageContext, canvas: Canvas) {
        with (obj) {
            canvas.translate(internalMargins.left, internalMargins.top)
            paintContents(obj, canvas,
                    width - internalMargins.left - internalMargins.right,
                    height - internalMargins.top - internalMargins.bottom
            )
            canvas.translate(-internalMargins.left, -internalMargins.top)
        }
    }

    private fun paintContents(obj: LayoutFrame, canvas: Canvas, width: Float, height: Float) {
        with (obj) {
            paintBackground(canvas, background, width, height)
            paintBorder(canvas, borders.left, { borderWidth ->
                moveTo(0F, 0F)
                lineTo(0F, height)
                lineTo(borderWidth, height - borders.bottom.width)
                lineTo(borderWidth, borders.top.width)
            })
            paintBorder(canvas, borders.right, { borderWidth ->
                moveTo(width, 0F)
                lineTo(width, height)
                lineTo(width - borderWidth, height - borders.bottom.width)
                lineTo(width - borderWidth, borders.top.width)
            })
            paintBorder(canvas, borders.top, { borderWidth ->
                moveTo(0F, 0F)
                lineTo(width, 0F)
                lineTo(width - borders.right.width, borderWidth)
                lineTo(borders.left.width, borderWidth)
            })
            paintBorder(canvas, borders.bottom, { borderWidth ->
                moveTo(0F, height)
                lineTo(width, height)
                lineTo(width - borders.right.width, height - borderWidth)
                lineTo(borders.left.width, height - borderWidth)
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