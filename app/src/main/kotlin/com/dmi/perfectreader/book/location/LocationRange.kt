package com.dmi.perfectreader.book.location

import com.dmi.util.range.Range
import java.io.Serializable

data class LocationRange(override val begin: Location, override val end: Location) : Range<Location>, Serializable {
    init {
        require(end >= begin)
    }

    fun sublocation(percent: Double): Location {
        require(percent >= 0.0 && percent <= 1.0)
        val distance = end.offset - begin.offset
        return Location(begin.offset + distance * percent)
    }

    fun subrange(beginPercent: Double, endPercent: Double): LocationRange {
        require(beginPercent <= endPercent)
        return LocationRange(
                sublocation(beginPercent),
                sublocation(endPercent)
        )
    }

    fun percentOf(location: Location): Double {
        require(location >= begin && location <= end)
        return (location.offset - begin.offset) / (end.offset - begin.offset)
    }

    infix fun intersects(other: LocationRange): LocationRange? {
        val intersectionBegin = max(begin, other.begin)
        val intersectionEnd = min(end, other.end)
        return if (intersectionBegin <= intersectionEnd) LocationRange(intersectionBegin, intersectionEnd) else null
    }
}