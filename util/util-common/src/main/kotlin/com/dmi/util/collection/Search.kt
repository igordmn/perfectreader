package com.dmi.util.collection

fun <T: Comparable<T>> List<T>.binarySearchLower(value: T, fromIndex: Int = 0, toIndex: Int = size): Int {
    var low = fromIndex
    var high = toIndex - 1
    var index = -1

    while (low <= high) {
        val mid = (low + high).ushr(1)
        val midValue = get(mid)

        if (value > midValue) {
            low = mid + 1
            index = mid
        } else if (value < midValue) {
            high = mid - 1
        } else {
            index = mid - 1
            break
        }
    }

    return index
}

fun <T: Comparable<T>> List<T>.binarySearchGreater(value: T, fromIndex: Int = 0, toIndex: Int = size): Int {
    var low = fromIndex
    var high = toIndex - 1
    var index = size

    while (low <= high) {
        val mid = (low + high).ushr(1)
        val midValue = get(mid)

        if (value > midValue) {
            low = mid + 1
        } else if (value < midValue) {
            high = mid - 1
            index = mid
        } else {
            index = mid + 1
            break
        }
    }

    return index
}

fun FloatArray.binarySearchLower(value: Float, fromIndex: Int = 0, toIndex: Int = size): Int {
    val index = binarySearch(value, fromIndex, toIndex)
    return when {
        index == fromIndex -> -1
        index > 0 -> index - 1
        else -> -index - 2
    }
}

fun FloatArray.binarySearchGreater(value: Float, fromIndex: Int = 0, toIndex: Int = size): Int {
    val index = binarySearch(value, fromIndex, toIndex)
    return when {
        index == toIndex - 1 -> size
        index >= 0 -> index + 1
        else -> -index - 1
    }
}