package com.dmi.perfectreader.book.pagebook

import com.dmi.util.lang.MathExt

object LocationUtils {
    fun percentToSegmentLocation(segmentSizes: IntArray, percent: Double): SegmentLocation {
        val totalSize = segmentSizes.sum()
        val position = (percent * totalSize).toInt()
        var segmentStart: Int
        var segmentEnd = 0
        for (i in segmentSizes.indices) {
            segmentStart = segmentEnd
            val segmentSize = segmentSizes[i]
            segmentEnd += segmentSize
            if (position >= segmentStart && position < segmentEnd) {
                val positionInSegment = position - segmentStart
                return SegmentLocation(i, positionInSegment.toDouble() / segmentSize)
            }
        }
        return SegmentLocation(segmentSizes.size - 1, 1.0)
    }

    fun segmentLocationToPercent(segmentSizes: IntArray, segmentLocation: SegmentLocation): Double {
        val totalSize = segmentSizes.sum()
        var position = 0
        for (i in 0..segmentLocation.index - 1) {
            position += segmentSizes[i]
        }
        position += (segmentSizes[segmentLocation.index] * segmentLocation.percent).toInt()
        return MathExt.clamp(0.0, 1.0, (position / totalSize).toDouble())
    }

    fun percentToPage(pageCount: Int, percent: Double): Int {
        return Math.min(Math.round(percent * pageCount), (pageCount - 1).toLong()).toInt()
    }

    fun pageToPercent(pageCount: Int, page: Int): Double {
        return page.toDouble() / pageCount
    }

    class SegmentLocation(val index: Int, val percent: Double)
}
