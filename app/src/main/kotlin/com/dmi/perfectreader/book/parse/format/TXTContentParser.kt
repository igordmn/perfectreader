package com.dmi.perfectreader.book.parse.format

import com.dmi.perfectreader.book.content.BookDescription
import com.dmi.perfectreader.book.content.Content
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.obj.ContentFrame
import com.dmi.perfectreader.book.content.obj.ContentParagraph
import com.dmi.perfectreader.book.content.obj.common.ContentClass
import com.dmi.perfectreader.book.parse.BookContentParser
import com.dmi.perfectreader.book.parse.CharsetDetector
import com.dmi.util.io.withoutUtfBom
import com.google.common.io.ByteSource

class TXTContentParser(
        private val charsetDetector: CharsetDetector,
        private val source: ByteSource,
        private val fileName: String
) : BookContentParser {
    override fun parse(): Content {
        val charset = charsetDetector.detect(source)
        val contentBuilder = Content.Builder()

        var begin = 0.0

        source.openBufferedStream().withoutUtfBom().reader(charset).forEachLine { text ->
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
            ), ContentClass.PARAGRAPH),
            ContentClass.PARAGRAPH
    )
}