package com.dmi.perfectreader.book.content

import com.dmi.perfectreader.book.content.obj.ContentObject
import com.dmi.perfectreader.book.location.LocatedSequence
import com.dmi.perfectreader.book.location.Location
import com.dmi.util.range.PercentRanges
import com.dmi.util.range.indexOfNearestRange
import java.io.FileNotFoundException
import java.util.*

class Content private constructor(
        private val objects: List<ContentObject>
) {
    private val percentRanges = PercentRanges()
    val length: Double

    init {
        require(objects.size > 0)

        var length = 0.0
        objects.forEach {
            percentRanges.add(it.length)
            length += it.length
        }
        this.length = length
    }

    val openResource = { path: String -> throw FileNotFoundException() }

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

        fun addObject(obj: ContentObject): Builder {
            objects.add(obj)
            return this
        }

        fun build(): Content {
            require(objects.size > 0)
            return Content(objects)
        }
    }
}