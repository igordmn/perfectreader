package com.dmi.perfectreader.book.content

import com.dmi.perfectreader.book.content.obj.ContentParagraph
import com.dmi.perfectreader.book.location.LocationRange
import com.dmi.perfectreader.book.location.clamp

fun Content.plainText(range: LocationRange): String {
    val plainText = StringBuilder()

    val leafSequence = ContentLeafSequence(sequence)
    var entry = leafSequence[range.begin]
    var isFirstParagraph = true

    while (entry.hasNext && entry.item.range.begin <= range.end) {
        val obj = entry.item
        if (obj is ContentParagraph) {
            if (!isFirstParagraph)
                plainText.append('\n')
            obj.appendPlainText(plainText, range)
            isFirstParagraph = false
        }
        entry = entry.next
    }

    return plainText.toString()
}

fun ContentParagraph.appendPlainText(builder: StringBuilder, range: LocationRange) {
    for (run in runs) {
        if (run is ContentParagraph.Run.Text) {
            val begin = clamp(range.begin, run.range)
            val end = clamp(range.end, run.range)
            val beginIndex = run.charIndex(begin)
            val endIndex = run.charIndex(end)
            if (endIndex > beginIndex)
                builder.append(run.text.substring(beginIndex, endIndex))
        }
    }
}