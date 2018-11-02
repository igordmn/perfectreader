package com.dmi.util.range

import kotlin.math.min

/**
 * // todo endInclusive should be inclusive, otherwise we should use range with exclusive end
 * if list contains range, which contains location (>= begin, < endInclusive), return index of that range
 * if list doesn't contain, return index of greater range
 * if there is no greater range, return lower range
 */
fun <T, L : Comparable<L>> List<T>.definitelySearchRangeIndex(
        getRange: T.() -> ClosedRange<L>, location: L,
        fromIndex: Int = 0, toIndex: Int = size
): Int {
    require(fromIndex in 0..size)
    require(toIndex in 0..size)

    return min(size - 1, -1 - binarySearch(fromIndex, toIndex) {
        val range = it.getRange()
        if (range.endInclusive <= location) {
            -1
        } else {
            1
        }
    })
}

fun <T: ClosedRange<L>, L : Comparable<L>> List<T>.definitelySearchRangeIndex(
        location: L,
        fromIndex: Int = 0, toIndex: Int = size
) = definitelySearchRangeIndex({ this }, location, fromIndex, toIndex)