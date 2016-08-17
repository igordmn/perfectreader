package com.dmi.perfectreader.fragment.book.layout

import com.dmi.perfectreader.fragment.book.content.obj.ConfiguredObject
import com.dmi.perfectreader.fragment.book.content.obj.ConfiguredParagraph
import com.dmi.perfectreader.fragment.book.content.obj.ConfiguredParagraph.Run
import com.dmi.perfectreader.fragment.book.content.obj.param.ConfiguredFontStyle
import com.dmi.perfectreader.fragment.book.content.obj.param.DefaultHangingConfig
import com.dmi.perfectreader.fragment.book.content.obj.param.HangingConfig
import com.dmi.perfectreader.fragment.book.content.obj.param.TextAlign
import com.dmi.perfectreader.fragment.book.layout.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.layout.common.LayoutSpace.Area
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutLine
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutObject
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutSpaceText
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutText
import com.dmi.perfectreader.fragment.book.layout.paragraph.ParagraphLayouter
import com.dmi.perfectreader.fragment.book.layout.paragraph.liner.Liner
import com.dmi.perfectreader.fragment.book.layout.paragraph.metrics.TextMetrics
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationRange
import com.dmi.perfectreader.fontStyle
import com.dmi.test.shouldEqual
import com.dmi.util.graphic.SizeF
import com.dmi.util.text.Chars
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.util.*

@Suppress("IllegalIdentifier")
class ParagraphLayouterTest {
    val HYPHEN_STRING = Chars.HYPHEN.toString()

    @Test
    fun `configure lines`() {
        // given
        val liner = object : Liner {
            lateinit var config: Liner.Config

            override fun makeLines(measuredText: Liner.MeasuredText, config: Liner.Config): List<Liner.Line> {
                this.config = config
                return emptyList()
            }
        }
        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(),
                liner
        )
        val hangingConfig = object : HangingConfig {
            override fun leftHangFactor(ch: Char) = 0.6F
            override fun rightHangFactor(ch: Char) = 0.5F
        }

        // when
        layouter.layout(
                ConfiguredParagraph(
                        Locale.US,
                        listOf(
                                Run.Text("text", fontStyle(), runRange(0))
                        ),
                        20F, TextAlign.LEFT, true,
                        hangingConfig, rootRange()
                ),
                rootSpace(200F, 200F)
        )

