package com.dmi.perfectreader.book.parse.format

import com.dmi.perfectreader.book.content.BookDescription
import com.dmi.perfectreader.book.content.Content
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.obj.ContentFrame
import com.dmi.perfectreader.book.content.obj.ContentParagraph
import com.dmi.perfectreader.book.parse.BookContentParser
import com.dmi.perfectreader.book.parse.CharsetDetector
import com.google.common.io.ByteSource
import com.kursx.parser.fb2.FictionBookExt

class FB2ContentParser(
        private val charsetDetector: CharsetDetector,
        private val source: ByteSource,
        private val fileName: String
) : BookContentParser {
    override fun parse(): Content {
        val charset = charsetDetector.detect(source)
        val contentBuilder = Content.Builder()
        val fictionBook = source.openBufferedStream().use {
            FictionBookExt(it)
        }

        var begin = 0.0
        source.openBufferedStream().reader(charset).forEachLine { text ->
            if (text.isNotEmpty()) {
                val end = begin + text.length
                val range = LocationRange(Location(begin), Location(end))
                contentBuilder.add(toContentObject(text, range))
                begin = end
            }
        }

        return contentBuilder.build(
                BookDescription(author = null, name = null, fileName = fileName),
                tableOfContents = null
        )
    }

    private fun toContentObject(text: String, range: LocationRange) = ContentFrame(
            ContentParagraph(null, listOf(
                    ContentParagraph.Run.Text(text, null, range)
            ), null, range),
            null,
            range
    )
}