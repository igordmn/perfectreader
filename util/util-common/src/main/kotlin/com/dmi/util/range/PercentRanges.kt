package com.dmi.util.range

import java.util.*

class PercentRanges {
    private val lengthBegins = ArrayList<Double>(1024)
    private var lengthEnd = 0.0

    var size: Int = 0
        private set

    fun add(length: Double) {
        lengthBegins.add(lengthEnd)
        lengthEnd += length
        size++
    }

    fun beginOf(index: Int) = lengthBegins[index] / lengthEnd
    fun endOf(index: Int) = if (index < size - 1) lengthBegins[index + 1] / lengthEnd else 1.0

    fun indexOfNearest(percent: Percent) = indexOfNearestRange({ get(it) }, percent, 0, size)

    operator fun get(index: Int) = beginOf(index)..endOf(index)
}

typealias Percent = Double

fun ClosedRange<Percent>.globalPercent(localPercent: Percent): Double {
    require(localPercent in 0.0..1.0)
    return start + localPercent * (endInclusive - start)
}

fun ClosedRange<Percent>.localPercent(globalPercent: Percent): Double {
    require(globalPercent in 0.0..1.0)
    require(globalPercent in start..endInclusive)
    return (globalPercent - start) / (endInclusive - start)
}