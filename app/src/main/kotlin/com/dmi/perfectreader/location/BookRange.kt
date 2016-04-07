package com.dmi.perfectreader.location

data class BookRange(val begin: BookLocation, val end: BookLocation) {
    init {
        check(end >= begin)
    }

    fun sublocation(percent: Double): BookLocation {
        check(percent >= 0.0 && percent <= 1.0)
        val distance = end.offset - begin.offset
        return BookLocation(begin.offset + distance * percent)
    }

    fun subrange(beginPercent: Double, endPercent: Double): BookRange {
        check(beginPercent <= endPercent)
        return BookRange(
                sublocation(beginPercent),
                sublocation(endPercent)
        )
    }

    operator fun contains(location: BookLocation) = location >= begin && location < end
}
