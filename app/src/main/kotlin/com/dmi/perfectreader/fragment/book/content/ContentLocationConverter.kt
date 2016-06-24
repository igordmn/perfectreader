package com.dmi.perfectreader.fragment.book.content

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationConverter
import com.dmi.perfectreader.fragment.book.content.obj.ContentObject
import com.dmi.util.range.PercentRanges
import com.dmi.util.range.indexOfNearestRange

class ContentLocationConverter(
        private val objects: List<ContentObject>
) : LocationConverter {
    private val percentRanges = PercentRanges()

    init {
        objects.forEach {
            percentRanges.add(it.length)
        }
    }

    override fun locationToPercent(location: Location): Double {
        val index = objects.indexOfNearestRange({ range }, location)
        val locationRange = objects[index].range
        val percentRange = percentRanges[index]
        val locationLocalPercent = locationRange.percentOf(location)
        return percentRange.globalPercent(locationLocalPercent)
    }

    override fun percentToLocation(percent: Double): Location {
        val index = percentRanges.indexOfNearest(percent)
        val locationRange = objects[index].range
        val percentRange = percentRanges[index]
        val percentLocalPercent = percentRange.localPercent(percent)
        return locationRange.sublocation(percentLocalPercent)
    }
}