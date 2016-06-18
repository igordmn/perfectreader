package com.dmi.perfectreader.fragment.book.content

import com.dmi.perfectreader.fragment.book.layout.pagination.LocatedSequence
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationConverter
import com.dmi.perfectreader.fragment.book.obj.content.ContentObject
import com.dmi.util.collection.ListSequenceEntry
import com.dmi.util.collection.SequenceEntry
import com.dmi.util.range.Range
import com.dmi.util.range.indexOfNearestRange
import java.io.InputStream
import java.util.*

class BookContent private constructor(
        private val objects: ContentObjects,
        private val percentRanges: PercentRanges,
        val openResource: (path: String) -> InputStream
) : LocatedSequence<ContentObject> {
    init {
        require(objects.size > 0)
        require(objects.size == percentRanges.size)
    }

    val locationConverter = object : LocationConverter {
        override fun locationToPercent(location: Location): Double {
            val index = objects.indexOf(location)
            val locationRange = objects.rangeOf(index)
            val percentRange = percentRanges[index]
            val locationLocalPercent = locationRange.percentOf(location)
            return percentRange.globalPercent(locationLocalPercent)
        }

        override fun percentToLocation(percent: Double): Location {
            val index = percentRanges.indexOf(percent)
            val locationRange = objects.rangeOf(index)
            val percentRange = percentRanges[index]
            val percentLocalPercent = percentRange.localPercent(percent)
            return locationRange.sublocation(percentLocalPercent)
        }
    }

    override fun get(location: Location): SequenceEntry<ContentObject> = objects.sequenceAt(location)

    class Builder {
        private val objects = ContentObjects()
        private val percentRanges = PercentRanges()

        fun addObject(obj: ContentObject): Builder {
            objects.add(obj)
            percentRanges.add(obj.length)
            return this
        }

        fun build(openResource: (path: String) -> InputStream): BookContent {
            require(objects.size > 0)
            return BookContent(objects, percentRanges, openResource)
        }
    }

    private class ContentObjects {
        private val list = ArrayList<ContentObject>(1024)

        val size: Int get() = list.size

        fun add(obj: ContentObject) {
            if (list.size > 0)
                require(list.last().range.end <= obj.range.begin)

            list.add(obj)
        }

        fun indexOf(location: Location) = list.indexOfNearestRange({ range }, location)
        fun rangeOf(index: Int) = list[index].range

        fun sequenceAt(location: Location): SequenceEntry<ContentObject> {
            require(size > 0)
            return ListSequenceEntry(list, indexOf(location))
        }
    }

    private class PercentRanges {
        private val lengthBegins = ArrayList<Double>(1024)
        private var lengthEnd = 0.0

        var size: Int = 0
            private set

        fun add(length: Double) {
            lengthBegins.add(lengthEnd)
            lengthEnd += length
            size++
        }

        fun beginOf(index: Int) = lengthBegins[index] / lengthEnd
        fun endOf(index: Int) = if (index < size - 1) lengthBegins[index + 1] / lengthEnd else 1.0

        fun indexOf(percent: Double) = indexOfNearestRange({ get(it) }, percent, 0, size)

        operator fun get(index: Int) = PercentRange(beginOf(index), endOf(index))
    }

    private data class PercentRange(override val begin: Double, override val end: Double) : Range<Double> {
        init {
            require(begin >= 0.0 && begin <= 1.0)
            require(end >= 0.0 && end <= 1.0)
        }

        fun globalPercent(localPercent: Double): Double {
            require(localPercent >= 0.0 && localPercent <= 1.0)
            return begin + localPercent * (end - begin)
        }

        fun localPercent(globalPercent: Double): Double {
            require(globalPercent >= 0.0 && globalPercent <= 1.0)
            require(globalPercent >= begin && globalPercent <= end)
            return (globalPercent - begin) / (end - begin)
        }
    }
}