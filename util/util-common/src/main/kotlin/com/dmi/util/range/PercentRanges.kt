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

    fun indexOfNearest(percent: Double) = indexOfNearestRange({ get(it) }, percent, 0, size)

    operator fun get(index: Int) = PercentRange(beginOf(index), endOf(index))
}

data class PercentRange(override val begin: Double, override val end: Double) : Range<Double> {
    init {
        require(begin >= 0.0 && begin <= 1.0)
        require(end >= 0.0 && end <= 1.0)
    }

    fun globalPercent(localPercent: Double): Double {
        require(localPercent >= 0.0 && localPercent <= 1.0)
        return begin + localPercent * (end - begin)
    }

    fun localPercent(globalPercent: Double): Double {
        require(globalPercent >= 0.0 && globalPercent <= 1.0)
        require(globalPercent >= begin && globalPercent <= end)
        return (globalPercent - begin) / (end - begin)
    }
}