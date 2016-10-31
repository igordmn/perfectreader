package com.dmi.util.collection

import com.dmi.util.lang.modPositive
import java.lang.Math.*

class DuplexBuffer<T>(val maxRelativeIndex: Int) {
    @Suppress("UNCHECKED_CAST")
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

    fun shift(relativeIndex: Int) {
        shift += relativeIndex

        when {
            relativeIndex > 0 -> {
                for (i in -maxRelativeIndex..min(maxRelativeIndex, -maxRelativeIndex + relativeIndex - 1))
                    this[i] = null
            }
            relativeIndex < 0 -> {
                for (i in max(-maxRelativeIndex, maxRelativeIndex + relativeIndex + 1)..maxRelativeIndex)
                    this[i] = null
            }
        }
    }

    fun clear() = items.fill(null)

    private fun arrayIndex(relativeIndex: Int) =
            (maxRelativeIndex + relativeIndex - shift) modPositive items.size

    override fun toString() =
            (-maxRelativeIndex..maxRelativeIndex)
                    .map { i -> "$i: ${get(i)}" }
                    .joinToString(", ")
}