package com.dmi.perfectreader.layout.layouter

import com.carrotsearch.hppc.FloatArrayList
import com.dmi.perfectreader.layout.LayoutObject
import com.dmi.perfectreader.layout.LayoutParagraph
import com.dmi.perfectreader.layout.config.LayoutArea
import com.dmi.perfectreader.layout.config.LayoutChars
import com.dmi.perfectreader.layout.config.TextMetrics
import com.dmi.perfectreader.layout.liner.Liner
import com.dmi.perfectreader.layout.run.ObjectRun
import com.dmi.perfectreader.layout.run.TextRun
import com.dmi.perfectreader.render.*
import com.dmi.perfectreader.style.TextAlign
import com.dmi.util.cache.ReusableArrayList
import com.dmi.util.cache.ReusableFloatArrayList
import com.dmi.util.cache.ReusableIntArrayList
import com.dmi.util.cache.ReusableStringBuilder
import com.google.common.base.Preconditions.checkArgument
import java.lang.Math.max
import java.util.*

class ParagraphLayouter(
        private val childrenLayouter: Layouter<LayoutObject, RenderObject>,
        private val textMetrics: TextMetrics,
        private val liner: Liner
) : Layouter<LayoutParagraph, RenderParagraph> {
    companion object {
        private val HYPHEN_STRING = String(charArrayOf(LayoutChars.HYPHEN))
    }

    override fun layout(obj: LayoutParagraph, area: LayoutArea): RenderParagraph {
        var runs = obj.runs
        var locale = obj.locale

        with(object {
            fun lineConfig(): Liner.Config {
                val hangingConfig = obj.hangingConfig
                return object : Liner.Config {
                    override val firstLineIndent = obj.firstLineIndent
                    override val maxWidth = area.width
                    override fun leftHangFactor(ch: Char) = hangingConfig.leftHangFactor(ch)
                    override fun rightHangFactor(ch: Char) = hangingConfig.rightHangFactor(ch)
                }
            }

            fun computeWidth(lines: List<Liner.Line>): Float {
                var maxWidth = 0F
                for (line in lines) {
                    if (line.right > maxWidth)
                        maxWidth = line.right
                }
                return maxWidth
            }

            fun renderLine(text: PrerenderedText, line: Liner.Line, width: Float): RenderLine {
                val renderLine = LineBuilder()
                renderLine.reset(width)
                renderLine.addOffset(line.left)

                val freeSpace = width - line.right
                val canJustify = !line.isLast && obj.textAlign == TextAlign.JUSTIFY
                val midspaceScale = if (canJustify) computeMidspaceScale(text, line, freeSpace) else 1F

                if (obj.textAlign == TextAlign.RIGHT)
                    renderLine.addOffset(freeSpace)
                else if (obj.textAlign == TextAlign.CENTER)
                    renderLine.addOffset(freeSpace / 2)

                val tokens = line.tokens
                for (i in 0..tokens.size - 1) {
                    val token = tokens[i]
                    val hasHyphenAfter = line.hasHyphenAfter && i == tokens.size - 1
                    val isMidspace = token.isSpace && i > 0 && i < tokens.size - 1
                    val scaleX = if (isMidspace) midspaceScale else 1F
                    text.render(token, hasHyphenAfter, scaleX, renderLine)
                }

                return renderLine.build()
            }

            /**
             * midspace - пробелы между словами (но не в конце или в начале строке).
             * возвращает множитель, на который нужно умножить их ширину, чтобы строка стала выровненной по ширине параграфа
             */
            fun computeMidspaceScale(text: PrerenderedText, line: Liner.Line, freeSpace: Float): Float {
                val totalExpansion = max(0F, freeSpace)

                var totalMidspace = 0F
                val tokens = line.tokens
                for (i in 1..tokens.size - 2) {
                    val token = tokens[i]
                    if (token.isSpace)
                        totalMidspace += text.widthOf(token.beginIndex, token.endIndex)
                }

                return if (totalMidspace > 0) (totalMidspace + totalExpansion) / totalMidspace else 1F
            }

            inner class PrerenderedText : Liner.MeasuredText {
                private val plainIndexToRunIndex = Reusables.plainIndexToRunIndex()
                private val plainIndexToWidth = Reusables.plainIndexToWidth()
                private val plainIndexToTotalWidth = Reusables.plainIndexToTotalWidth()
                private val runIndexToPlainBeginIndex = Reusables.runIndexToPlainBeginIndex()
                private val runIndexToObject = Reusables.runIndexToObject()
                private val runIndexToHeight = Reusables.runIndexToHeight()
                private val runIndexToBaseline = Reusables.runIndexToBaseline()
                private val runIndexToHyphenWidth = Reusables.runIndexToHyphenWidth()

                private val childrenArea = LayoutArea(area.width, 0F)

                override val plainText = Reusables.plainText()
                override val locale: Locale = locale

                init {
                    for (r in 0..runs.size - 1) {
                        val run = runs[r]
                        when (run) {
                            is TextRun -> prerenderTextRun(r, run)
                            is ObjectRun -> prerenderObject(r, run)
                            else -> throw UnsupportedOperationException()
                        }
                    }
                }

                private fun prerenderTextRun(runIndex: Int, run: TextRun) {
                    val text = run.text
                    val verticalMetrics = textMetrics.verticalMetrics(run.style)

                    plainText.append(text)

                    val charWidths = textMetrics.charWidths(text, run.style)
                    for (i in 0..text.length - 1) {
                        plainIndexToRunIndex.add(runIndex)
                        addWidth(charWidths[i])
                    }

                    runIndexToPlainBeginIndex.add(plainText.length - text.length)
                    runIndexToObject.add(null)
                    runIndexToHeight.add(-verticalMetrics.ascent + verticalMetrics.descent + verticalMetrics.leading)
                    runIndexToBaseline.add(-verticalMetrics.ascent)
                    runIndexToHyphenWidth.add(hyphenWidth(run))
                }

                private fun hyphenWidth(run: TextRun): Float {
                    return textMetrics.charWidths(HYPHEN_STRING, run.style)[0]
                }

                private fun prerenderObject(runIndex: Int, run: ObjectRun) {
                    val renderObj = childrenLayouter.layout(run.obj(), childrenArea)

                    plainText.append(LayoutChars.OBJECT_REPLACEMENT_CHARACTER)
                    plainIndexToRunIndex.add(runIndex)
                    addWidth(renderObj.width)

                    runIndexToPlainBeginIndex.add(plainText.length - 1)
                    runIndexToObject.add(renderObj)
                    runIndexToHeight.add(renderObj.height)
                    runIndexToBaseline.add(renderObj.height)
                    runIndexToHyphenWidth.add(0F)
                }

                private fun addWidth(width: Float) {
                    val size = plainIndexToTotalWidth.size()
                    val currentWidth = if (size > 0) plainIndexToTotalWidth[size - 1] else 0F
                    plainIndexToWidth.add(width)
                    plainIndexToTotalWidth.add(currentWidth + width)
                }

                override fun widthOf(index: Int) = plainIndexToWidth[index]

                override fun widthOf(beginIndex: Int, endIndex: Int): Float {
                    val widthToBegin = if (beginIndex > 0) plainIndexToTotalWidth[beginIndex - 1] else 0F
                    val widthToEnd = if (endIndex > 0) plainIndexToTotalWidth[endIndex - 1] else 0F
                    return widthToEnd - widthToBegin
                }

                override fun hyphenWidthAfter(index: Int): Float {
                    val runIndex = plainIndexToRunIndex[index]
                    return runIndexToHyphenWidth[runIndex]
                }

                fun render(token: Liner.Token, hasHyphenAfter: Boolean, scaleX: Float, line: LineBuilder) {
                    with(token) {
                        checkArgument(beginIndex < plainText.length && beginIndex < endIndex)

                        renderRuns(beginIndex, endIndex, isSpace, scaleX, line)
                        if (hasHyphenAfter)
                            renderHyphen(endIndex - 1, line)
                    }
                }

                private fun renderRuns(beginIndex: Int, endIndex: Int, isSpace: Boolean, scaleX: Float, line: LineBuilder) {
                    var begin = beginIndex
                    for (end in beginIndex + 1..endIndex) {
                        val runIndex = plainIndexToRunIndex[begin]
                        val isEndOfRun = end == endIndex || runIndex != plainIndexToRunIndex[end]
                        if (isEndOfRun) {
                            renderRun(begin, end, runIndex, isSpace, scaleX, line)
                            begin = end
                        }
                    }
                }

                private fun renderRun(beginIndex: Int, endIndex: Int, runIndex: Int, isSpace: Boolean, scaleX: Float, line: LineBuilder) {
                    val run = runs[runIndex]
                    when {
                        isSpace -> renderSpace(beginIndex, endIndex, runIndex, run as TextRun, scaleX, line)
                        run is TextRun -> renderTextRun(beginIndex, endIndex, runIndex, run, line)
                        run is ObjectRun -> renderObjectRun(runIndex, line)
                        else -> throw UnsupportedOperationException()
                    }
                }

                private fun renderSpace(beginIndex: Int, endIndex: Int, runIndex: Int, run: TextRun, scaleX: Float, line: LineBuilder) {
                    val runBegin = runIndexToPlainBeginIndex[runIndex]
                    val baseline = runIndexToBaseline[runIndex]

                    line.addObject(
                            RenderSpace(
                                    widthOf(beginIndex, endIndex) * scaleX,
                                    runIndexToHeight[runIndex],
                                    run.text.subSequence(beginIndex - runBegin, endIndex - runBegin),
                                    locale,
                                    baseline,
                                    run.style,
                                    scaleX
                            ),
                            baseline
                    )
                }

                private fun renderTextRun(beginIndex: Int, endIndex: Int, runIndex: Int, run: TextRun, line: LineBuilder) {
                    val runBegin = runIndexToPlainBeginIndex[runIndex]
                    val baseline = runIndexToBaseline[runIndex]

                    line.addObject(
                            RenderText(
                                    widthOf(beginIndex, endIndex),
                                    runIndexToHeight[runIndex],
                                    run.text.subSequence(beginIndex - runBegin, endIndex - runBegin),
                                    locale,
                                    baseline,
                                    run.style
                            ),
                            baseline
                    )
                }

                private fun renderObjectRun(runIndex: Int, line: LineBuilder) {
                    line.addObject(
                            runIndexToObject[runIndex]!!,
                            runIndexToBaseline[runIndex]
                    )
                }

                private fun renderHyphen(plainIndex: Int, line: LineBuilder) {
                    val runIndex = plainIndexToRunIndex[plainIndex]
                    val run = runs[runIndex]
                    if (run is TextRun) {
                        val baseline = runIndexToBaseline[runIndex]

                        line.addObject(
                                RenderText(
                                        runIndexToHyphenWidth[runIndex],
                                        runIndexToHeight[runIndex],
                                        HYPHEN_STRING,
                                        locale,
                                        baseline,
                                        run.style
                                ),
                                baseline
                        )
                    }
                }
            }
        }) {
            val text = PrerenderedText()
            val lines = liner.makeLines(text, lineConfig())
            val width = if (obj.fitAreaWidth) area.width else computeWidth(lines)

            return ParagraphBuilder().run {
                reset(width)
                for (line in lines) {
                    addLine(renderLine(text, line, width))
                }
                build()
            }
        }
    }

    private class LineBuilder {
        private var width = 0F
        private val objects = ArrayList<RenderObject>(32)
        private val baselines = FloatArrayList(32)
        private val lefts = FloatArrayList(32)
        private var offset = 0F

        fun reset(width: Float) {
            this.width = width
            objects.clear()
            baselines.elementsCount = 0
            lefts.elementsCount = 0
            offset = 0F
        }

        fun addOffset(offset: Float) {
            this.offset += offset
        }

        fun addObject(obj: RenderObject, baseline: Float) {
            objects.add(obj)
            baselines.add(baseline)
            lefts.add(offset)
            offset += obj.width
        }

        fun build(): RenderLine {
            val children = ArrayList<RenderChild>()

            var lineBaseline = 0F
            for (i in 0..objects.size - 1) {
                val baseline = baselines[i]
                if (baseline > lineBaseline) {
                    lineBaseline = baseline
                }
            }

            var lineHeight = 0F
            for (i in 0..objects.size - 1) {
                val x = lefts[i]
                val y = lineBaseline - baselines[i]
                val obj = objects[i]
                if (y + obj.height > lineHeight) {
                    lineHeight = y + obj.height
                }
                children.add(RenderChild(x, y, obj))
            }

            return RenderLine(width, lineHeight, children)
        }
    }

    private class ParagraphBuilder {
        private val children = ArrayList<RenderChild>()
        private var width = 0F
        private var height = 0F

        fun reset(width: Float) {
            this.width = width
        }

        fun addLine(line: RenderLine) {
            children.add(RenderChild(0F, height, line))
            height += line.height
        }

        fun build(): RenderParagraph = RenderParagraph(width, height, children)
    }

    private object Reusables {
        private val INITIAL_CHARS_CAPACITY = 4000
        private val INITIAL_RUNS_CAPACITY = 16

        val plainText = ReusableStringBuilder(INITIAL_CHARS_CAPACITY)
        val plainIndexToRunIndex = ReusableIntArrayList(INITIAL_CHARS_CAPACITY)
        val plainIndexToWidth = ReusableFloatArrayList(INITIAL_CHARS_CAPACITY)
        val plainIndexToTotalWidth = ReusableFloatArrayList(INITIAL_CHARS_CAPACITY)
        val runIndexToPlainBeginIndex = ReusableIntArrayList(INITIAL_RUNS_CAPACITY)
        val runIndexToObject = ReusableArrayList<RenderObject?>(INITIAL_RUNS_CAPACITY)
        val runIndexToHeight = ReusableFloatArrayList(INITIAL_RUNS_CAPACITY)
        val runIndexToBaseline = ReusableFloatArrayList(INITIAL_RUNS_CAPACITY)
        val runIndexToHyphenWidth = ReusableFloatArrayList(INITIAL_RUNS_CAPACITY)
    }
}
