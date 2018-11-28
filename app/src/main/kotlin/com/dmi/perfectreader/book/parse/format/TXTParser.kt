package com.dmi.perfectreader.book.parse.format

import com.dmi.perfectreader.book.content.BookDescription
import com.dmi.perfectreader.book.content.Content
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.parse.BookParser
import com.dmi.perfectreader.book.parse.CharsetDetector
import com.dmi.perfectreader.book.parse.format.txt.txtParagraphs
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

        source.openBufferedStream().withoutUtfBom().reader(charset).useLines { lines ->
            lines.txtParagraphs().forEach { par ->
                val range = LocationRange(
                        Location(par.positions.first.toDouble()),
                        Location(par.positions.last.toDouble())
                )
                root.frame {
                    paragraph(par.text, range)
                }
            }
        }

        content.build(description())
    }

    override suspend fun description() = BookDescription(author = null, name = null, fileName = fileName, cover = null)
    override suspend fun descriptionOnFail() = description()
}