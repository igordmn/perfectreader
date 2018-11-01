package com.dmi.perfectreader.book.content

import com.dmi.perfectreader.book.content.location.LocatedSequence
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.obj.ContentBox
import com.dmi.perfectreader.book.content.obj.ContentFrame
import com.dmi.perfectreader.book.content.obj.ContentObject
import com.dmi.perfectreader.book.content.obj.ContentParagraph
import com.dmi.perfectreader.book.content.obj.common.ContentClass
import com.dmi.perfectreader.book.content.obj.common.ContentCompositeClass
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList

// todo handle empty books
class Content private constructor(
        private val objects: ContentObjects,
        val description: BookDescription,
        val tableOfContents: TableOfContents?
) {
    val length: Double get() = objects.length
    val sequence: LocatedSequence<ContentObject> = ContentObjectSequence(objects.list)
    val openResource = { _: String -> throw FileNotFoundException() }

    fun locationToPercent(location: Location): Double = objects.locationToPercent(location)
    fun percentToLocation(percent: Double): Location = objects.percentToLocation(percent)

    data class SectionBuilder(
            private val objects: ArrayList<ContentObject>,
            private val chapters: ArrayList<TableOfContents.Chapter>,
            private val locale: Locale?,
            private val cls: ContentCompositeClass?,
            val chapterLevel: Int
    ) {
        fun obj(obj: ContentObject?) {
            if (obj != null)
                objects.add(obj)
        }

        fun chapter(name: String, location: Location, apply: SectionBuilder.() -> Unit) {
            val childChapters = ArrayList<TableOfContents.Chapter>()
            val chapter = TableOfContents.Chapter(name, location, childChapters)
            chapters.add(chapter)
            copy(chapters = childChapters, chapterLevel = chapterLevel + 1).apply()
        }

        fun customized(cls: ContentClass? = null, locale: Locale? = null, apply: SectionBuilder.() -> Unit) {
            val builder = if (cls == null && locale == null) {
                this
            } else {
                val newCls = if (cls != null) ContentCompositeClass(this.cls, cls) else this.cls
                val newLocale = locale ?: this.locale
                copy(locale = newLocale, cls = newCls, chapterLevel = chapterLevel)
            }
            builder.apply()
        }

        fun paragraph(apply: ContentParagraph.Builder.() -> Unit) {
            val paragraph = ContentParagraph.Builder(cls = cls, locale = locale)
            paragraph.apply()
            obj(paragraph.build())
        }

        fun paragraph(text: String, range: LocationRange) {
            obj(ContentParagraph(
                    listOf(ContentParagraph.Run.Text(text, cls, range)), cls, locale
            ))
        }

        fun frame(apply: SectionBuilder.() -> Unit) {
            val objects = ArrayList<ContentObject>()
            copy(objects = objects).apply()
            objects.trimToSize()
            if (objects.isNotEmpty()) {
                val child = if (objects.size == 1) objects.first() else ContentBox(objects)
                obj(ContentFrame(child, this.cls))
            }
        }
    }

    class Builder {
        private val objects = ArrayList<ContentObject>()
        private val chapters = ArrayList<TableOfContents.Chapter>()

        fun root(locale: Locale?) = SectionBuilder(objects, chapters, locale = locale, cls = null, chapterLevel = -1)

        fun build(description: BookDescription) = Content(
                ContentObjects(objects), description, tableOfContents()
        )

        private fun tableOfContents() = if (chapters.isNotEmpty()) TableOfContents(chapters) else null
    }
}