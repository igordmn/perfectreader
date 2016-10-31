package com.dmi.util.collection

import java.util.*

class DoubleRingBuffer(val maxSize: Int) {
    private val values = DoubleArray(maxSize)
    private var endIndex = 0
    var size: Int = 0
        private set

    fun add(value: Double) {
        if (size < maxSize)
            size++
        values[endIndex] = value
        endIndex++
        if (endIndex == maxSize)
            endIndex = 0
    }

    operator fun get(index: Int): Double {
        require(index >= 0 && index < size)
        var arrayIndex = (endIndex - 1) - index
        if (arrayIndex < 0)
            arrayIndex += maxSize
        return values[arrayIndex]
    }

    fun clear() {
        endIndex = 0
        size = 0
    }

    override fun toString(): String {
        return (0..size - 1).map { String.format(Locale.US, "%.2f", get(it)) }.joinToString()
    }
}