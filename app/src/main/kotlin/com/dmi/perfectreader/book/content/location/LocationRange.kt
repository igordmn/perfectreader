package com.dmi.perfectreader.book.content.location

typealias LocationRange = ClosedRange<Location>

fun LocationRange.sublocation(percent: Double): Location {
    require(percent in 0.0..1.0)
    val distance = endInclusive.offset - start.offset
    return Location(start.offset + distance * percent)
}

fun LocationRange.subrange(beginPercent: Double, endPercent: Double): LocationRange {
    require(beginPercent <= endPercent)
    return sublocation(beginPercent)..sublocation(endPercent)
}

fun LocationRange.percentOf(location: Location): Double {
    require(location in start..endInclusive)
    return (location.offset - start.offset) / (endInclusive.offset - start.offset)
}