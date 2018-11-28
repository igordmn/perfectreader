package com.dmi.perfectreader.book.parse.format.txt

fun Sequence<String>.txtParagraphs(): Sequence<TXTParagraph> {
    val iterator = iterator()
    val first = iterator.asSequence().take(200).toList()

    val isColumn = first.size >= 50 && first.maxBy { it.length }!!.length <= 100

    return if (isColumn) {
        txtParagraphsByColumn(first.asSequence() + iterator.asSequence())
    } else {
        txtParagraphsByLineBreak(first.asSequence() + iterator.asSequence())
    }
}

private fun txtParagraphsByColumn(lines: Sequence<String>): Sequence<TXTParagraph> = sequence {
    class Info(val trimmed: String, val indent: String)

    var begin = 0L

    val paragraphText = StringBuilder()
    var paragraphBegin = 0L
    var paragraphEnd = 0L

    suspend fun SequenceScope<TXTParagraph>.commitParagraph() {
        if (paragraphText.isNotEmpty())
            yield(TXTParagraph(paragraphText.toString(), paragraphBegin..paragraphEnd))
        paragraphBegin = paragraphEnd
        paragraphText.clear()
    }

    var previous: Info? = null

    lines.forEach { text ->
        val end = begin + text.length
        paragraphEnd = end

        val info = Info(
                trimmed = text.trim(),
                indent = text.indent().replace("\t", "    ")
        )

        if (previous != null &&
            (info.indent.length > previous!!.indent.length || previous!!.trimmed.isEmpty())
        ) {
            commitParagraph()
        }

        if (info.trimmed.isNotEmpty()) {
            if (paragraphText.isNotEmpty())
                paragraphText.append(" ")
            paragraphText.append(info.trimmed)
        }

        begin = end
        previous = info
    }

    commitParagraph()
}

private fun txtParagraphsByLineBreak(lines: Sequence<String>): Sequence<TXTParagraph> = sequence {
    var begin = 0L
    var end: Long
    lines.forEach { text ->
        end = begin + text.length
        val trimmed = text.trim()
        if (trimmed.isNotEmpty())
            yield(TXTParagraph(trimmed, begin..end))
        begin = end
    }
}

private fun String.indent(): String {
    for (index in this.indices)
        if (!Character.isWhitespace(this[index]))
            return substring(0, index)
    return ""
}

class TXTParagraph(val text: String, val positions: LongRange)