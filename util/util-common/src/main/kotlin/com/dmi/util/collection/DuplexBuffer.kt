package com.dmi.util.collection

import com.dmi.util.lang.modPositive
import com.google.common.base.Preconditions.checkArgument
import java.lang.Math.abs

class DuplexBuffer<T>(val maxRelativeIndex: Int) {
    @Suppress("CAST_NEVER_SUCCEEDS")
    private val items: Array<T?> = arrayOfNulls<Any>(2 * maxRelativeIndex + 1) as Array<T?>
    private var shift = 0

    operator fun set(relativeIndex: Int, item: T?) {
        checkArgument(abs(relativeIndex) <= maxRelativeIndex)
        items[arrayIndex(relativeIndex)] = item
    }

    operator fun get(relativeIndex: Int): T? {
        checkArgument(abs(relativeIndex) <= maxRelativeIndex)
        return items[arrayIndex(relativeIndex)]
    }

    fun shiftLeft() = shift--

    fun shiftRight() = shift++

    fun clear() = items.fill(null)

    private fun arrayIndex(relativeIndex: Int): Int {
        return modPositive(maxRelativeIndex + relativeIndex - shift, items.size)
    }
}
