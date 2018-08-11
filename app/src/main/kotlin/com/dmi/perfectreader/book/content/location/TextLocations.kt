package com.dmi.perfectreader.book.content.location

fun textIndexAt(text: CharSequence, range: LocationRange, location: Location): Int {
    require(location >= range.start && location <= range.endInclusive)
    return Math.round(range.percentOf(location) * text.length).toInt()
}

fun textSubRange(text: CharSequence, range: LocationRange, beginIndex: Int, endIndex: Int): LocationRange {
    require(beginIndex >= 0 && beginIndex <= text.length)
    require(endIndex >= 0 && endIndex <= text.length)
    return range.subrange(beginIndex.toDouble() / text.length, endIndex.toDouble() / text.length)
}

fun textSubLocation(text: CharSequence, range: LocationRange, index: Int): Location {
    require(index >= 0 && index <= text.length)
    return range.sublocation(index.toDouble() / text.length)
}