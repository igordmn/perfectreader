package com.dmi.perfectreader.fragment.book.location

import com.google.common.primitives.Doubles
import java.io.Serializable

data class Location(val offset: Double) : Comparable<Location>, Serializable {
    override operator fun compareTo(other: Location) = Doubles.compare(offset, other.offset)
}