        // then
        with (liner.config) {
            firstLineIndent shouldEqual 20F
            maxWidth shouldEqual 200F
            leftHangFactor('(') shouldEqual 0.6F
            rightHangFactor(',') shouldEqual 0.5F
        }
    }

    @Test
    fun `measure text runs`() {
        // given
        val LETTER_WIDTH1 = 10F
        val SPACE_WIDTH1 = 5F
        val HYPHEN_WIDTH1 = 5F
        val style1 = fontStyle()

        val LETTER_WIDTH2 = 15F
        val SPACE_WIDTH2 = 10F
        val HYPHEN_WIDTH2 = 10F
        val style2 = fontStyle()

        val liner = object : Liner {
            lateinit var measuredText: Liner.MeasuredText

            override fun makeLines(measuredText: Liner.MeasuredText, config: Liner.Config): List<Liner.Line> {
                this.measuredText = measuredText
                return emptyList()
            }
        }
        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(mapOf(
                        style1 to TestMetrics(LETTER_WIDTH1, SPACE_WIDTH1, HYPHEN_WIDTH1),
                        style2 to TestMetrics(LETTER_WIDTH2, SPACE_WIDTH2, HYPHEN_WIDTH2)
                )),
                liner
        )

        // when
        layouter.layout(
                ConfiguredParagraph(
                        Locale.US,
                        listOf(
                                Run.Text("some ", style1, runRange(0)),
                                Run.Text("text", style2, runRange(1))
                        ),
                        20F, TextAlign.LEFT, true,
                        DefaultHangingConfig, rootRange()
                ),
                rootSpace(200F, 200F)
        )

        // then
        with (liner.measuredText) {
            plainText.toString() shouldEqual "some text"
            locale shouldEqual Locale.US
            advanceOf(0) shouldEqual LETTER_WIDTH1
            advanceOf(4) shouldEqual SPACE_WIDTH1
            advanceOf(0, 9) shouldEqual 4 * LETTER_WIDTH1 + SPACE_WIDTH1 + 4 * LETTER_WIDTH2
            advanceOf(4, 8) shouldEqual SPACE_WIDTH1 + 3 * LETTER_WIDTH2
            hyphenWidthAfter(0) shouldEqual HYPHEN_WIDTH1
            hyphenWidthAfter(4) shouldEqual HYPHEN_WIDTH1
            hyphenWidthAfter(5) shouldEqual HYPHEN_WIDTH2
            hyphenWidthAfter(8) shouldEqual HYPHEN_WIDTH2
        }
    }

    @Test
    fun `measure object runs`() {
        // given
        val object1 = configuredObj()
        val object2 = configuredObj()

        val liner = object : Liner {
            lateinit var measuredText: Liner.MeasuredText

            override fun makeLines(measuredText: Liner.MeasuredText, config: Liner.Config): List<Liner.Line> {
                this.measuredText = measuredText
                return emptyList()
            }
        }
        val layouter = ParagraphLayouter(
                childLayouter(mapOf(
                        object1 to layoutObj(30F, 0F),
                        object2 to layoutObj(100F, 0F)
                )),
                textMetrics(),
                liner
        )

        // when
        layouter.layout(
                ConfiguredParagraph(
                        Locale.US,
                        listOf(
                                Run.Object(object1),
                                Run.Text("text", fontStyle(), runRange(1)),
                                Run.Object(object2)
                        ),
                        20F, TextAlign.LEFT, true,
                        DefaultHangingConfig, rootRange()
                ),
                rootSpace(200F, 200F)
        )

        // then
        with (liner.measuredText) {
            plainText.toString() shouldEqual "\uFFFCtext\uFFFC"
            locale shouldEqual Locale.US
            advanceOf(0) shouldEqual 30F
            advanceOf(5) shouldEqual 100F
            hyphenWidthAfter(0) shouldEqual 0F
            hyphenWidthAfter(5) shouldEqual 0F
        }
    }

    @Test
    fun `layout space for object runs`() {
        // given
        val childLayouter = object : ObjectLayouter<ConfiguredObject, LayoutObject> {
            lateinit var space: LayoutSpace

            override fun layout(obj: ConfiguredObject, space: LayoutSpace): LayoutObject {
                this.space = space
                return layoutObj()
            }
        }
        val obj = configuredObj()
        val layouter = ParagraphLayouter(
                childLayouter,
                textMetrics(),
                liner()
        )

        // when
        layouter.layout(
                ConfiguredParagraph(
                        Locale.US,
                        listOf(Run.Object(obj)),
                        20F, TextAlign.LEFT, true,
                        DefaultHangingConfig, rootRange()
                ),
                rootSpace(200F, 200F)
        )

        // then
        with (childLayouter.space) {
            with (width) {
                percentBase shouldEqual 200F
                area shouldEqual Area.WrapContent(200F)
            }
            with (height) {
                percentBase shouldEqual 0F
                area shouldEqual Area.WrapContent(200F)
            }
        }
    }

    @Test
    fun `layout text runs`() {
        // given
        val LETTER_WIDTH1 = 10F
        val SPACE_WIDTH1 = 5F
        val HYPHEN_WIDTH1 = 5F
        val ASCENT1 = -8F
        val DESCENT1 = 5F
        val LEADING1 = 1F
        val style1 = fontStyle()

        val LETTER_WIDTH2 = 15F
        val SPACE_WIDTH2 = 10F
        val HYPHEN_WIDTH2 = 10F
        val ASCENT2 = -16F
        val DESCENT2 = 10F
        val LEADING2 = 4F
        val style2 = fontStyle()

        val runs = listOf(
                Run.Text("some t", style1, runRange(0)),
                Run.Text("ext words ", style2, runRange(1)),
                Run.Text(" qwerty", style1, runRange(2))
        )
        val obj = ConfiguredParagraph(Locale.US, runs, 20F, TextAlign.LEFT, true, DefaultHangingConfig, rootRange())

        val lines = listOf(
                TestLine(left = 20F, width = 100F, text = "some "),
                TestLine(left = -5F, width = 150F, text = "text words"),
                TestLine(left = 0F, width = 80F, text = "  qwert"),
                TestLine(left = 0F, width = 80F, text = "y")
        )

        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(mapOf(
                        style1 to TestMetrics(LETTER_WIDTH1, SPACE_WIDTH1, HYPHEN_WIDTH1, ASCENT1, DESCENT1, LEADING1),
                        style2 to TestMetrics(LETTER_WIDTH2, SPACE_WIDTH2, HYPHEN_WIDTH2, ASCENT2, DESCENT2, LEADING2)
                )),
                liner(lines)
        )

        // when
        val layoutObj = layouter.layout(obj, rootSpace(200F, 200F))

        // then
        with (layoutObj) {
            val HEIGHT1 = -ASCENT1 + DESCENT1
            val HEIGHT2 = -ASCENT2 + DESCENT2
            val LINE_HEIGHT1 = HEIGHT1 + LEADING1
            val LINE_HEIGHT2 = HEIGHT2 + LEADING2

            with (children[0].obj as LayoutLine) {
                childTexts shouldEqual listOf("some", " ")
                childIsSpaces shouldEqual listOf(false, true)
                childBaselines shouldEqual listOf(-ASCENT1, -ASCENT1)
                childStyles shouldEqual listOf(style1, style1)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(4, LETTER_WIDTH1),
                        charOffsets(1, SPACE_WIDTH1)
                )

                childWidths shouldEqual listOf(4 * LETTER_WIDTH1, 1 * SPACE_WIDTH1)
                childHeights shouldEqual listOf(HEIGHT1, HEIGHT1)
                childXs shouldEqual listOf(20F, 20 + 4 * LETTER_WIDTH1)
                childYs shouldEqual listOf(LEADING1 / 2, LEADING1 / 2)

                range == LocationRange(
                        runs[0].charLocation(0),
                        runs[0].charLocation(5)
                )
                childRanges shouldEqual listOf(
                        runs[0].charRange(0, 4),
                        runs[0].charRange(4, 5)
                )
            }

            with (children[1].obj as LayoutLine) {
                childTexts shouldEqual listOf("t", "ext", " ", "words")
                childIsSpaces shouldEqual listOf(false, false, true, false)
                childBaselines shouldEqual listOf(-ASCENT1, -ASCENT2, -ASCENT2, -ASCENT2)
                childStyles shouldEqual listOf(style1, style2, style2, style2)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(1, LETTER_WIDTH1),
                        charOffsets(3, LETTER_WIDTH2),
                        charOffsets(1, SPACE_WIDTH2),
                        charOffsets(5, LETTER_WIDTH2)
                )

                childWidths shouldEqual listOf(1 * LETTER_WIDTH1, 3 * LETTER_WIDTH2, 1 * SPACE_WIDTH2, 5 * LETTER_WIDTH2)
                childHeights shouldEqual listOf(HEIGHT1, HEIGHT2, HEIGHT2, HEIGHT2)
                childXs shouldEqual listOf(
                        -5F,
                        -5F + 1 * LETTER_WIDTH1,
                        -5F + 1 * LETTER_WIDTH1 + 3 * LETTER_WIDTH2,
                        -5F + 1 * LETTER_WIDTH1 + 3 * LETTER_WIDTH2 + 1 * SPACE_WIDTH2
                )
                childYs shouldEqual listOf(LEADING2 / 2 - ASCENT2 + ASCENT1, LEADING2 / 2, LEADING2 / 2, LEADING2 / 2)

                range == LocationRange(
                        runs[0].charLocation(5),
                        runs[1].charLocation(9)
                )
                childRanges shouldEqual listOf(
                        runs[0].charRange(5, 6),
                        runs[1].charRange(0, 3),
                        runs[1].charRange(3, 4),
                        runs[1].charRange(4, 9)
                )
            }

            with (children[2].obj as LayoutLine) {
                childTexts shouldEqual listOf(" ", " ", "qwert")
                childIsSpaces shouldEqual listOf(true, true, false)
                childBaselines shouldEqual listOf(-ASCENT2, -ASCENT1, -ASCENT1)
                childStyles shouldEqual listOf(style2, style1, style1)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(1, SPACE_WIDTH2),
                        charOffsets(1, SPACE_WIDTH1),
                        charOffsets(5, LETTER_WIDTH1)
                )

                childWidths shouldEqual listOf(1 * SPACE_WIDTH2, 1 * SPACE_WIDTH1, 5 * LETTER_WIDTH1)
                childHeights shouldEqual listOf(HEIGHT2, HEIGHT1, HEIGHT1)
                childXs shouldEqual listOf(0F, SPACE_WIDTH2, SPACE_WIDTH2 + SPACE_WIDTH1)
                childYs shouldEqual listOf(LEADING2 / 2, LEADING2 / 2 - ASCENT2 + ASCENT1, LEADING2 / 2 - ASCENT2 + ASCENT1)

                range == LocationRange(
                        runs[1].charLocation(9),
                        runs[2].charLocation(6)
                )
                childRanges shouldEqual listOf(
                        runs[1].charRange(9, 10),
                        runs[2].charRange(0, 1),
                        runs[2].charRange(1, 6)
                )
            }

            with (children[3].obj as LayoutLine) {
                childTexts shouldEqual listOf("y")
                childIsSpaces shouldEqual listOf(false)
                childBaselines shouldEqual listOf(-ASCENT1)
                childStyles shouldEqual listOf(style1)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(1, LETTER_WIDTH1)
                )

                childWidths shouldEqual listOf(1 * LETTER_WIDTH1)
                childHeights shouldEqual listOf(HEIGHT1)
                childXs shouldEqual listOf(0F)
                childYs shouldEqual listOf(LEADING1 / 2)

                range == LocationRange(
                        runs[2].charLocation(6),
                        runs[2].charLocation(7)
                )
                childRanges shouldEqual listOf(
                        runs[2].charRange(6, 7)
                )
            }

            width shouldEqual 200F
            height shouldEqual 1 * LINE_HEIGHT1 + 2 * LINE_HEIGHT2 + 1 * LINE_HEIGHT1
            childWidths shouldEqual listOf(200F, 200F, 200F, 200F)
            childHeights shouldEqual listOf(LINE_HEIGHT1, LINE_HEIGHT2, LINE_HEIGHT2, LINE_HEIGHT1)
            childXs shouldEqual listOf(0F, 0F, 0F, 0F)
            childYs shouldEqual listOf(0F, LINE_HEIGHT1, LINE_HEIGHT1 + LINE_HEIGHT2, LINE_HEIGHT1 + LINE_HEIGHT2 + LINE_HEIGHT2)
            blankVerticalMargins shouldEqual listOf(LEADING1 / 2, LEADING2 / 2, LEADING2 / 2, LEADING1 / 2)
            range shouldEqual obj.range
        }

        layoutObj.children.map { it.obj }.forEach {
            (it as LayoutLine).children.map { it.obj }.forEach {
                it as LayoutText
                it.locale shouldEqual Locale.US
            }
        }
    }

    @Test
    fun `layout object runs`() {
        // given
        val CHAR_WIDTH = 10F
        val ASCENT = -20F
        val DESCENT = 6F
        val style = fontStyle()

        val object1 = configuredObj()
        val object2 = configuredObj()
        val layoutObj1 = layoutObj(50F, 10F)
        val layoutObj2 = layoutObj(70F, 70F)

        val runs = listOf(
                Run.Object(object1),
                Run.Text("text", style, runRange(1)),
                Run.Object(object2)
        )
        val lines = listOf(
                TestLine(left = 0F, width = 100F, text = "\uFFFCtext\uFFFC")
        )

        val layouter = ParagraphLayouter(
                childLayouter(mapOf(
                        object1 to layoutObj1,
                        object2 to layoutObj2
                )),
                textMetrics(mapOf(
                        style to TestMetrics(CHAR_WIDTH, CHAR_WIDTH, CHAR_WIDTH, ASCENT, DESCENT)
                )),
                liner(lines)
        )

        // when
        val layoutObj = layouter.layout(
                ConfiguredParagraph(Locale.US, runs, 20F, TextAlign.LEFT, true, DefaultHangingConfig, rootRange()),
                rootSpace(200F, 200F)
        )

        // then
        with (layoutObj) {
            with (children[0].obj as LayoutLine) {
                children[0].obj shouldEqual layoutObj1
                children[2].obj shouldEqual layoutObj2

                with (children[1].obj as LayoutText) {
                    text.toString() shouldEqual "text"
                    baseline shouldEqual 20F
                    style shouldEqual style
                }

                childWidths shouldEqual listOf(50F, 4 * CHAR_WIDTH, 70F)
                childHeights shouldEqual listOf(10F, 26F, 70F)
                childXs shouldEqual listOf(0F, 50F, 50F + 4 * CHAR_WIDTH)
                childYs shouldEqual listOf(70F - 10, 70F - 20, 70F - 70)
            }

            width shouldEqual 200F
            height shouldEqual 70F + 6F
            childWidths shouldEqual listOf(200F)
            childHeights shouldEqual listOf(70F + 6)
            childXs shouldEqual listOf(0F)
            childYs shouldEqual listOf(0F)
        }
    }

    @Test
    fun `layout hyphen`() {
        // given
        val CHAR_WIDTH1 = 10F
        val HYPHEN_WIDTH1 = 5F
        val ASCENT1 = -8F
        val DESCENT1 = 5F
        val style1 = fontStyle()

        val CHAR_WIDTH2 = 20F
        val HYPHEN_WIDTH2 = 15F
        val ASCENT2 = -16F
        val DESCENT2 = 10F
        val style2 = fontStyle()

        val object1 = configuredObj()
        val layoutObj1 = layoutObj(50F, 10F)

        val runs = listOf(
                Run.Text("text", style1, runRange(0)),
                Run.Object(object1),
                Run.Text("t", style1, runRange(2)),
                Run.Text("ext2", style2, runRange(3))
        )
        val run0 = runs[0] as Run.Text
        val run2 = runs[2] as Run.Text
        val run3 = runs[3] as Run.Text

        val obj = ConfiguredParagraph(Locale.US, runs, 20F, TextAlign.LEFT, true, DefaultHangingConfig, rootRange())

        val lines = listOf(
                TestLine(left = 0F, width = 100F, text = "t", hasHyphenAfter = true),
                TestLine(left = 0F, width = 100F, text = "ext", hasHyphenAfter = true),
                TestLine(left = 0F, width = 100F, text = "\uFFFC", hasHyphenAfter = true),
                TestLine(left = 0F, width = 100F, text = "text2", hasHyphenAfter = true)
        )

        val layouter = ParagraphLayouter(
                childLayouter(mapOf(
                        object1 to layoutObj1
                )),
                textMetrics(mapOf(
                        style1 to TestMetrics(CHAR_WIDTH1, CHAR_WIDTH1, HYPHEN_WIDTH1, ASCENT1, DESCENT1),
                        style2 to TestMetrics(CHAR_WIDTH2, CHAR_WIDTH2, HYPHEN_WIDTH2, ASCENT2, DESCENT2)
                )),
                liner(lines)
        )

        // when
        val layoutObj = layouter.layout(obj, rootSpace(200F, 200F))

        // then
        with (layoutObj) {
            val HEIGHT1 = -ASCENT1 + DESCENT1
            val HEIGHT2 = -ASCENT2 + DESCENT2

            with (children[0].obj as LayoutLine) {
                childTexts shouldEqual listOf("t", HYPHEN_STRING)
                childBaselines shouldEqual listOf(-ASCENT1, -ASCENT1)
                childStyles shouldEqual listOf(style1, style1)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(1, CHAR_WIDTH1),
                        charOffsets(1, HYPHEN_WIDTH1)
                )

                childWidths shouldEqual listOf(1 * CHAR_WIDTH1, 1 * HYPHEN_WIDTH1)
                childHeights shouldEqual listOf(HEIGHT1, HEIGHT1)
                childXs shouldEqual listOf(0F, 1 * CHAR_WIDTH1)
                childYs shouldEqual listOf(0F, 0F)

                range == LocationRange(
                        run0.charLocation(0),
                        run0.charLocation(1)
                )
                childRanges shouldEqual listOf(
                        run0.charRange(0, 1),
                        run0.charRange(1, 1)
                )
            }

            with (children[1].obj as LayoutLine) {
                childTexts shouldEqual listOf("ext", HYPHEN_STRING)
                childBaselines shouldEqual listOf(-ASCENT1, -ASCENT1)
                childStyles shouldEqual listOf(style1, style1)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(3, CHAR_WIDTH1),
                        charOffsets(1, HYPHEN_WIDTH1)
                )

                childWidths shouldEqual listOf(3 * CHAR_WIDTH1, 1 * HYPHEN_WIDTH1)
                childHeights shouldEqual listOf(HEIGHT1, HEIGHT1)
                childXs shouldEqual listOf(0F, 3 * CHAR_WIDTH1)
                childYs shouldEqual listOf(0F, 0F)

                range == LocationRange(
                        run0.charLocation(0),
                        run0.charLocation(4)
                )
                childRanges shouldEqual listOf(
                        run0.charRange(1, 4),
                        run0.charRange(4, 4)
                )
            }

            with (children[2].obj as LayoutLine) {
                children[0].obj shouldEqual layoutObj1

                childWidths shouldEqual listOf(50F)
                childHeights shouldEqual listOf(10F)
                childXs shouldEqual listOf(0F)
                childYs shouldEqual listOf(0F)
            }

            with (children[3].obj as LayoutLine) {
                childTexts shouldEqual listOf("t", "ext2", HYPHEN_STRING)
                childBaselines shouldEqual listOf(-ASCENT1, -ASCENT2, -ASCENT2)
                childStyles shouldEqual listOf(style1, style2, style2)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(1, CHAR_WIDTH1),
                        charOffsets(4, CHAR_WIDTH2),
                        charOffsets(1, HYPHEN_WIDTH2)
                )

                childWidths shouldEqual listOf(1 * CHAR_WIDTH1, 4 * CHAR_WIDTH2, 1 * HYPHEN_WIDTH2)
                childHeights shouldEqual listOf(HEIGHT1, HEIGHT2, HEIGHT2)
                childXs shouldEqual listOf(0F, 1 * CHAR_WIDTH1, 1 * CHAR_WIDTH1 + 4 * CHAR_WIDTH2)
                childYs shouldEqual listOf(8F, 0F, 0F)

                range == LocationRange(
                        run2.charLocation(0),
                        run3.charLocation(4)
                )
                childRanges shouldEqual listOf(
                        run2.charRange(0, 1),
                        run3.charRange(0, 4),
                        run3.charRange(4, 4)
                )
            }

            width shouldEqual 200F
            height shouldEqual HEIGHT1 + HEIGHT1 + 10 + HEIGHT2
            childWidths shouldEqual listOf(200F, 200F, 200F, 200F)
            childHeights shouldEqual listOf(HEIGHT1, HEIGHT1, 10F, HEIGHT2)
            childXs shouldEqual listOf(0F, 0F, 0F, 0F)
            childYs shouldEqual listOf(0F, HEIGHT1, HEIGHT1 + HEIGHT1, HEIGHT1 + HEIGHT1 + 10)
        }
    }

    @Test
    fun `align text right`() {
        // given
        val LETTER_WIDTH = 10F
        val SPACE_WIDTH = 5F
        val style = fontStyle()

        val runs = listOf(
                Run.Text(" text1   text2   text3 text4", style, runRange(0))
        )
        val lines = listOf(
                TestLine(left = -10F, width = 180F, text = " text1   text2 "),
                TestLine(left = 20F, width = 50F, text = "  t", hasHyphenAfter = true),
                TestLine(left = 0F, width = 300F, text = "ext3 text4")
        )

        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(mapOf(
                        style to TestMetrics(LETTER_WIDTH, SPACE_WIDTH, LETTER_WIDTH)
                )),
                liner(lines)
        )

        // when
        val layoutObj = layouter.layout(
                ConfiguredParagraph(Locale.US, runs, 20F, TextAlign.RIGHT, true, DefaultHangingConfig, rootRange()),
                rootSpace(200F, 200F)
        )

        // then
        with (layoutObj.children[0].obj as LayoutLine) {
            val widths = listOf(1 * SPACE_WIDTH, 5 * LETTER_WIDTH, 3 * SPACE_WIDTH, 5 * LETTER_WIDTH, 1 * SPACE_WIDTH)
            childCharOffsets shouldEqualCharOffsets listOf(
                    charOffsets(1, SPACE_WIDTH),
                    charOffsets(5, LETTER_WIDTH),
                    charOffsets(3, SPACE_WIDTH),
                    charOffsets(5, LETTER_WIDTH),
                    charOffsets(1, SPACE_WIDTH)
            )
            childWidths shouldEqual widths
            childXs shouldEqual listOf(
                    200F - 180,
                    20F + widths[0],
                    20F + widths[0] + widths[1],
                    20F + widths[0] + widths[1] + widths[2],
                    20F + widths[0] + widths[1] + widths[2] + widths[3]
            )
        }

        with (layoutObj.children[1].obj as LayoutLine) {
            val widths = listOf(2 * SPACE_WIDTH, 1 * LETTER_WIDTH, 1 * LETTER_WIDTH)
            childCharOffsets shouldEqualCharOffsets listOf(
                    charOffsets(2, SPACE_WIDTH),
                    charOffsets(1, LETTER_WIDTH),
                    charOffsets(1, LETTER_WIDTH)
            )
            childWidths shouldEqual widths
            childXs shouldEqual listOf(
                    200F - 50,
                    150F + widths[0],
                    150F + widths[0] + widths[1]
            )
        }

        with (layoutObj.children[2].obj as LayoutLine) {
            val widths = listOf(4 * LETTER_WIDTH, 1 * SPACE_WIDTH, 5 * LETTER_WIDTH)
            childCharOffsets shouldEqualCharOffsets listOf(
                    charOffsets(4, LETTER_WIDTH),
                    charOffsets(1, SPACE_WIDTH),
                    charOffsets(5, LETTER_WIDTH)
            )
            childWidths shouldEqual widths
            childXs shouldEqual listOf(
                    200F - 300,
                    -100F + widths[0],
                    -100F + widths[0] + widths[1]
            )
        }
    }

    @Test
    fun `align text center`() {
        // given
        val LETTER_WIDTH = 10F
        val SPACE_WIDTH = 5F
        val style = fontStyle()

        val runs = listOf(
                Run.Text(" text1   text2   text3 text4", style, runRange(0))
        )
        val lines = listOf(
                TestLine(left = -10F, width = 180F, text = " text1   text2 "),
                TestLine(left = 20F, width = 50F, text = "  t", hasHyphenAfter = true),
                TestLine(left = 0F, width = 300F, text = "ext3 text4")
        )

        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(mapOf(
                        style to TestMetrics(LETTER_WIDTH, SPACE_WIDTH, LETTER_WIDTH)
                )),
                liner(lines)
        )

        // when
        val layoutObj = layouter.layout(
                ConfiguredParagraph(Locale.US, runs, 20F, TextAlign.CENTER, true, DefaultHangingConfig, rootRange()),
                rootSpace(200F, 200F)
        )

        // then
        with (layoutObj.children[0].obj as LayoutLine) {
            val widths = listOf(1 * SPACE_WIDTH, 5 * LETTER_WIDTH, 3 * SPACE_WIDTH, 5 * LETTER_WIDTH, 1 * SPACE_WIDTH)
            childCharOffsets shouldEqualCharOffsets listOf(
                    charOffsets(1, SPACE_WIDTH),
                    charOffsets(5, LETTER_WIDTH),
                    charOffsets(3, SPACE_WIDTH),
                    charOffsets(5, LETTER_WIDTH),
                    charOffsets(1, SPACE_WIDTH)
            )
            childWidths shouldEqual widths
            childXs shouldEqual listOf(
                    -10F + (200 + 10 - 180) / 2,
                    5F + widths[0],
                    5F + widths[0] + widths[1],
                    5F + widths[0] + widths[1] + widths[2],
                    5F + widths[0] + widths[1] + widths[2] + widths[3]
            )
        }

        with (layoutObj.children[1].obj as LayoutLine) {
            val widths = listOf(2 * SPACE_WIDTH, 1 * LETTER_WIDTH, 1 * LETTER_WIDTH)
            childCharOffsets shouldEqualCharOffsets listOf(
                    charOffsets(2, SPACE_WIDTH),
                    charOffsets(1, LETTER_WIDTH),
                    charOffsets(1, LETTER_WIDTH)
            )
            childWidths shouldEqual widths
            childXs shouldEqual listOf(
                    20F + (200 - 20 - 50) / 2,
                    85F + widths[0],
                    85F + widths[0] + widths[1]
            )
        }

        with (layoutObj.children[2].obj as LayoutLine) {
            val widths = listOf(4 * LETTER_WIDTH, 1 * SPACE_WIDTH, 5 * LETTER_WIDTH)
            childCharOffsets shouldEqualCharOffsets listOf(
                    charOffsets(4, LETTER_WIDTH),
                    charOffsets(1, SPACE_WIDTH),
                    charOffsets(5, LETTER_WIDTH)
            )
            childWidths shouldEqual widths
            childXs shouldEqual listOf(
                    0F + (200 - 300) / 2,
                    -50F + widths[0],
                    -50F + widths[0] + widths[1]
            )
        }
    }

    @Test
    fun `align text justify`() {
        // given
        val LETTER_WIDTH = 10F
        val SPACE_WIDTH = 5F
        val style = fontStyle()

        val runs = listOf(
                Run.Text(" text1   t ext2   text3 text4  text5 ", style, runRange(0))
        )
        val lines = listOf(
                TestLine(left = -10F, width = 180F, text = " text1   t ext2 "),
                TestLine(left = 20F, width = 50F, text = "  t", hasHyphenAfter = true),
                TestLine(left = 0F, width = 300F, text = "ext3 t"),
                TestLine(left = 0F, width = 100F, text = "ext4  text5 ")
        )

        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(mapOf(
                        style to TestMetrics(LETTER_WIDTH, SPACE_WIDTH, LETTER_WIDTH)
                )),
                liner(lines)
        )

        // when
        val layoutObj = layouter.layout(
                ConfiguredParagraph(Locale.US, runs, 20F, TextAlign.JUSTIFY, true, DefaultHangingConfig, rootRange()),
                rootSpace(200F, 200F)
        )

        // then
        with (layoutObj.children[0].obj as LayoutLine)
        {
            val freeSpace = 200F - 180F + 10F
            val midSpaceWidths = (3 + 1) * SPACE_WIDTH
            val midSpaceScaleX = (freeSpace + midSpaceWidths) / midSpaceWidths

            val widths = listOf(
                    1 * SPACE_WIDTH,
                    5 * LETTER_WIDTH,
                    3 * SPACE_WIDTH * midSpaceScaleX,
                    1 * LETTER_WIDTH,
                    1 * SPACE_WIDTH * midSpaceScaleX,
                    4 * LETTER_WIDTH,
                    1 * SPACE_WIDTH
            )
            childCharOffsetsOrNull shouldEqualCharOffsets listOf(
                    charOffsets(1, SPACE_WIDTH),
                    charOffsets(5, LETTER_WIDTH),
                    charOffsets(3, SPACE_WIDTH * midSpaceScaleX),
                    charOffsets(1, LETTER_WIDTH),
                    charOffsets(1, SPACE_WIDTH * midSpaceScaleX),
                    charOffsets(4, LETTER_WIDTH),
                    charOffsets(1, SPACE_WIDTH)
            )
            childWidths shouldEqual widths
            childXs shouldEqual listOf(
                    -10F,
                    -10F + widths[0],
                    -10F + widths[0] + widths[1],
                    -10F + widths[0] + widths[1] + widths[2],
                    -10F + widths[0] + widths[1] + widths[2] + widths[3],
                    -10F + widths[0] + widths[1] + widths[2] + widths[3] + widths[4],
                    -10F + widths[0] + widths[1] + widths[2] + widths[3] + widths[4] + widths[5]
            )
        }

        with (layoutObj.children[1].obj as LayoutLine)
        {
            val widths = listOf(2 * SPACE_WIDTH, 1 * LETTER_WIDTH, 1 * LETTER_WIDTH)
            childCharOffsetsOrNull shouldEqualCharOffsets listOf(
                    charOffsets(2, SPACE_WIDTH),
                    charOffsets(1, LETTER_WIDTH),
                    charOffsets(1, LETTER_WIDTH)
            )
            childWidths shouldEqual widths
            childXs shouldEqual listOf(
                    20F,
                    20F + widths[0],
                    20F + widths[0] + widths[1]
            )
        }

        with (layoutObj.children[2].obj as LayoutLine)
        {
            val widths = listOf(4 * LETTER_WIDTH, 1 * SPACE_WIDTH, 1 * LETTER_WIDTH)
            childCharOffsetsOrNull shouldEqualCharOffsets listOf(
                    charOffsets(4, LETTER_WIDTH),
                    charOffsets(1, SPACE_WIDTH),
                    charOffsets(1, LETTER_WIDTH)
            )
            childWidths shouldEqual widths
            childXs shouldEqual listOf(
                    0F,
                    widths[0],
                    widths[0] + widths[1]
            )
        }

        with (layoutObj.children[3].obj as LayoutLine)
        {
            val widths = listOf(4 * LETTER_WIDTH, 2 * SPACE_WIDTH, 5 * LETTER_WIDTH, 1 * SPACE_WIDTH)
            childCharOffsetsOrNull shouldEqualCharOffsets listOf(
                    charOffsets(4, LETTER_WIDTH),
                    charOffsets(2, SPACE_WIDTH),
                    charOffsets(5, LETTER_WIDTH),
                    charOffsets(1, SPACE_WIDTH)
            )
            childWidths shouldEqual widths
            childXs shouldEqual listOf(
                    0F,
                    widths[0],
                    widths[0] + widths[1],
                    widths[0] + widths[1] + widths[2]
            )
        }
    }

    @Test
    fun `layout empty paragraph`() {
        // given
        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(),
                liner()
        )

        // when
        val layoutObj = layouter.layout(
                ConfiguredParagraph(Locale.US, listOf(), 20F, TextAlign.LEFT, true, DefaultHangingConfig, rootRange()),
                rootSpace(200F, 200F)
        )

        // then
        with (layoutObj) {
            width shouldEqual 200F
            height shouldEqual 0F
            children.size shouldEqual 0
        }
    }

    @Test
    fun `compute width when wraps content`() {
        // given
        val runs = listOf(
                Run.Text("texttexttext", fontStyle(), runRange(0))
        )
        val lines = listOf(
                TestLine(left = 30F, width = 100F, text = "text"),
                TestLine(left = -10F, width = 180F, text = "text"),
                TestLine(left = 0F, width = 50F, text = "text")
        )

        val layouter = ParagraphLayouter(
                childLayouter(),
                textMetrics(),
                liner(lines)
        )

        val maxWidth = 200F
        val layoutSpace = LayoutSpace(
                LayoutSpace.Dimension(0F, Area.WrapContent(maxWidth)),
                LayoutSpace.Dimension(0F, Area.Fixed(0F))
        )

        // when
        var layoutObj = layouter.layout(
                ConfiguredParagraph(Locale.US, runs, 20F, TextAlign.LEFT, true, DefaultHangingConfig, rootRange()),
                layoutSpace
        )

        // then
        layoutObj.width shouldEqual 170F
        layoutObj.childWidths shouldEqual listOf(170F, 170F, 170F)

        // when
        layoutObj = layouter.layout(
                ConfiguredParagraph(Locale.US, runs, 20F, TextAlign.RIGHT, true, DefaultHangingConfig, rootRange()),
                layoutSpace
        )

        // then
        layoutObj.width shouldEqual 170F
        layoutObj.childWidths shouldEqual listOf(170F, 170F, 170F)
        layoutObj.children[0].obj.childXs shouldEqual listOf(170F - 100)
        layoutObj.children[1].obj.childXs shouldEqual listOf(170F - 180)
        layoutObj.children[2].obj.childXs shouldEqual listOf(170F - 50)

        // when
        layoutObj = layouter.layout(
                ConfiguredParagraph(Locale.US, runs, 20F, TextAlign.CENTER, true, DefaultHangingConfig, rootRange()),
                layoutSpace
        )

        // then
        layoutObj.width shouldEqual 170F
        layoutObj.childWidths shouldEqual listOf(170F, 170F, 170F)
        layoutObj.children[0].obj.childXs shouldEqual listOf(30F + (170 - 30 - 100) / 2)
        layoutObj.children[1].obj.childXs shouldEqual listOf(-10F + (170 + 10 - 180) / 2)
        layoutObj.children[2].obj.childXs shouldEqual listOf(0F + (170 + 0 - 50) / 2)
    }

    fun childLayouter(configuredToLayoutObject: Map<ConfiguredObject, LayoutObject> = emptyMap()) =
            object : ObjectLayouter<ConfiguredObject, LayoutObject> {
                override fun layout(obj: ConfiguredObject, space: LayoutSpace) = configuredToLayoutObject[obj]!!
            }

    fun configuredObj() = object : ConfiguredObject(rootRange()) {}

    fun layoutObj(width: Float = 0F, height: Float = 0F) =
            object : LayoutObject(width, height, emptyList(), rootRange()) {}

    fun textMetrics() =
            object : TextMetrics {
                override fun charAdvances(text: CharSequence, style: ConfiguredFontStyle) = FloatArray(text.length)
                override fun verticalMetrics(style: ConfiguredFontStyle) = TextMetrics.VerticalMetrics()
            }

    fun textMetrics(styleToParams: Map<ConfiguredFontStyle, TestMetrics>) =
            object : TextMetrics {
                override fun charAdvances(text: CharSequence, style: ConfiguredFontStyle): FloatArray {
                    val params = styleToParams[style] as TestMetrics

                    if (text == HYPHEN_STRING) {
                        return floatArrayOf(params.hyphenWidth)
                    } else {
                        return text.map {
                            if (it == ' ') params.spaceWidth else params.letterWidth
                        }.toFloatArray()
                    }
                }

                @Override
                override fun verticalMetrics(style: ConfiguredFontStyle): TextMetrics.VerticalMetrics {
                    val params = styleToParams[style] as TestMetrics
                    return TextMetrics.VerticalMetrics().apply {
                        ascent = params.ascent
                        descent = params.descent
                        leading = params.leading
                    }
                }
            }


    fun liner(testLines: List<TestLine> = emptyList()) = object : Liner {
        override fun makeLines(measuredText: Liner.MeasuredText, config: Liner.Config) = lines(testLines)
    }

    fun lines(testLines: List<TestLine>): List<Liner.Line> {
        val lines = ArrayList<Liner.Line>()
        var beginIndex = 0
        for (testLine in testLines) {
            lines.add(Liner.Line().apply {
                this.left = testLine.left
                this.width = testLine.width
                this.hasHyphenAfter = testLine.hasHyphenAfter
                this.tokens.addAll(tokens(beginIndex, testLine.text))
            })
            beginIndex += testLine.text.length
        }
        return lines
    }

    fun tokens(beginIndex: Int, text: String): ArrayList<Liner.Token> {
        fun isSpace(index: Int) = text[index] == ' '

        val tokens = ArrayList<Liner.Token>()

        var begin = 0
        for (end in 1..text.length) {
            if (end == text.length || isSpace(end) != isSpace(begin)) {
                tokens.add(Liner.Token().apply {
                    this.isSpace = isSpace(begin)
                    this.beginIndex = beginIndex + begin
                    this.endIndex = beginIndex + end
                })
                begin = end
            }
        }

        return tokens
    }

    val LayoutObject.childWidths: List<Float>
        get() = children.map { it.obj.width }

    val LayoutObject.childHeights: List<Float>
        get() = children.map { it.obj.height }

    val LayoutObject.childXs: List<Float>
        get() = children.map { it.x }

    val LayoutObject.childYs: List<Float>
        get() = children.map { it.y }

    val LayoutObject.childTexts: List<String>
        get() = children.map {
            with(it.obj as LayoutText) { text.toString() }
        }

    val LayoutObject.blankVerticalMargins: List<Float?>
        get() = children.map {
            if (it.obj is LayoutLine) {
                (it.obj as LayoutLine).blankVerticalMargins
            } else {
                null
            }
        }

    val LayoutObject.childIsSpaces: List<Boolean>
        get() = children.map { it.obj is LayoutSpaceText }

    val LayoutObject.childBaselines: List<Float>
        get() = children.map {
            with (it.obj as LayoutText) { baseline }
        }

    val LayoutObject.childCharOffsets: List<FloatArray>
        get() = childCharOffsetsOrNull.map { it!! }

    val LayoutObject.childCharOffsetsOrNull: List<FloatArray?>
        get() = children.map {
            with (it.obj as LayoutText) { charOffsets }
        }

    val LayoutObject.childStyles: List<ConfiguredFontStyle>
        get() = children.map {
            with (it.obj as LayoutText) { style }
        }

    val LayoutObject.childRanges: List<LocationRange>
        get() = children.map { it.obj.range }

    fun charOffsets(textLength: Int, charWidth: Float) = (0..textLength - 1).map { charWidth * it }.toFloatArray()

    fun rootRange() = LocationRange(Location(0.0), Location(100.0))
    fun runRange(index: Int) = rootRange().subrange(
            index.toDouble() / 1000,
            (index.toDouble() + 1) / 1000
    )

    fun rootSpace(width: Float, height: Float) = LayoutSpace.root(SizeF(width, height))

    private infix fun List<FloatArray?>.shouldEqualCharOffsets(expected: List<FloatArray?>) {
        size shouldEqual expected.size
        for (i in 0..size-1) {
            assertArrayEquals("actual[$i] == expected[$i]", expected[i], this[i], 0.00001F)
        }
    }

    data class TestMetrics(
            val letterWidth: Float = 0F,
            val spaceWidth: Float = 0F,
            val hyphenWidth: Float = 0F,
            val ascent: Float = 0F,
            val descent: Float = 0F,
            val leading: Float = 0F
    )

    data class TestLine(
            val left: Float,
            val width: Float,
            val hasHyphenAfter: Boolean = false,
            val text: String
    )
}