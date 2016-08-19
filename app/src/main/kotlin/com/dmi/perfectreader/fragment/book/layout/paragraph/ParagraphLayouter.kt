package com.dmi.perfectreader.fragment.book.layout.paragraph

import com.carrotsearch.hppc.FloatArrayList
import com.dmi.perfectreader.fragment.book.content.obj.ConfiguredObject
import com.dmi.perfectreader.fragment.book.content.obj.ConfiguredParagraph
import com.dmi.perfectreader.fragment.book.content.obj.ConfiguredParagraph.Run
import com.dmi.perfectreader.fragment.book.content.obj.param.TextAlign
import com.dmi.perfectreader.fragment.book.layout.ObjectLayouter
import com.dmi.perfectreader.fragment.book.layout.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.layout.common.LayoutSpace.Area
import com.dmi.perfectreader.fragment.book.layout.obj.*
import com.dmi.perfectreader.fragment.book.layout.paragraph.liner.Liner
import com.dmi.perfectreader.fragment.book.layout.paragraph.metrics.TextMetrics
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.util.lang.ReusableArrayList
import com.dmi.util.lang.ReusableFloatArrayList
import com.dmi.util.lang.ReusableIntArrayList
import com.dmi.util.lang.ReusableStringBuilder
import com.dmi.util.text.Chars
import java.lang.Math.max
import java.util.*

