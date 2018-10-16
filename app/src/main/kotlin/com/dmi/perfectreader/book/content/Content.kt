package com.dmi.perfectreader.book.content

import com.dmi.perfectreader.book.content.location.LocatedSequence
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.percentOf
import com.dmi.perfectreader.book.content.location.sublocation
import com.dmi.perfectreader.book.content.obj.ContentObject
import com.dmi.util.range.PercentRanges
import com.dmi.util.range.globalPercent
import com.dmi.util.range.indexOfNearestRange
import com.dmi.util.range.localPercent
import java.io.FileNotFoundException
import java.util.*

class Content private constructor(
        private val objects: List<ContentObject>,
        private val percentRanges: PercentRanges,
        val length: Double,
        val description: BookDescription,
        val tableOfContents: TableOfContents?
) {
    init {
        require(objects.isNotEmpty())
        require(objects.size == percentRanges.size)
    }

    val openResource = { _: String -> throw FileNotFoundException() }

    val sequence: LocatedSequence<ContentObject> = ContentObjectSequence(objects)

    fun locationToPercent(location: Location): Double {
        val index = objects.indexOfNearestRange({ range }, location)
        val locationRange = objects[index].range
        val percentRange = percentRanges[index]
        // todo PR-576 В Content, если проценты объектов не друг за другом, при вычислении будет ошибка
        val objLocalPercent = locationRange.percentOf(location)
        return percentRange.globalPercent(objLocalPercent)
    }

    fun percentToLocation(percent: Double): Location {
        val index = percentRanges.indexOfNearest(percent)
        val locationRange = objects[index].range
        val percentRange = percentRanges[index]
        // todo PR-576 В Content, если проценты объектов не друг за другом, при вычислении будет ошибка
        val objLocalPercent = percentRange.localPercent(percent)
        return locationRange.sublocation(objLocalPercent)
    }

    class Builder {
        private val objects = ArrayList<ContentObject>(1024)
        private val percentRanges = PercentRanges()
        private var length = 0.0

        fun add(obj: ContentObject) {
            objects.add(obj)
            percentRanges.add(obj.length)
            length += obj.length
        }

        fun build(
                description: BookDescription,
                tableOfContents: TableOfContents?
        ) = Content(objects, percentRanges, length, description, tableOfContents)
    }
}