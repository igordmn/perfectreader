package com.dmi.perfectreader.book.content

import com.dmi.perfectreader.book.content.obj.ContentParagraph
import com.dmi.perfectreader.book.location.Location
import com.dmi.perfectreader.book.location.textIndexAt
import com.dmi.perfectreader.book.location.textSubLocation
import org.jetbrains.anko.collections.forEachReversedWithIndex
import org.jetbrains.anko.collections.forEachWithIndex
import java.lang.Character.*

fun Content.wordEndAfter(location: Location): Location? {
    val leafSequence = ContentLeafSequence(sequence)
    var entry = leafSequence[location]
    while (entry.hasNext) {
        val obj = entry.item
        if (obj is ContentParagraph && obj.range.end > location) {
            val wordEnd = obj.wordEndAfter(location)
            if (wordEnd != null)
                return wordEnd
        }
        entry = entry.next
    }
    return null
}

fun Content.wordBeginBefore(location: Location): Location? {
    val leafSequence = ContentLeafSequence(sequence)
    var entry = leafSequence[location]
    while (entry.hasPrevious) {
        val obj = entry.item
        if (obj is ContentParagraph && obj.range.begin < location) {
            val wordEnd = obj.wordBeginBefore(location)
            if (wordEnd != null)
                return wordEnd
        }
        entry = entry.previous
    }
    return null
}

fun ContentParagraph.wordEndAfter(location: Location): Location? {
    runs.forEachWithIndex { runIndex, run ->
        val range = run.range
        if (run is ContentParagraph.Run.Text && location <= range.end) {
            val text = run.text
            val locationCharIndex = if (location >= range.begin) textIndexAt(text, range, location) else 0
            for (charIndex in locationCharIndex + 1..text.length) {
                if (isWordEnd(run, runIndex, charIndex))
                    return textSubLocation(text, range, charIndex)
            }
        }
    }
    return null
}

fun ContentParagraph.wordBeginBefore(location: Location): Location? {
    runs.forEachReversedWithIndex { runIndex, run ->
        val range = run.range
        if (run is ContentParagraph.Run.Text && location >= range.begin) {
            val text = run.text
            val locationCharIndex = if (location <= range.end) textIndexAt(text, range, location) else text.length
            for (charIndex in locationCharIndex - 1 downTo 0) {
                if (isWordBegin(run, runIndex, charIndex))
                    return textSubLocation(text, range, charIndex)
            }
        }
    }
    return null
}

private fun ContentParagraph.isWordBegin(run: ContentParagraph.Run.Text, runIndex: Int, charIndex: Int): Boolean {
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

private fun ContentParagraph.isWordEnd(run: ContentParagraph.Run.Text, runIndex: Int, charIndex: Int): Boolean {
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

private enum class CharType { SPACE, LETTER_OR_DIGIT, OTHER }

private fun typeOf(ch: Char) = when {
    isSpaceChar(ch) || isWhitespace(ch) -> CharType.SPACE
    isLetter(ch) || isDigit(ch) -> CharType.LETTER_OR_DIGIT
    else -> CharType.OTHER
}