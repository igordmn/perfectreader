package com.dmi.perfectreader.book.content

import com.dmi.perfectreader.book.content.location.Location
import java.util.*
import kotlin.collections.ArrayList

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

    val allChapters: List<PlainChapter> = ArrayList<PlainChapter>().apply {
        fun PlainChapter.addToList() {
            add(this)

            original.children.forEach {
                PlainChapter(it, level + 1).addToList()
            }
        }

        chapters.forEach {
            PlainChapter(it, 0).addToList()
        }
    }

    fun chapterAt(location: Location): Chapter? = locationToChapter.floorEntry(location)?.value
    fun lowerChapter(location: Location): Chapter? = locationToChapter.lowerEntry(location)?.value
    fun higherChapter(location: Location): Chapter? = locationToChapter.higherEntry(location)?.value

    class Chapter(val name: String, val location: Location, val children: List<Chapter> = emptyList()) {
        init {
            require(name.isNotEmpty())
        }
    }

    class PlainChapter(val original: Chapter, val level: Int)
}