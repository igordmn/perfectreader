package com.dmi.util.range

fun <T, L : Comparable<L>> List<T>.indexOfNearestRange(getRange: T.() -> ClosedRange<L>, location: L, fromIndex: Int = 0, toIndex: Int = size): Int {
    require(fromIndex in 0..size)
    require(toIndex in 0..size)

    val rangeAt = { index: Int -> get(index).getRange() }
    return indexOfNearestRange(rangeAt, location, fromIndex, toIndex)
}

fun <L : Comparable<L>> indexOfNearestRange(rangeAt: (index: Int) -> ClosedRange<L>, location: L, fromIndex: Int, toIndex: Int): Int {
    require(toIndex - fromIndex >= 1)

    var low = fromIndex
    var high = toIndex - 1
    var index = 0

    while (low <= high) {
        val mid = (low + high).ushr(1)
        val midRange = rangeAt(mid)

        if (location >= midRange.endInclusive) {
            low = mid + 1
            index = mid
        } else if (location < midRange.start) {
            high = mid - 1
        } else {
            index = mid
            break
        }
    }

    return index
}