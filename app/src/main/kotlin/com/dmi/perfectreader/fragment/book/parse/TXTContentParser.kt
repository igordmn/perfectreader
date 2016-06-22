package com.dmi.perfectreader.fragment.book.parse

import com.dmi.perfectreader.fragment.book.content.Content
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fragment.book.obj.content.ContentFrame
import com.dmi.perfectreader.fragment.book.obj.content.ContentParagraph
import com.dmi.perfectreader.fragment.book.obj.content.param.ContentFontStyle
import com.dmi.perfectreader.fragment.book.obj.content.param.StyleType
import com.google.common.io.ByteSource

class TXTContentParser(private val source: ByteSource) : BookContentParser {
    private val charset = Charsets.UTF_8

    override fun parse(): Content {
        val contentBuilder = Content.Builder()
        var begin = 0.0
        source.openBufferedStream().reader(charset).forEachLine() { text ->
            val end = begin + text.length
            val range = LocationRange(Location(begin), Location(end))
            contentBuilder.addObject(toContentObject(text, range))
            begin = end
        }
        return contentBuilder.build()
    }

    private fun toContentObject(text: String, range: LocationRange) = ContentFrame(
            StyleType.PARAGRAPH,
            ContentFrame.Margins(null, null, null, null),
            ContentFrame.Paddings(null, null, null, null),
            ContentFrame.Borders(border, border, border, border),
            ContentFrame.Background(null),
            ContentParagraph(StyleType.PARAGRAPH, null, listOf(textRun(text, range)), null, null, range),
            range
    )

    private val border = ContentFrame.Border(null, null)

    private fun textRun(text: String, range: LocationRange) = ContentParagraph.Run.Text(text, ContentFontStyle(null, null), range)
}