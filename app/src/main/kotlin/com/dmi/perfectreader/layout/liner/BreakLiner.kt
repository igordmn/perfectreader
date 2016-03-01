package com.dmi.perfectreader.layout.liner

import com.dmi.perfectreader.layout.config.LayoutChars
import com.dmi.perfectreader.layout.liner.Liner.Line
import com.dmi.perfectreader.layout.liner.Liner.Token
import com.dmi.util.cache.ReusableArrayList
import com.google.common.base.Preconditions.checkState
import java.lang.Math.max

class BreakLiner(private val breakFinder: BreakFinder) : Liner {

    override fun makeLines(measuredText: Liner.MeasuredText, config: Liner.Config): List<Liner.Line> {
        val lines = Reusables.lines()

        val text = measuredText.plainText
        val locale = measuredText.locale

        object : Runnable {
            var part = LinePart()             // содержит символы, которые уже точно будет содержать строка
            var newPart = LinePart()          // содержит символы из part, а также новые добавочные. необходима для проверки, вмещается ли новая строка

            override fun run() {
                part.reset(0, 0, config.firstLineIndent, false)

                breakFinder.findBreaks(text, locale, { br ->
                    checkState(part.endIndex < br.index, "Wrong line end index")

                    pushChars(br.index, br.hasHyphen)

                    if (br.isForce) {
                        pushLine(true)
                    }

                    checkState(part.endIndex == br.index, "Wrong line end index")
                })

                pushLine(true)
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

            private fun pushCharsAtBeginOfLine(endIndex: Int, hasHyphenAfter: Boolean) {
                newPart.reset(part.beginIndex, endIndex, part.indent, hasHyphenAfter)

                if (canUsePart(newPart)) {
                    useNewPart()
                } else {
                    pushCharByChar(endIndex, hasHyphenAfter)
                }
            }

            private fun pushCharByChar(endIndex: Int, hasHyphenAfter: Boolean) {
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

            private fun canUsePart(newPart: LinePart): Boolean {
                return newPart.left + newPart.width <= config.maxWidth || newPart.left + newPart.width == part.left + part.width
            }

            private fun useNewPart() {
                val temp = part
                part = newPart
                newPart = temp
            }

            fun pushLine(isLast: Boolean = false) {
                if (!part.isEmpty) {
                    val line = Line()
                    line.left = part.left
                    line.width = part.width
                    line.hasHyphenAfter = part.hasHyphenAfter
                    line.isLast = isLast
                    addTokensInto(line.tokens, part.beginIndex, part.endIndex)
                    lines.add(line)

                    part.reset(part.endIndex, part.endIndex, 0f, false)
                }
            }

            private fun addTokensInto(tokens: MutableList<Liner.Token>, beginIndex: Int, endIndex: Int) {
                var token: Token? = null
                for (begin in beginIndex..endIndex - 1) {
                    val ch = text[begin]
                    val isSpace = isSpace(ch)
                    val end = begin + 1

                    if (token == null || token.isSpace != isSpace) {
                        token = Token()
                        token.isSpace = isSpace
                        token.beginIndex = begin
                        token.endIndex = end
                        tokens.add(token)
                    } else {
                        token.endIndex = end
                    }
                }
            }

            private fun isSpace(ch: Char): Boolean {
                return Character.isSpaceChar(ch) || Character.isWhitespace(ch)
            }

            inner class LinePart {
                var beginIndex: Int = 0
                var endIndex: Int = 0
                var indent = 0F
                var hasHyphenAfter: Boolean = false

                var left = 0F
                var width = 0F

                fun reset(beginIndex: Int, endIndex: Int, indent: Float, hasHyphenAfter: Boolean) {
                    this.beginIndex = beginIndex
                    this.endIndex = endIndex
                    this.indent = indent
                    this.hasHyphenAfter = hasHyphenAfter

                    if (endIndex > beginIndex) {
                        val trailingSpacesBeginIndex = findTrailingSpacesBeginIndex(beginIndex, endIndex)
                        val symbolsWidth = measuredText.widthOf(beginIndex, trailingSpacesBeginIndex)
                        val hyphenWidth = if (hasHyphenAfter) measuredText.hyphenWidthAfter(endIndex - 1) else 0F

                        val leftHang = config.leftHangFactor(text[beginIndex]) * measuredText.widthOf(beginIndex)
                        val rightHang = rightHang(beginIndex, trailingSpacesBeginIndex, hyphenWidth, hasHyphenAfter)

                        left = indent - leftHang
                        width = max(0f, symbolsWidth + hyphenWidth - rightHang)
                    } else {
                        left = 0f
                        width = 0f
                    }
                }

                private fun rightHang(beginIndex: Int, trailingSpacesBeginIndex: Int, hyphenWidth: Float, hasHyphenAfter: Boolean): Float {
                    val lastIndex = trailingSpacesBeginIndex - 1
                    if (lastIndex >= beginIndex) {
                        val lastChar = text[lastIndex]
                        return if (hasHyphenAfter)
                            config.rightHangFactor(LayoutChars.HYPHEN) * hyphenWidth
                        else
                            config.rightHangFactor(lastChar) * measuredText.widthOf(lastIndex)
                    } else {
                        return 0f
                    }
                }

                private fun findTrailingSpacesBeginIndex(beginIndex: Int, endIndex: Int): Int {
                    var i = endIndex - 1
                    while (i >= beginIndex) {
                        val ch = text[i]
                        if (!isSpace(ch)) {
                            return i + 1
                        }
                        i--
                    }
                    return beginIndex
                }

                val isEmpty: Boolean
                    get() = endIndex == beginIndex
            }
        }.run()

        return lines
    }

    private object Reusables {
        val lines = ReusableArrayList<Liner.Line>()
    }
}
