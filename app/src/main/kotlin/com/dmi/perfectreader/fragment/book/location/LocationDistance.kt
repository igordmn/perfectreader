package com.dmi.perfectreader.fragment.book.location

import com.google.common.primitives.Doubles
import java.lang.Math.abs

data class LocationDistance(val offset: Double) : Comparable<LocationDistance> {
    companion object {
        val MAX = LocationDistance(Double.MAX_VALUE)
    }

    override operator fun compareTo(other: LocationDistance) = Doubles.compare(offset, other.offset)
}

fun distance(a: Location, b: Location) = LocationDistance(abs(a.offset - b.offset))