package com.dmi.util.range

fun <T, L : Comparable<L>> List<T>.indexOfNearestRange(getRange: T.() -> Range<L>, location: L, fromIndex: Int = 0, toIndex: Int = size): Int {
    require(fromIndex >= 0 && fromIndex <= size)
    require(toIndex >= 0 && toIndex <= size)

    val rangeAt = { index: Int -> get(index).getRange() }
    return indexOfNearestRange(rangeAt, location, fromIndex, toIndex)
}

fun <L : Comparable<L>> indexOfNearestRange(rangeAt: (index: Int) -> Range<L>, location: L, fromIndex: Int, toIndex: Int): Int {
    require(toIndex - fromIndex >= 1)

    var low = fromIndex
    var high = toIndex - 1
    var index = 0

    while (low <= high) {
        val mid = (low + high).ushr(1)
        val midRange = rangeAt(mid)

        if (location >= midRange.end) {
            low = mid + 1
            index = mid
        } else if (location < midRange.begin) {
            high = mid - 1
        } else {
            index = mid
            break
        }
    }

    return index
}