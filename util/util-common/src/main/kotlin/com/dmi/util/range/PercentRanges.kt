package com.dmi.util.range

class PercentRanges : AbstractList<ClosedRange<Double>>() {
    private val lengthBegins = ArrayList<Double>(1024)
    private var lengthEnd = 0.0

    override var size: Int = 0
        private set

    fun add(length: Double) {
        lengthBegins.add(lengthEnd)
        lengthEnd += length
        size++
    }

    override operator fun get(index: Int) = beginOf(index)..endOf(index)
    private fun beginOf(index: Int) = lengthBegins[index] / lengthEnd
    private fun endOf(index: Int) = if (index < size - 1) lengthBegins[index + 1] / lengthEnd else 1.0
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