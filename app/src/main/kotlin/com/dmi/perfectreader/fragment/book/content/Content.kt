package com.dmi.perfectreader.fragment.book.content

import com.dmi.perfectreader.fragment.book.location.LocatedSequence
import com.dmi.perfectreader.fragment.book.location.LocationConverter
import com.dmi.perfectreader.fragment.book.content.obj.ContentObject
import java.io.FileNotFoundException
import java.util.*

class Content private constructor(
        private val objects: List<ContentObject>
) {
    init {
        require(objects.size > 0)
    }

    val openResource = { path: String -> throw FileNotFoundException() }

    val locationConverter: LocationConverter = ContentLocationConverter(objects)
    val sequence: LocatedSequence<ContentObject> = ContentObjectSequence(objects)

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