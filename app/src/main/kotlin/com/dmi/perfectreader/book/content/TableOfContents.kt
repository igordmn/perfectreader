package com.dmi.perfectreader.book.content

import com.dmi.perfectreader.book.content.location.Location
import java.util.*

class TableOfContents(val chapters: List<Chapter>) {
    init {
        require(chapters.isNotEmpty())
    }

    private val locationToChapter = TreeMap<Location, Chapter>().apply {
        fun Chapter.addToMap() {
            if (children.isEmpty()) {
                put(location, this)
            } else {
                children.forEach(Chapter::addToMap)
            }
        }

        chapters.forEach(Chapter::addToMap)
    }

    fun chapterAt(location: Location): Chapter? = locationToChapter.floorEntry(location)?.value

    class Chapter(val name: String, val location: Location, val children: List<Chapter>)
}