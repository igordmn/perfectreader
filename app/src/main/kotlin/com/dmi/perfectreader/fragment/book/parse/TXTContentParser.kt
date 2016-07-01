package com.dmi.perfectreader.fragment.book.parse

import com.dmi.perfectreader.fragment.book.content.Content
import com.dmi.perfectreader.fragment.book.content.obj.ContentFrame
import com.dmi.perfectreader.fragment.book.content.obj.ContentParagraph
import com.dmi.perfectreader.fragment.book.content.obj.param.ContentFontStyle
import com.dmi.perfectreader.fragment.book.content.obj.param.StyleType
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.google.common.io.ByteSource

class TXTContentParser(
        private val charsetDetector: CharsetDetector,
        private val source: ByteSource
) : BookContentParser {
    override fun parse(): Content {
        val charset = charsetDetector.detect(source)

        val contentBuilder = Content.Builder()
        val style = ContentFontStyle(null, null)

        var begin = 0.0
        source.openBufferedStream().reader(charset).forEachLine() { text ->
            val end = begin + text.length
            val range = LocationRange(Location(begin), Location(end))
            contentBuilder.addObject(toContentObject(text, style, range))
            begin = end
        }

        return contentBuilder.build()
    }

    private fun toContentObject(text: String, style: ContentFontStyle, range: LocationRange) = ContentFrame(
            StyleType.PARAGRAPH,
            ContentFrame.Margins(null, null, null, null),
            ContentFrame.Paddings(null, null, null, null),
            ContentFrame.Borders(border, border, border, border),
            ContentFrame.Background(null),
            ContentParagraph(StyleType.PARAGRAPH, null, listOf(textRun(text, style, range)), null, null, range),
            range
    )

    private val border = ContentFrame.Border(null, null)

    private fun textRun(text: String, style: ContentFontStyle, range: LocationRange) = ContentParagraph.Run.Text(text, style, range)
}