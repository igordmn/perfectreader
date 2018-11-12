package com.dmi.perfectreader.book.content

import com.dmi.perfectreader.book.content.location.*
import com.dmi.perfectreader.book.content.obj.ContentObject
import com.dmi.perfectreader.book.content.obj.ContentParagraph
import com.dmi.perfectreader.book.content.obj.common.ContentConfig
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.collections.forEachReversedWithIndex
import org.jetbrains.anko.collections.forEachWithIndex
import java.util.*

class ContentText(private val content: Content, private val config: ContentConfig) {
    fun plain(range: LocationRange): String = runBlocking {
        val plainText = StringBuilder()

        val leafSequence = contentLeafSequence(content.sequence)
        var entry = leafSequence.get(range.start)
        var isFirstParagraph = true

        while (entry.hasNext && entry.item.range.start <= range.endInclusive) {
            val obj = entry.item
            if (obj is ContentParagraph) {
                if (!isFirstParagraph)
                    plainText.append('\n')
                plainText.appendPlainText(obj, range)
                isFirstParagraph = false
            }
            entry = entry.next()
        }

        return@runBlocking plainText.toString()
    }

    fun locale(range: LocationRange): Locale? = runBlocking {
        val leafSequence = contentLeafSequence(content.sequence)
        var entry = leafSequence.get(range.start)

        while (entry.hasNext && entry.item.range.start <= range.endInclusive) {
            val obj = entry.item
            if (obj is ContentParagraph)
                return@runBlocking obj.configure(config).locale
            entry = entry.next()
        }

        return@runBlocking null
    }

    fun leafs(): LocatedSequence<ContentObject> = contentLeafSequence(content.sequence)

    fun wordEndAfter(location: Location): Location? = runBlocking {
        val leafSequence = contentLeafSequence(content.sequence)
        var entry = leafSequence.get(location)
        while (entry.hasNext) {
            val obj = entry.item
            if (obj is ContentParagraph && obj.range.endInclusive > location) {
                val wordEnd = obj.wordEndAfter(location)
                if (wordEnd != null)
                    return@runBlocking wordEnd
            }
            entry = entry.next()
        }
        return@runBlocking null
    }

    fun wordBeginBefore(location: Location): Location? = runBlocking {
        val leafSequence = contentLeafSequence(content.sequence)
        var entry = leafSequence.get(location)
        while (entry.hasPrevious) {
            val obj = entry.item
            if (obj is ContentParagraph && obj.range.start < location) {
                val wordEnd = obj.wordBeginBefore(location)
                if (wordEnd != null)
                    return@runBlocking wordEnd
            }
            entry = entry.previous()
        }
        return@runBlocking null
    }
}

fun ContentParagraph.wordEndAfter(location: Location): Location? {
    runs.forEachWithIndex { runIndex, run ->
        val range = run.range
        if (run is ContentParagraph.Run.Text && location <= range.endInclusive) {
            val text = run.text
            val locationCharIndex = if (location >= range.start) textIndexAt(text, range, location) else 0
            for (charIndex in locationCharIndex + 1..text.length) {
                if (isWordEnd(run, runIndex, charIndex))
                    return run.charLocation(charIndex)
            }
        }
    }
    return null
}

fun ContentParagraph.wordBeginBefore(location: Location): Location? {
    runs.forEachReversedWithIndex { runIndex, run ->
        val range = run.range
        if (run is ContentParagraph.Run.Text && location >= range.start) {
            val text = run.text
            val locationCharIndex = if (location <= range.endInclusive) textIndexAt(text, range, location) else text.length
            for (charIndex in locationCharIndex - 1 downTo 0) {
                if (isWordBegin(run, runIndex, charIndex))
                    return run.charLocation(charIndex)
            }
        }
    }
    return null
}

