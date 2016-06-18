package com.dmi.util.collection

import com.dmi.util.lang.modPositive
import java.lang.Math.abs

class DuplexBuffer<T>(val maxRelativeIndex: Int) {
    @Suppress("CAST_NEVER_SUCCEEDS")
    private val items: Array<T?> = arrayOfNulls<Any>(2 * maxRelativeIndex + 1) as Array<T?>
    private var shift = 0

    operator fun set(relativeIndex: Int, item: T?) {
        require(abs(relativeIndex) <= maxRelativeIndex)
        items[arrayIndex(relativeIndex)] = item
    }

    operator fun get(relativeIndex: Int): T? {
        require(abs(relativeIndex) <= maxRelativeIndex)
        return items[arrayIndex(relativeIndex)]
    }

    fun shiftLeft() {
        shift--
        this[maxRelativeIndex] = null
    }

    fun shiftRight() {
        shift++
        this[-maxRelativeIndex] = null
    }

    fun clear() = items.fill(null)

    private fun arrayIndex(relativeIndex: Int) =
            (maxRelativeIndex + relativeIndex - shift) modPositive items.size

    override fun toString() =
            (-maxRelativeIndex..maxRelativeIndex)
                    .map { i -> "$i: ${get(i)}" }
                    .joinToString(", ")
}