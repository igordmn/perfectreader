package com.dmi.util.lang

fun String.splitIntoTwoLines(): Pair<String, String?> {
    val trimmed = trim()
    val index = trimmed.indexOfSpaceNearMiddle()
    return if (index >= 0) {
        trimmed.substring(0 until index) to trimmed.substring(index + 1 until trimmed.length)
    } else {
        trimmed to null
    }
}

@Suppress("UnnecessaryVariable")
private fun String.indexOfSpaceNearMiddle(): Int {
    if (length == 0) return -1

    val middle = length / 2
    var left = middle
    var right = middle
    while (left >= 0 || right < length) {
        if (left >= 0 && this[left] == ' ')
            return left
        if (right < length && this[right] == ' ')
            return right
        left--
        right++
    }

    return -1
}