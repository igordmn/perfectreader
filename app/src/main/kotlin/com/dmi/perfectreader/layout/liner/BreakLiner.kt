package com.dmi.perfectreader.layout.liner

import com.dmi.perfectreader.layout.config.LayoutChars
import com.dmi.perfectreader.layout.liner.Liner.Line
import com.dmi.perfectreader.layout.liner.Liner.Token
import com.dmi.util.cache.ReusableArrayList
import com.google.common.base.Preconditions.checkState
import java.lang.Math.max

class BreakLiner(private val breakFinder: BreakFinder) : Liner {
    override fun makeLines(measuredText: Liner.MeasuredText, config: Liner.Config): List<Liner.Line> {
        return object {
            val lines = Reusables.lines()
            val text = measuredText.plainText
            val locale = measuredText.locale

            var part = LinePart()             // содержит символы, которые уже точно будет содержать строка
            var newPart = LinePart()          // содержит символы из part, а также новые добавочные. необходима для проверки, вмещается ли новая строка

            fun makeLines(): List<Liner.Line> {
                part.reset(0, 0, config.firstLineIndent, false)

                breakFinder.findBreaks(text, locale) { br ->
                    checkState(part.endIndex < br.index, "Wrong line end index")
                    pushChars(br.index, br.hasHyphen)
                    checkState(part.endIndex == br.index, "Wrong line end index")
                }

                pushLine()

                return lines
            }

            fun pushChars(endIndex: Int, hasHyphenAfter: Boolean) {
                newPart.reset(part.beginIndex, endIndex, part.indent, hasHyphenAfter)

                if (canUsePart(newPart)) {
                    useNewPart()
                } else {
                    pushLine()
                    pushCharsAtBeginOfLine(endIndex, hasHyphenAfter)
                }
            }

            fun pushCharsAtBeginOfLine(endIndex: Int, hasHyphenAfter: Boolean) {
                newPart.reset(part.beginIndex, endIndex, part.indent, hasHyphenAfter)

                if (canUsePart(newPart)) {
                    useNewPart()
                } else {
                    pushCharByChar(endIndex, hasHyphenAfter)
                }
            }

            fun pushCharByChar(endIndex: Int, hasHyphenAfter: Boolean) {
                for (end in part.endIndex + 1..endIndex) {
                    val charHasHyphenAfter = end == endIndex && hasHyphenAfter
                    newPart.reset(part.beginIndex, end, part.indent, charHasHyphenAfter)

                    if (!canUsePart(newPart)) {
                        pushLine()
                        newPart.reset(part.beginIndex, end, part.indent, charHasHyphenAfter)
                    }

                    useNewPart()
                }
            }

            fun canUsePart(newPart: LinePart) = newPart.right <= config.maxWidth || newPart.right == part.right

            fun useNewPart() {
                val temp = part
                part = newPart
                newPart = temp
            }

            fun pushLine() {
                if (!part.isEmpty) {
                    lines.add(
                            Line().apply {
                                this.left = part.left
                                this.width = part.width
                                this.hasHyphenAfter = part.hasHyphenAfter
                                addTokensInto(tokens, part.beginIndex, part.endIndex)
                            }
                    )
                    part.reset(part.endIndex, part.endIndex, 0F, false)
                }
            }

            fun addTokensInto(tokens: MutableList<Liner.Token>, beginIndex: Int, endIndex: Int) {
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

                val right: Float
                    get() = left + width
                val isEmpty: Boolean
                    get() = endIndex == beginIndex

                fun reset(beginIndex: Int, endIndex: Int, indent: Float, hasHyphenAfter: Boolean) {
                    this.beginIndex = beginIndex
                    this.endIndex = endIndex
                    this.indent = indent
                    this.hasHyphenAfter = hasHyphenAfter

                    if (endIndex > beginIndex) {
                        val trailingSpacesBegin = trailingSpacesBegin(beginIndex, endIndex)
                        val symbolsWidth = measuredText.widthOf(beginIndex, trailingSpacesBegin)
                        val hyphenWidth = if (hasHyphenAfter) measuredText.hyphenWidthAfter(endIndex - 1) else 0F

                        val leftHang = config.leftHangFactor(text[beginIndex]) * measuredText.widthOf(beginIndex)
                        val rightHang = rightHang(beginIndex, trailingSpacesBegin, hyphenWidth, hasHyphenAfter)

                        left = indent - leftHang
                        width = max(0F, symbolsWidth + hyphenWidth - rightHang)
                    } else {
                        left = 0F
                        width = 0F
                    }
                }

                private fun trailingSpacesBegin(beginIndex: Int, endIndex: Int): Int {
                    var i = endIndex - 1
                    while (i >= beginIndex) {
                        val ch = text[i]
                        if (!isSpace(ch))
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
                            return config.rightHangFactor(lastChar) * measuredText.widthOf(lastIndex)
                        }
                    } else {
                        return 0F
                    }
                }
            }
        }.makeLines()
    }

    private object Reusables {
        val lines = ReusableArrayList<Liner.Line>()
    }
}
