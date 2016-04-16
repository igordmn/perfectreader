package com.dmi.perfectreader.location

fun <T> List<T>.indexOfNearest(getRange: T.() -> BookRange, location: BookLocation, fromIndex: Int = 0, toIndex: Int = size): Int {
    check(size > 0)
    check(fromIndex >= 0 && fromIndex <= size)
    check(toIndex >= 0 && toIndex <= size)
    check(fromIndex <= toIndex)

    var low = fromIndex
    var high = toIndex - 1
    var index = 0

    while (low <= high) {
        val mid = (low + high).ushr(1)
        val midRange = getRange(get(mid))

        if (location >= midRange.end) {
            low = mid + 1
            index = mid
        } else if (location < midRange.begin)
            high = mid - 1
        else {
            index = mid
            break
        }
    }

    return index
}