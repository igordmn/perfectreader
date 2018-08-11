package com.dmi.perfectreader.book.page

import com.dmi.perfectreader.book.pagination.page.Page

data class VisiblePages(val left: Page?, val right: Page?, val future: Page?, val leftProgress: Float, val rightProgress: Float): Iterable<Page?> {
    companion object {
        const val COUNT = 3
    }

    override fun iterator(): Iterator<Page?> = arrayOf(left, right, future).iterator()
}