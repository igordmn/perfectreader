package com.dmi.perfectreader.location

import com.google.common.primitives.Doubles

data class BookLocation(val offset: Double) {
    operator fun compareTo(other: BookLocation) = Doubles.compare(offset, other.offset)
}
