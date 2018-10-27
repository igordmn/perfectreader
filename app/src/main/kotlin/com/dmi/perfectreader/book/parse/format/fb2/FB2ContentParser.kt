package com.dmi.perfectreader.book.parse.format.fb2

import com.dmi.perfectreader.book.content.BookDescription
import com.dmi.perfectreader.book.content.Content
import com.dmi.perfectreader.book.content.TableOfContents
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.obj.ContentFrame
import com.dmi.perfectreader.book.content.obj.ContentParagraph
import com.dmi.perfectreader.book.parse.BookContentParser
import com.dmi.perfectreader.book.parse.CharsetDetector
import com.dmi.perfectreader.book.parse.format.fb2.entities.FictionBook
import com.dmi.perfectreader.book.parse.format.fb2.entities.parseFictionBook
import com.google.common.io.ByteSource

class FB2ContentParser(
        private val charsetDetector: CharsetDetector,
        private val source: ByteSource,
        private val fileName: String
) : BookContentParser {
    override fun parse(): Content {
        // xml parser doesn't detect charset automatically
        val charset = charsetDetector.detectForXML(source)
        val contentBuilder = Content.Builder()

        val chapters = ArrayList<TableOfContents.Chapter>()

        val fictionBook: FictionBook = source
                .openBufferedStream()
                .reader(charset)
                .use(::parseFictionBook)

        val description = run {
            val titleInfo = fictionBook.description?.titleInfo
            val author = titleInfo?.compositeAuthorName()
            val name = titleInfo?.bookTitle
            BookDescription(author, name, fileName)
        }

        val tableOfContents = if (chapters.isNotEmpty()) TableOfContents(chapters) else null

        contentBuilder.add(toContentObject("d", LocationRange(Location(0.0), Location(1.0))))
        return contentBuilder.build(description, tableOfContents)
    }

    private fun toContentObject(text: String, range: LocationRange) = ContentFrame(
            ContentParagraph(null, listOf(
                    ContentParagraph.Run.Text(text, null, range)
            ), null),
            null
    )
}