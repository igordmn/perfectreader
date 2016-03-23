package com.dmi.perfectreader.layout.paragraph

import com.carrotsearch.hppc.FloatArrayList
import com.dmi.perfectreader.layout.LayoutObject
import com.dmi.perfectreader.layout.LayoutParagraph
import com.dmi.perfectreader.layout.common.LayoutSpace
import com.dmi.perfectreader.layout.common.LayoutSpace.Area
import com.dmi.perfectreader.layout.common.Layouter
import com.dmi.perfectreader.layout.LayoutParagraph.Run
import com.dmi.perfectreader.layout.paragraph.liner.Liner
import com.dmi.perfectreader.render.*
import com.dmi.perfectreader.style.TextAlign
import com.dmi.util.cache.ReusableArrayList
import com.dmi.util.cache.ReusableFloatArrayList
import com.dmi.util.cache.ReusableIntArrayList
import com.dmi.util.cache.ReusableStringBuilder
import java.lang.Math.max
import java.util.*

class ParagraphLayouter(
        private val childrenLayouter: Layouter<LayoutObject, RenderObject>,
        private val textMetrics: TextMetrics,
        private val liner: Liner
) : Layouter<LayoutParagraph, RenderParagraph> {
    companion object {
        private val HYPHEN_STRING = LayoutChars.HYPHEN.toString()
    }

    override fun layout(obj: LayoutParagraph, space: LayoutSpace): RenderParagraph {
        val runs = obj.runs
        val locale = obj.locale
        val widthArea = space.width.area
        val maxWidth = when (widthArea) {
            is Area.WrapContent -> widthArea.max
            is Area.Fixed -> widthArea.value
        }

        val lineConfig = object : Liner.Config {
            override val firstLineIndent = obj.firstLineIndent
            override val maxWidth = maxWidth
            override fun leftHangFactor(ch: Char) = obj.hangingConfig.leftHangFactor(ch)
            override fun rightHangFactor(ch: Char) = obj.hangingConfig.rightHangFactor(ch)
        }

        return object {
            fun layout(): RenderParagraph {
                val text = PrerenderedText()
                val lines = liner.makeLines(text, lineConfig)
                val width = when (widthArea) {
                    is Area.WrapContent -> maxLineWidth(lines)
                    is Area.Fixed -> widthArea.value
                }
                return ParagraphBuilder(width).run {
                    for (i in 0..lines.size - 1) {
                        val line = lines[i]
                        val isLast = i == lines.size - 1
                        addLine(renderLine(text, line, width, isLast))
                    }
                    build()
                }
            }


            fun maxLineWidth(lines: List<Liner.Line>): Float {
                var width = 0F
                for (line in lines) {
                    if (line.right > width)
                        width = line.right
                }
                return width
            }

            fun renderLine(text: PrerenderedText, line: Liner.Line, width: Float, isLast: Boolean): RenderLine {
                val renderLine = LineBuilder()
                renderLine.reset(width)
                renderLine.addOffset(line.left)

                val freeSpace = width - line.right
                val canJustify = !isLast && obj.textAlign == TextAlign.JUSTIFY
                val midspaceScale = if (canJustify) computeMidspaceScale(text, line, freeSpace) else 1F

                if (obj.textAlign == TextAlign.RIGHT)
                    renderLine.addOffset(freeSpace)
                else if (obj.textAlign == TextAlign.CENTER)
                    renderLine.addOffset(freeSpace / 2)

                with (line.tokens) {
                    for (i in 0..size - 1) {
                        val spaceScaleX = if (i > 0 && i < size - 1) midspaceScale else 1F
                        text.render(this[i], spaceScaleX, renderLine)
                    }

                    if (line.hasHyphenAfter && size > 0)
                        text.renderHyphen(last().endIndex - 1, renderLine)
                }

                return renderLine.build()
            }

            /**
             * midspace - пробелы между словами (но не в конце или в начале строки).
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
                private val plainTextBuilder = Reusables.plainTextBuilder()
                private val plainIndexToRunIndex = Reusables.plainIndexToRunIndex()
                private val plainIndexToWidth = Reusables.plainIndexToWidth()
                private val plainIndexToTotalWidth = Reusables.plainIndexToTotalWidth()
                private val runIndexToPlainBeginIndex = Reusables.runIndexToPlainBeginIndex()
                private val runIndexToObject = Reusables.runIndexToObject()
                private val runIndexToHeight = Reusables.runIndexToHeight()
                private val runIndexToBaseline = Reusables.runIndexToBaseline()
                private val runIndexToHyphenWidth = Reusables.runIndexToHyphenWidth()

                private val childrenSpace = LayoutSpace(
                        LayoutSpace.Metric(
                                space.width.percentBase,
                                Area.WrapContent(maxWidth)
                        ),
                        LayoutSpace.Metric(
                                0F,
                                Area.WrapContent(Float.MAX_VALUE)
                        )
                )

                override lateinit var plainText: String
                override val locale: Locale = locale

                init {
                    for (r in 0..runs.size - 1) {
                        val run = runs[r]
                        when (run) {
                            is Run.Text -> prerenderTextRun(r, run)
                            is Run.Object -> prerenderObject(r, run)
                        }
                    }
                    plainText = plainTextBuilder.toString()
                }

                private fun prerenderTextRun(runIndex: Int, run: Run.Text) {
                    val text = run.text
                    val verticalMetrics = textMetrics.verticalMetrics(run.style)

                    plainTextBuilder.append(text)

                    val charWidths = textMetrics.charWidths(text, run.style)
                    for (i in 0..text.length - 1) {
                        plainIndexToRunIndex.add(runIndex)
                        addWidth(charWidths[i])
                    }

                    runIndexToPlainBeginIndex.add(plainTextBuilder.length - text.length)
                    runIndexToObject.add(null)
                    runIndexToHeight.add(-verticalMetrics.ascent + verticalMetrics.descent + verticalMetrics.leading)
                    runIndexToBaseline.add(-verticalMetrics.ascent)
                    runIndexToHyphenWidth.add(hyphenWidth(run))
                }

                private fun hyphenWidth(run: Run.Text): Float {
                    return textMetrics.charWidths(HYPHEN_STRING, run.style)[0]
                }

                private fun prerenderObject(runIndex: Int, run: Run.Object) {
                    val renderObj = childrenLayouter.layout(run.obj, childrenSpace)

                    plainTextBuilder.append(LayoutChars.OBJECT_REPLACEMENT_CHARACTER)
                    plainIndexToRunIndex.add(runIndex)
                    addWidth(renderObj.width)

                    runIndexToPlainBeginIndex.add(plainTextBuilder.length - 1)
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

                fun render(token: Liner.Token, spaceScaleX: Float, line: LineBuilder) {
                    forEachRun(token.beginIndex, token.endIndex) { begin, end, runIndex ->
                        if (token.isSpace) {
                            renderSpace(begin, end, runIndex, spaceScaleX, line)
                        } else {
                            renderRun(begin, end, runIndex, line)
                        }
                    }
                }

                private inline fun forEachRun(
                        beginIndex: Int, endIndex: Int,
                        action: (begin: Int, end: Int, runIndex: Int) -> Unit
                ) {
                    var begin = beginIndex
                    for (end in beginIndex + 1..endIndex) {
                        val runIndex = plainIndexToRunIndex[begin]
                        val isEndOfRun = end == endIndex || runIndex != plainIndexToRunIndex[end]
                        if (isEndOfRun) {
                            action(begin, end, runIndex)
                            begin = end
                        }
                    }
                }

                private fun renderSpace(beginIndex: Int, endIndex: Int, runIndex: Int, scaleX: Float, line: LineBuilder) {
                    val run = runs[runIndex] as Run.Text

                    val runBegin = runIndexToPlainBeginIndex[runIndex]
                    val baseline = runIndexToBaseline[runIndex]

                    line.addObject(
                            RenderSpace(
                                    width = widthOf(beginIndex, endIndex) * scaleX,
                                    height = runIndexToHeight[runIndex],
                                    text = run.text.subSequence(beginIndex - runBegin, endIndex - runBegin),
                                    locale = locale,
                                    baseline = baseline,
                                    style = run.style,
                                    scaleX = scaleX
                            ),
                            baseline
                    )
                }

                private fun renderRun(beginIndex: Int, endIndex: Int, runIndex: Int, line: LineBuilder) {
                    val run = runs[runIndex]
                    when (run) {
                        is Run.Text -> renderTextRun(beginIndex, endIndex, runIndex, run, line)
                        is Run.Object -> renderObjectRun(runIndex, line)
                    }
                }

                private fun renderTextRun(beginIndex: Int, endIndex: Int, runIndex: Int, run: Run.Text, line: LineBuilder) {
                    val runBegin = runIndexToPlainBeginIndex[runIndex]
                    val baseline = runIndexToBaseline[runIndex]

                    line.addObject(
                            RenderText(
                                    width = widthOf(beginIndex, endIndex),
                                    height = runIndexToHeight[runIndex],
                                    text = run.text.subSequence(beginIndex - runBegin, endIndex - runBegin),
                                    locale = locale,
                                    baseline = baseline,
                                    style = run.style
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

                fun renderHyphen(plainIndex: Int, line: LineBuilder) {
                    val runIndex = plainIndexToRunIndex[plainIndex]
                    val run = runs[runIndex]
                    if (run is Run.Text) {
                        val baseline = runIndexToBaseline[runIndex]

                        line.addObject(
                                RenderText(
                                        width = runIndexToHyphenWidth[runIndex],
                                        height = runIndexToHeight[runIndex],
                                        text = HYPHEN_STRING,
                                        locale = locale,
                                        baseline = baseline,
                                        style = run.style
                                ),
                                baseline
                        )
                    }
                }
            }
        }.layout()
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

    private class ParagraphBuilder(
            private val width: Float
    ) {
        private val children = ArrayList<RenderChild>()
        private var height = 0F

        fun addLine(line: RenderLine) {
            children.add(RenderChild(0F, height, line))
            height += line.height
        }

        fun build(): RenderParagraph = RenderParagraph(width, height, children)
    }

    private object Reusables {
        private val INITIAL_CHARS_CAPACITY = 4000
        private val INITIAL_RUNS_CAPACITY = 16

        val plainTextBuilder = ReusableStringBuilder(INITIAL_CHARS_CAPACITY)
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