fun ContentParagraph.isWordBegin(run: ContentParagraph.Run.Text, runIndex: Int, charIndex: Int): Boolean {
    require(charIndex >= 0 && charIndex < run.text.length)

    val leftCharType = if (charIndex > 0) {
        typeOf(run.text[charIndex - 1])
    } else if (runIndex > 0) {
        val previousRun = runs[runIndex - 1]
        if (previousRun is ContentParagraph.Run.Text) {
            typeOf(previousRun.text[previousRun.text.length - 1])
        } else {
            CharType.OTHER
        }
    } else {
        CharType.SPACE
    }
    val rightCharType = typeOf(run.text[charIndex])

    return (rightCharType == CharType.LETTER_OR_DIGIT || rightCharType == CharType.OTHER) && leftCharType == CharType.SPACE ||
           leftCharType == CharType.LETTER_OR_DIGIT && rightCharType == CharType.OTHER ||
           leftCharType == CharType.OTHER && rightCharType == CharType.LETTER_OR_DIGIT
}

fun ContentParagraph.isWordEnd(run: ContentParagraph.Run.Text, runIndex: Int, charIndex: Int): Boolean {
    require(charIndex > 0 && charIndex <= run.text.length)

    val leftCharType = typeOf(run.text[charIndex - 1])
    val rightCharType = if (charIndex < run.text.length) {
        typeOf(run.text[charIndex])
    } else if (runIndex < runs.size - 1) {
        val nextRun = runs[runIndex + 1]
        if (nextRun is ContentParagraph.Run.Text) {
            typeOf(nextRun.text[0])
        } else {
            CharType.OTHER
        }
    } else {
        CharType.SPACE
    }

    return (leftCharType == CharType.LETTER_OR_DIGIT || leftCharType == CharType.OTHER) && rightCharType == CharType.SPACE ||
           leftCharType == CharType.LETTER_OR_DIGIT && rightCharType == CharType.OTHER ||
           leftCharType == CharType.OTHER && rightCharType == CharType.LETTER_OR_DIGIT
}

enum class CharType { SPACE, LETTER_OR_DIGIT, OTHER }

fun typeOf(ch: Char) = when {
    Character.isSpaceChar(ch) || Character.isWhitespace(ch) -> CharType.SPACE
    Character.isLetter(ch) || Character.isDigit(ch) -> CharType.LETTER_OR_DIGIT
    else -> CharType.OTHER
}

fun StringBuilder.appendPlainText(obj: ContentParagraph, range: LocationRange = obj.range) {
    for (run in obj.runs) {
        if (run is ContentParagraph.Run.Text) {
            val begin = clamp(range.start, run.range)
            val end = clamp(range.endInclusive, run.range)
            val beginIndex = run.charIndex(begin)
            val endIndex = run.charIndex(end)
            if (endIndex > beginIndex)
                append(run.text.substring(beginIndex, endIndex))
        }
    }
}

suspend inline fun ContentText.continuousTexts(action: (ContinuousText) -> Unit) {
    var entry = leafs().get(Location(0.0))

    while (entry.hasNext) {
        val obj = entry.item
        if (obj is ContentParagraph) {
            obj.continuousTexts {
                action(it)
            }
        }
        entry = entry.next()
    }
}

inline fun ContentParagraph.continuousTexts(action: (ContinuousText) -> Unit) {
    val runs = ArrayList<ContentParagraph.Run.Text>()

    for (run in this@continuousTexts.runs) {
        if (run is ContentParagraph.Run.Text) {
            runs.add(run)
        } else {
            if (runs.size > 0)
                action(ContinuousText(runs))
            runs.clear()
        }
    }

    if (runs.size > 0)
        action(ContinuousText(runs))
}

class ContinuousText(private val runs: List<ContentParagraph.Run.Text>) {
    init {
        require(runs.isNotEmpty())
    }

    val range = LocationRange(runs.first().range.start, runs.first().range.endInclusive)

    private val string = StringBuilder().apply {
        for (run in runs)
            append(run.text)
    }.toString()

    override fun toString() = string

    fun rangeOf(indices: IntRange) = textSubRange(string, range, indices.start, indices.endInclusive)
}