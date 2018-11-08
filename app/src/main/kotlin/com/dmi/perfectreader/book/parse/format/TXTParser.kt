package com.dmi.perfectreader.book.parse.format

import com.dmi.perfectreader.book.content.BookDescription
import com.dmi.perfectreader.book.content.Content
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.parse.BookParser
import com.dmi.perfectreader.book.parse.CharsetDetector
import com.dmi.util.io.withoutUtfBom
import com.google.common.io.ByteSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TXTParser(
        private val charsetDetector: CharsetDetector,
        private val source: ByteSource,
        private val fileName: String
) : BookParser {
    override suspend fun content(): Content = withContext(Dispatchers.IO) {
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

        content.build(description())
    }

    override suspend fun description() = BookDescription(author = null, name = null, fileName = fileName, cover = null)
    override suspend fun descriptionOnFail() = description()
}