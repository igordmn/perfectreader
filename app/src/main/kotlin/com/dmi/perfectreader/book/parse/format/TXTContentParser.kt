package com.dmi.perfectreader.book.parse.format

import com.dmi.perfectreader.book.content.BookDescription
import com.dmi.perfectreader.book.content.Content
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
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
        val content = Content.Builder()
        val root = content.root(locale = null)

        var begin = 0.0

        source.openBufferedStream().withoutUtfBom().reader(charset).forEachLine { text ->
            if (text.isNotEmpty()) {
                val end = begin + text.length
                val range = LocationRange(Location(begin), Location(end))
                root.frame {
                    paragraph(text, range)
                }
                begin = end
            }
        }

        return content.build(BookDescription(author = null, name = null, fileName = fileName))
    }
}