package com.dmi.perfectreader.book.content.location

import com.google.common.primitives.Doubles
import kotlinx.serialization.Serializable

@Serializable
data class Location(val offset: Double) : Comparable<Location> {
    override operator fun compareTo(other: Location) = Doubles.compare(offset, other.offset)
}

fun clamp(a: Location, range: LocationRange) = when {
    a < range.start -> range.start
    a > range.endInclusive -> range.endInclusive
    else -> a
}