package com.dmi.perfectreader.book.pagebook

interface PageBook {
    fun resize(width: Int, height: Int)

    fun canGoPage(offset: Int): CanGoResult

    fun tap(x: Float, y: Float, tapDiameter: Float)

    fun goPercent(percent: Double)

    fun goNextPage()

    fun goPreviewPage()

    enum class CanGoResult {
        /* Next/preview page is loaded */
        CAN,

        /* On first or last page and cannot go preview/next page */
        CANNOT,

        /* Next/preview page is loading and it is not known whether it is possible go page */
        UNKNOWN
    }
}
