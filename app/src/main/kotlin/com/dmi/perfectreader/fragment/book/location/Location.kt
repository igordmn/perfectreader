package com.dmi.perfectreader.fragment.book.location

import com.google.common.primitives.Doubles
import java.io.Serializable

data class Location(val offset: Double) : Comparable<Location>, Serializable {
    override operator fun compareTo(other: Location) = Doubles.compare(offset, other.offset)
}

fun min(a: Location, b: Location) = if (a < b) a else b
fun max(a: Location, b: Location) = if (a > b) a else b
fun clamp(a: Location, range: LocationRange) = when {
    a < range.begin -> range.begin
    a > range.end -> range.end
    else -> a
}