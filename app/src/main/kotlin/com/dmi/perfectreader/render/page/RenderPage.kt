package com.dmi.perfectreader.render.page

import android.graphics.Canvas

class RenderPage(val rows: List<RenderRow> = emptyList()) {
    val height = run {
        var sum = 0F
        for (row in rows) {
            sum += row.height
        }
        sum
    }

    fun paint(canvas: Canvas) {
        canvas.save()
        for (i in 0..rows.size - 1) {
            val row = rows[i]
            row.paint(canvas)
            canvas.translate(0F, row.height)
        }
        canvas.restore()
    }
}

infix fun RenderPage.merge(row: RenderRow) = RenderPage(
        if (this.rows.size == 0) {
            listOf(row)
        } else {
            val last = this.rows.last()
            if (last.obj == row.obj) {
                this.rows.dropLast(1) + listOf(last merge row)
            } else {
                this.rows.dropLast(1) + listOf(last.extendToEnd(), row.extendToBegin())
            }
        }
)

infix fun RenderRow.merge(page: RenderPage) = RenderPage(
        if (page.rows.size == 0) {
            listOf(this)
        } else {
            val first = page.rows.first()
            if (first.obj == obj) {
                listOf(this merge first) + page.rows.drop(1)
            } else {
                listOf(this.extendToEnd(), first.extendToBegin()) + page.rows.drop(1)
            }
        }
)

private infix fun RenderRow.merge(other: RenderRow) = RenderRow(obj, top, other.bottom)
private fun RenderRow.extendToEnd() = RenderRow(obj, top, RenderRow.Edge(bottom.childIndices, obj.height))
private fun RenderRow.extendToBegin() = RenderRow(obj, RenderRow.Edge(top.childIndices, 0F), bottom)
