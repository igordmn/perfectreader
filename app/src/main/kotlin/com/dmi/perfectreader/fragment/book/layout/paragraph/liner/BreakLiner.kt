package com.dmi.perfectreader.fragment.book.layout.paragraph.liner

import com.dmi.perfectreader.fragment.book.layout.common.LayoutChars
import com.dmi.perfectreader.fragment.book.layout.paragraph.breaker.Breaker
import com.dmi.perfectreader.fragment.book.layout.paragraph.liner.Liner.Line
import com.dmi.perfectreader.fragment.book.layout.paragraph.liner.Liner.Token
import com.dmi.util.lang.ReusableArrayList
import java.lang.Math.max
import java.lang.Math.min

class BreakLiner(private val breaker: Breaker) : Liner {
    companion object {
        val PROCESS_CHARS_STEP = 4
    }

    override fun makeLines(measuredText: Liner.MeasuredText, config: Liner.Config): List<Line> {
        return object {
            val lines = Reusables.lines()
            val text = measuredText.plainText
            val locale = measuredText.locale
            val breaks = breaker.breakText(text, locale, Breaker.Config(config.hyphenation))

            fun makeLines(): List<Line> {
                var part = LinePart().apply { reset(0, config.firstLineIndent) }

                iteratePotentialEnds { end ->
                    require(end > part.endIndex)

                    part.setEnd(end, false)
                    if (part.right > config.maxWidth) {
                        part = fitPartByBreak(part, part.beginIndex, part.endIndex)
                        pushLine(part)
                        part.reset(part.endIndex, 0F)
                    }

                    return@iteratePotentialEnds part.endIndex
                }

                if (!part.isEmpty)
                    pushLine(part)

                return lines
            }

            fun iteratePotentialEnds(action: (end: Int) -> Int) {
                if (text.length > 0) {
                    var end = min(PROCESS_CHARS_STEP, text.length)
                    while (end < text.length)
                        end = action(end) + PROCESS_CHARS_STEP

                    end = text.length
                    while (end <= text.length)
                        end = action(text.length) + 1
                }
            }

            fun fitPartByBreak(part: LinePart, beginIndex: Int, endIndex: Int): LinePart {
                var i = endIndex - 1
                while (i > beginIndex) {
                    if (breaks.hasBreakBefore(i)) {
                        part.setEnd(i, breaks.hasHyphenBefore(i))
                        if (part.right <= config.maxWidth)
                            return part
                    }
                    i--
                }

                return fitPartCharByChar(part, beginIndex, endIndex)
            }

            fun fitPartCharByChar(part: LinePart, beginIndex: Int, endIndex: Int): LinePart {
                var end = endIndex - 1
                while (end > beginIndex) {
                    part.setEnd(end, false)
                    if (part.right <= config.maxWidth)
                        return part
                    end--
                }

                return fitMinimalChars(part, beginIndex)
            }

            fun fitMinimalChars(part: LinePart, beginIndex: Int): LinePart {
                part.setEnd(beginIndex + 1, false)
                val minimalWidth = part.right

                var end = beginIndex + 2
                while (end <= text.length) {
                    part.setEnd(end, false)
                    if (part.right > minimalWidth)
                        break
                    end++
                }

                part.setEnd(end - 1, false)
                return part
            }

            fun pushLine(part: LinePart) {
                require(part.endIndex > part.beginIndex)
                lines.add(
                        Line().apply {
                            this.left = part.left
                            this.width = part.width
                            this.hasHyphenAfter = part.hasHyphenAfter
                            addTokensInto(tokens, part.beginIndex, part.endIndex)
                        }
                )
            }

            fun addTokensInto(tokens: MutableList<Token>, beginIndex: Int, endIndex: Int) {
                for (begin in beginIndex..endIndex - 1) {
                    val ch = text[begin]
                    val isSpace = isSpace(ch)
                    val end = begin + 1
                    val last = tokens.lastOrNull()

                    if (last == null || last.isSpace != isSpace) {
                        tokens.add(Token().apply {
                            this.isSpace = isSpace
                            this.beginIndex = begin
                            this.endIndex = end
                        })
                    } else {
                        last.endIndex = end
                    }
                }
            }

            fun isSpace(ch: Char) = Character.isSpaceChar(ch) || Character.isWhitespace(ch)

            inner class LinePart {
                var beginIndex = 0
                var endIndex = 0
                var indent = 0F
                var hasHyphenAfter = false

                var left = 0F
                var width = 0F

                val right: Float get() = left + width
                val isEmpty: Boolean get() = endIndex == beginIndex

                fun reset(initialIndex: Int, indent: Float) {
                    this.beginIndex = initialIndex
                    this.endIndex = initialIndex
                    this.indent = indent
                    this.hasHyphenAfter = false
                    this.left = indent
                    this.width = 0F
                }

                fun setEnd(endIndex: Int, hasHyphenAfter: Boolean) {
                    if (isEmpty) {
                        val leftHang = config.leftHangFactor(text[beginIndex]) * measuredText.advanceOf(beginIndex)
                        left = indent - leftHang
                    }

                    this.endIndex = endIndex
                    this.hasHyphenAfter = hasHyphenAfter

                    val trailingSpacesBegin = trailingSpacesBegin(beginIndex, endIndex)
                    val symbolsWidth = measuredText.advanceOf(beginIndex, trailingSpacesBegin)
                    val hyphenWidth = if (hasHyphenAfter) measuredText.hyphenWidthAfter(endIndex - 1) else 0F
                    val rightHang = rightHang(beginIndex, trailingSpacesBegin, hyphenWidth, hasHyphenAfter)
                    width = max(0F, symbolsWidth + hyphenWidth - rightHang)
                }

                private fun trailingSpacesBegin(beginIndex: Int, endIndex: Int): Int {
                    var i = endIndex - 1
                    while (i >= beginIndex) {
                        if (!isSpace(text[i]))
                            return i + 1
                        i--
                    }
                    return beginIndex
                }

                private fun rightHang(beginIndex: Int, trailingSpacesBegin: Int, hyphenWidth: Float, hasHyphenAfter: Boolean): Float {
                    val lastIndex = trailingSpacesBegin - 1
                    if (lastIndex >= beginIndex) {
                        if (hasHyphenAfter) {
                            return config.rightHangFactor(LayoutChars.HYPHEN) * hyphenWidth
                        } else {
                            val lastChar = text[lastIndex]
                            return config.rightHangFactor(lastChar) * measuredText.advanceOf(lastIndex)
                        }
                    } else {
                        return 0F
                    }
                }
            }
        }.makeLines()
    }

    private object Reusables {
        val lines = ReusableArrayList<Line>()
    }
}