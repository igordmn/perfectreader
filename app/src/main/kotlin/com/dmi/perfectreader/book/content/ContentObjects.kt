package com.dmi.perfectreader.book.content

import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.percentOf
import com.dmi.perfectreader.book.content.location.sublocation
import com.dmi.perfectreader.book.content.obj.ContentObject
import com.dmi.util.range.PercentRanges
import com.dmi.util.range.globalPercent
import com.dmi.util.range.indexOfNearestRange
import com.dmi.util.range.localPercent

class ContentObjects(
        val list: List<ContentObject>,
        private val percentRanges: PercentRanges,
        val length: Double
) {
    init {
        require(list.isNotEmpty())
        require(list.size == percentRanges.size)
    }

    fun locationToPercent(location: Location): Double {
        val index = list.indexOfNearestRange({ range }, location)
        val locationRange = list[index].range
        val percentRange = percentRanges[index]
        val objLocalPercent = locationRange.percentOf(location).coerceIn(0.0..1.0)
        return percentRange.globalPercent(objLocalPercent)
    }

    fun percentToLocation(percent: Double): Location {
        val index = percentRanges.indexOfNearest(percent)
        val locationRange = list[index].range
        val percentRange = percentRanges[index]
        val objLocalPercent = percentRange.localPercent(percent)
        return locationRange.sublocation(objLocalPercent)
    }
}

fun ContentObjects(list: List<ContentObject>): ContentObjects {
    val percentRanges = PercentRanges()
    var length = 0.0

    for (obj in list) {
        percentRanges.add(obj.length)
        length += obj.length
    }

    return ContentObjects(list, percentRanges, length)
}