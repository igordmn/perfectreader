package com.dmi.util.collection

import com.dmi.util.lang.MathExt.modPositive
import com.google.common.base.Preconditions.checkArgument
import java.lang.Math.abs
import java.util.*

class DuplexBuffer<T>(private val maxRelativeIndex: Int) {
    private val items: Array<Any?>
    private var shift = 0

    init {
        items = arrayOfNulls<Any>(2 * maxRelativeIndex + 1)
    }

    operator fun set(relativeIndex: Int, item: T) {
        checkArgument(abs(relativeIndex) <= maxRelativeIndex)
        items[arrayIndex(relativeIndex)] = item
    }

    @Suppress("UNCHECKED_CAST")
    operator fun get(relativeIndex: Int): T {
        checkArgument(abs(relativeIndex) <= maxRelativeIndex)
        return items[arrayIndex(relativeIndex)] as T
    }

    fun shiftLeft() {
        shift--
    }

    fun shiftRight() {
        shift++
    }

    fun clear() {
        Arrays.fill(items, null)
    }

    fun maxRelativeIndex(): Int {
        return maxRelativeIndex
    }

    private fun arrayIndex(relativeIndex: Int): Int {
        return modPositive(maxRelativeIndex + relativeIndex - shift, items.size)
    }
}