class ParagraphLayouter(
        private val childrenLayouter: ObjectLayouter<ConfiguredObject, LayoutObject>,
        private val textMetrics: TextMetrics,
        private val liner: Liner
) : ObjectLayouter<ConfiguredParagraph, LayoutParagraph> {
    companion object {
        private val HYPHEN_STRING = Chars.HYPHEN.toString()
    }

    override fun layout(obj: ConfiguredParagraph, space: LayoutSpace): LayoutParagraph {
        val runs = obj.runs
        val locale = obj.locale
        val widthArea = space.width.area
        val heightArea = space.width.area
        val maxWidth = when (widthArea) {
            is Area.WrapContent -> widthArea.max
            is Area.Fixed -> widthArea.value
        }
        val maxHeight = when (heightArea) {
            is Area.WrapContent -> heightArea.max
            is Area.Fixed -> heightArea.value
        }

        val lineConfig = object : Liner.Config {
            override val firstLineIndent = obj.firstLineIndent
            override val maxWidth = maxWidth
            override val hyphenation = obj.hyphenation
            override fun leftHangFactor(ch: Char) = obj.hangingConfig.leftHangFactor(ch)
            override fun rightHangFactor(ch: Char) = obj.hangingConfig.rightHangFactor(ch)
        }

        return object {
            fun layout(): LayoutParagraph {
                val text = PrelayoutedText()
                val lines = liner.makeLines(text, lineConfig)
                val width = when (widthArea) {
                    is Area.WrapContent -> maxLineWidth(lines)
                    is Area.Fixed -> widthArea.value
                }
                return ParagraphBuilder(width, obj).run {
                    lines.forEachIndexed { i, line ->
                        val isLast = i == lines.size - 1
                        addLine(layoutLine(text, line, width, isLast))
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

            fun layoutLine(text: PrelayoutedText, line: Liner.Line, width: Float, isLast: Boolean): LayoutLine {
                val layoutLine = LineBuilder()
                layoutLine.reset(width)
                layoutLine.addOffset(line.left)

                val freeSpace = width - line.right
                val canJustify = !isLast && obj.textAlign == TextAlign.JUSTIFY
                val midspaceScale = if (canJustify) computeMidspaceScale(text, line, freeSpace) else 1F

                if (obj.textAlign == TextAlign.RIGHT)
                    layoutLine.addOffset(freeSpace)
                else if (obj.textAlign == TextAlign.CENTER)
                    layoutLine.addOffset(freeSpace / 2)

                with (line.tokens) {
                    require(size > 0)

                    for (i in 0..size - 1) {
                        val spaceScaleX = if (i > 0 && i < size - 1) midspaceScale else 1F
                        text.layout(this[i], spaceScaleX, layoutLine)
                    }

                    if (line.hasHyphenAfter)
                        text.layoutHyphenAfter(last().endIndex - 1, layoutLine)
                }

                return layoutLine.build()
            }

            /**
             * midspace - пробелы между словами (но не в конце или в начале строки).
             * возвращает множитель, на который нужно умножить их ширину, чтобы строка стала выровненной по ширине параграфа
             */
            fun computeMidspaceScale(text: PrelayoutedText, line: Liner.Line, freeSpace: Float): Float {
                val totalExpansion = max(0F, freeSpace)

                var totalMidspace = 0F
                val tokens = line.tokens
                for (i in 1..tokens.size - 2) {
                    val token = tokens[i]
                    if (token.isSpace)
                        totalMidspace += text.advanceOf(token.beginIndex, token.endIndex)
                }

                return if (totalMidspace > 0) (totalMidspace + totalExpansion) / totalMidspace else 1F
            }

            inner class PrelayoutedText : Liner.MeasuredText {
                private val plainTextBuilder = Reusables.plainTextBuilder()
                private val plainIndexToRunIndex = Reusables.plainIndexToRunIndex()
                private val plainIndexToAdvance = Reusables.plainIndexToWidth()
                private val plainIndexToRight = Reusables.plainIndexToTotalWidth()
                private val runIndexToPlainBeginIndex = Reusables.runIndexToPlainBeginIndex()
                private val runIndexToObject = Reusables.runIndexToObject()
                private val runIndexToHeight = Reusables.runIndexToHeight()
                private val runIndexToBaseline = Reusables.runIndexToBaseline()
                private val runIndexToLeading = Reusables.runIndexToLeading()
                private val runIndexToHyphenWidth = Reusables.runIndexToHyphenWidth()

                private val childrenSpace = LayoutSpace(
                        LayoutSpace.Dimension(
                                space.width.percentBase,
                                Area.WrapContent(maxWidth)
                        ),
                        LayoutSpace.Dimension(
                                0F,
                                Area.WrapContent(maxHeight)
                        )
                )

                override lateinit var plainText: String
                override val locale: Locale = locale

                init {
                    runs.forEachIndexed { r, run ->
                        when (run) {
                            is Run.Text -> prelayoutTextRun(r, run)
                            is Run.Object -> prelayoutObject(r, run)
                        }
                    }
                    plainText = plainTextBuilder.toString()
                }

                private fun prelayoutTextRun(runIndex: Int, run: Run.Text) {
                    val text = run.text
                    val verticalMetrics = textMetrics.verticalMetrics(run.style)

                    plainTextBuilder.append(text)

                    val charAdvances = textMetrics.charAdvances(text, run.style)
                    for (i in 0..text.length - 1) {
                        plainIndexToRunIndex.add(runIndex)
                        addAdvance(
                                if (isSpaceWordSeparator(text[i])) {
                                    charAdvances[i] * run.style.wordSpacingMultiplier
                                } else {
                                    charAdvances[i]
                                }
                        )
                    }

                    runIndexToPlainBeginIndex.add(plainTextBuilder.length - text.length)
                    runIndexToObject.add(null)
                    runIndexToHeight.add(-verticalMetrics.ascent + verticalMetrics.descent)
                    runIndexToBaseline.add(-verticalMetrics.ascent)
                    runIndexToLeading.add(verticalMetrics.leading)
                    runIndexToHyphenWidth.add(hyphenWidth(run))
                }

                private fun hyphenWidth(run: Run.Text): Float {
                    return textMetrics.charAdvances(HYPHEN_STRING, run.style)[0]
                }

                private fun prelayoutObject(runIndex: Int, run: Run.Object) {
                    val layoutObj = childrenLayouter.layout(run.obj, childrenSpace)

                    plainTextBuilder.append(Chars.OBJECT_REPLACEMENT)
                    plainIndexToRunIndex.add(runIndex)
                    addAdvance(layoutObj.width)

                    runIndexToPlainBeginIndex.add(plainTextBuilder.length - 1)
                    runIndexToObject.add(layoutObj)
                    runIndexToHeight.add(layoutObj.height)
                    runIndexToBaseline.add(layoutObj.height)
                    runIndexToLeading.add(0F)
                    runIndexToHyphenWidth.add(0F)
                }

                private fun addAdvance(advance: Float) {
                    val size = plainIndexToRight.size()
                    val currentWidth = if (size > 0) plainIndexToRight[size - 1] else 0F
                    plainIndexToAdvance.add(advance)
                    plainIndexToRight.add(currentWidth + advance)
                }

                override fun advanceOf(index: Int) = plainIndexToAdvance[index]

                override fun advanceOf(beginIndex: Int, endIndex: Int): Float {
                    val advanceToBegin = if (beginIndex > 0) plainIndexToRight[beginIndex - 1] else 0F
                    val advanceToEnd = if (endIndex > 0) plainIndexToRight[endIndex - 1] else 0F
                    return advanceToEnd - advanceToBegin
                }

                override fun hyphenWidthAfter(index: Int): Float {
                    val runIndex = plainIndexToRunIndex[index]
                    return runIndexToHyphenWidth[runIndex]
                }

                fun layout(token: Liner.Token, spaceScaleX: Float, line: LineBuilder) {
                    forEachRun(token.beginIndex, token.endIndex) { begin, end, runIndex ->
                        if (token.isSpace) {
                            layoutSpace(begin, end, runIndex, spaceScaleX, line)
                        } else {
                            layoutRun(begin, end, runIndex, line)
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

                private fun layoutSpace(beginIndex: Int, endIndex: Int, runIndex: Int, scaleX: Float, line: LineBuilder) {
                    val run = runs[runIndex] as Run.Text

                    val plainBeginOfRun = runIndexToPlainBeginIndex[runIndex]
                    val baseline = runIndexToBaseline[runIndex]
                    val leading = runIndexToLeading[runIndex]
                    val beginOfRunText = beginIndex - plainBeginOfRun
                    val endOfRunText = endIndex - plainBeginOfRun

                    line.addObject(
                            LayoutSpaceText(
                                    width = advanceOf(beginIndex, endIndex) * scaleX,
                                    height = runIndexToHeight[runIndex],
                                    text = run.text.subSequence(beginOfRunText, endOfRunText),
                                    locale = locale,
                                    baseline = baseline,
                                    charOffsets = computeCharOffsets(beginIndex, endIndex, scaleX),
                                    style = run.style,
                                    range = run.charRange(beginOfRunText, endOfRunText)
                            ),
                            baseline,
                            leading,
                            run.lineHeightMultiplier
                    )
                }

                private fun layoutRun(beginIndex: Int, endIndex: Int, runIndex: Int, line: LineBuilder) {
                    val run = runs[runIndex]
                    when (run) {
                        is Run.Text -> layoutTextRun(beginIndex, endIndex, runIndex, run, line)
                        is Run.Object -> layoutObjectRun(runIndex, line)
                    }
                }

                private fun layoutTextRun(beginIndex: Int, endIndex: Int, runIndex: Int, run: Run.Text, line: LineBuilder) {
                    val plainBeginOfRun = runIndexToPlainBeginIndex[runIndex]
                    val baseline = runIndexToBaseline[runIndex]
                    val leading = runIndexToLeading[runIndex]
                    val beginOfRunText = beginIndex - plainBeginOfRun
                    val endOfRunText = endIndex - plainBeginOfRun

                    line.addObject(
                            LayoutText(
                                    width = advanceOf(beginIndex, endIndex),
                                    height = runIndexToHeight[runIndex],
                                    text = run.text.subSequence(beginOfRunText, endOfRunText),
                                    locale = locale,
                                    baseline = baseline,
                                    charOffsets = computeCharOffsets(beginIndex, endIndex, 1F),
                                    style = run.style,
                                    range = run.charRange(beginOfRunText, endOfRunText)
                            ),
                            baseline,
                            leading,
                            run.lineHeightMultiplier
                    )
                }

                private fun layoutObjectRun(runIndex: Int, line: LineBuilder) {
                    val run = runs[runIndex]
                    line.addObject(
                            runIndexToObject[runIndex]!!,
                            runIndexToBaseline[runIndex],
                            runIndexToLeading[runIndex],
                            run.lineHeightMultiplier
                    )
                }

                fun layoutHyphenAfter(plainIndex: Int, line: LineBuilder) {
                    val runIndex = plainIndexToRunIndex[plainIndex]
                    val run = runs[runIndex]
                    val plainBeginOfRun = runIndexToPlainBeginIndex[runIndex]
                    val indexOfHyphen = (plainIndex - plainBeginOfRun) + 1
                    if (run is Run.Text) {
                        val baseline = runIndexToBaseline[runIndex]
                        val leading = runIndexToLeading[runIndex]

                        line.addObject(
                                LayoutText(
                                        width = runIndexToHyphenWidth[runIndex],
                                        height = runIndexToHeight[runIndex],
                                        text = HYPHEN_STRING,
                                        locale = locale,
                                        baseline = baseline,
                                        charOffsets = floatArrayOf(0F),
                                        style = run.style,
                                        range = run.charRange(indexOfHyphen, indexOfHyphen)
                                ),
                                baseline,
                                leading,
                                run.lineHeightMultiplier
                        )
                    }
                }

                private fun computeCharOffsets(begin: Int, end: Int, scaleX: Float) =
                        FloatArray(end - begin) { i ->
                            advanceOf(begin, begin + i) * scaleX
                        }
            }
        }.layout()
    }

    /**
     * См. https://drafts.csswg.org/css-text-3/#word-separator
     * Поддерживаем только пробелы, т.к. они легко поддаются растягиванию
     */
    private fun isSpaceWordSeparator(ch: Char) = ch == '\u0020' || ch == '\u00A0'

    private class LineBuilder {
        private var width = 0F
        private val objects = ArrayList<LayoutObject>(32)
        private val baselines = FloatArrayList(32)
        private val leadings = FloatArrayList(32)
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

        fun addObject(obj: LayoutObject, baseline: Float, leading: Float, lineHeightMultiplier: Float) {
            objects.add(obj)
            baselines.add(baseline)
            leadings.add(max(-obj.height, (obj.height + leading) * lineHeightMultiplier - obj.height))
            lefts.add(offset)
            offset += obj.width
        }

        fun build(): LayoutLine {
            val children = ArrayList<LayoutChild>()

            var lineBaseline = leadings[0] / 2 + baselines[0]
            for (i in 1..objects.size - 1) {
                val baseline = leadings[i] / 2 + baselines[i]
                if (baseline > lineBaseline)
                    lineBaseline = baseline
            }

            for (i in 0..objects.size - 1) {
                val x = lefts[i]
                val y = lineBaseline - baselines[i]
                val obj = objects[i]
                children.add(LayoutChild(x, y, obj))
            }

            var lineHeight = children[0].y + children[0].obj.height + leadings[0] / 2
            for (i in 1..objects.size - 1) {
                val objLineHeight = children[i].y + children[i].obj.height + leadings[i] / 2
                if (objLineHeight > lineHeight)
                    lineHeight = objLineHeight
            }

            var minFactLeading = lineHeight - objects[0].height
            for (i in 1..objects.size - 1) {
                val factLeading = lineHeight - objects[i].height
                if (factLeading < minFactLeading)
                    minFactLeading = factLeading
            }

            val range = LocationRange(
                    objects.first().range.begin,
                    objects.last().range.end
            )

            return LayoutLine(width, lineHeight, max(0F, minFactLeading / 2), children, range)
        }
    }

    private class ParagraphBuilder(
            private val width: Float,
            private val obj: ConfiguredParagraph
    ) {
        private val children = ArrayList<LayoutChild>()
        private var height = 0F

        fun addLine(line: LayoutLine) {
            children.add(LayoutChild(0F, height, line))
            height += line.height
        }

        fun build() = LayoutParagraph(width, height, children, obj.range)
    }

    private object Reusables {
        private val INITIAL_CHARS_CAPACITY = 4000
        private val INITIAL_RUNS_CAPACITY = 16

        val plainTextBuilder = ReusableStringBuilder(INITIAL_CHARS_CAPACITY)
        val plainIndexToRunIndex = ReusableIntArrayList(INITIAL_CHARS_CAPACITY)
        val plainIndexToWidth = ReusableFloatArrayList(INITIAL_CHARS_CAPACITY)
        val plainIndexToTotalWidth = ReusableFloatArrayList(INITIAL_CHARS_CAPACITY)
        val runIndexToPlainBeginIndex = ReusableIntArrayList(INITIAL_RUNS_CAPACITY)
        val runIndexToObject = ReusableArrayList<LayoutObject?>(INITIAL_RUNS_CAPACITY)
        val runIndexToHeight = ReusableFloatArrayList(INITIAL_RUNS_CAPACITY)
        val runIndexToBaseline = ReusableFloatArrayList(INITIAL_RUNS_CAPACITY)
        val runIndexToLeading = ReusableFloatArrayList(INITIAL_RUNS_CAPACITY)
        val runIndexToHyphenWidth = ReusableFloatArrayList(INITIAL_RUNS_CAPACITY)
    }
}