package com.dmi.perfectreader.book.layout

import com.dmi.perfectreader.fontStyle
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.book.content.location.LocationRange
import com.dmi.perfectreader.book.content.location.subrange
import com.dmi.perfectreader.book.content.obj.ConfiguredObject
import com.dmi.perfectreader.book.content.obj.ConfiguredParagraph
import com.dmi.perfectreader.book.content.obj.ConfiguredParagraph.Run
import com.dmi.perfectreader.book.content.obj.param.ConfiguredFontStyle
import com.dmi.perfectreader.book.content.obj.param.DefaultHangingConfig
import com.dmi.perfectreader.book.content.obj.param.HangingConfig
import com.dmi.perfectreader.book.content.obj.param.TextAlign
import com.dmi.perfectreader.book.layout.common.LayoutSpace
import com.dmi.perfectreader.book.layout.common.LayoutSpace.Area
import com.dmi.perfectreader.book.layout.obj.LayoutLine
import com.dmi.perfectreader.book.layout.obj.LayoutObject
import com.dmi.perfectreader.book.layout.obj.LayoutSpaceText
import com.dmi.perfectreader.book.layout.obj.LayoutText
import com.dmi.perfectreader.book.layout.paragraph.ParagraphLayouter
import com.dmi.perfectreader.book.layout.paragraph.liner.Liner
import com.dmi.perfectreader.book.layout.paragraph.metrics.TextMetrics
import com.dmi.test.shouldBe
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
                                Run.Text("text", fontStyle(), 1F, runRange(0))
                        ),
                        20F, TextAlign.LEFT, true,
                        hangingConfig, rootRange()
                ),
                rootSpace(200F, 200F)
        )

        // then
        with (liner.config) {
            firstLineIndent shouldBe 20F
            maxWidth shouldBe 200F
            leftHangFactor('(') shouldBe 0.6F
            rightHangFactor(',') shouldBe 0.5F
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
                                Run.Text("some ", style1, 1F, runRange(0)),
                                Run.Text("text", style2, 1F, runRange(1))
                        ),
                        20F, TextAlign.LEFT, true,
                        DefaultHangingConfig, rootRange()
                ),
                rootSpace(200F, 200F)
        )

        // then
        with (liner.measuredText) {
            plainText.toString() shouldBe "some text"
            locale shouldBe Locale.US
            advanceOf(0) shouldBe LETTER_WIDTH1
            advanceOf(4) shouldBe SPACE_WIDTH1
            advanceOf(0, 9) shouldBe 4 * LETTER_WIDTH1 + SPACE_WIDTH1 + 4 * LETTER_WIDTH2
            advanceOf(4, 8) shouldBe SPACE_WIDTH1 + 3 * LETTER_WIDTH2
            hyphenWidthAfter(0) shouldBe HYPHEN_WIDTH1
            hyphenWidthAfter(4) shouldBe HYPHEN_WIDTH1
            hyphenWidthAfter(5) shouldBe HYPHEN_WIDTH2
            hyphenWidthAfter(8) shouldBe HYPHEN_WIDTH2
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
                                Run.Object(object1, 1F),
                                Run.Text("text", fontStyle(), 1F, runRange(1)),
                                Run.Object(object2, 1F)
                        ),
                        20F, TextAlign.LEFT, true,
                        DefaultHangingConfig, rootRange()
                ),
                rootSpace(200F, 200F)
        )

        // then
        with (liner.measuredText) {
            plainText.toString() shouldBe "\uFFFCtext\uFFFC"
            locale shouldBe Locale.US
            advanceOf(0) shouldBe 30F
            advanceOf(5) shouldBe 100F
            hyphenWidthAfter(0) shouldBe 0F
            hyphenWidthAfter(5) shouldBe 0F
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
                        listOf(Run.Object(obj, 1F)),
                        20F, TextAlign.LEFT, true,
                        DefaultHangingConfig, rootRange()
                ),
                rootSpace(200F, 200F)
        )

        // then
        with (childLayouter.space) {
            with (width) {
                percentBase shouldBe 200F
                area shouldBe Area.WrapContent(200F)
            }
            with (height) {
                percentBase shouldBe 0F
                area shouldBe Area.WrapContent(200F)
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
                Run.Text("some t", style1, 1F, runRange(0)),
                Run.Text("ext words ", style2, 1F, runRange(1)),
                Run.Text(" qwerty", style1, 1F, runRange(2))
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
                childTexts shouldBe listOf("some", " ")
                childIsSpaces shouldBe listOf(false, true)
                childBaselines shouldBe listOf(-ASCENT1, -ASCENT1)
                childStyles shouldBe listOf(style1, style1)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(4, LETTER_WIDTH1),
                        charOffsets(1, SPACE_WIDTH1)
                )

                childWidths shouldBe listOf(4 * LETTER_WIDTH1, 1 * SPACE_WIDTH1)
                childHeights shouldBe listOf(HEIGHT1, HEIGHT1)
                childXs shouldBe listOf(20F, 20 + 4 * LETTER_WIDTH1)
                childYs shouldBe listOf(LEADING1 / 2, LEADING1 / 2)

                range shouldBe runs[0].charLocation(0)..runs[0].charLocation(5)
                childRanges shouldBe listOf(
                        runs[0].charRange(0, 4),
                        runs[0].charRange(4, 5)
                )
            }

            with (children[1].obj as LayoutLine) {
                childTexts shouldBe listOf("t", "ext", " ", "words")
                childIsSpaces shouldBe listOf(false, false, true, false)
                childBaselines shouldBe listOf(-ASCENT1, -ASCENT2, -ASCENT2, -ASCENT2)
                childStyles shouldBe listOf(style1, style2, style2, style2)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(1, LETTER_WIDTH1),
                        charOffsets(3, LETTER_WIDTH2),
                        charOffsets(1, SPACE_WIDTH2),
                        charOffsets(5, LETTER_WIDTH2)
                )

                childWidths shouldBe listOf(1 * LETTER_WIDTH1, 3 * LETTER_WIDTH2, 1 * SPACE_WIDTH2, 5 * LETTER_WIDTH2)
                childHeights shouldBe listOf(HEIGHT1, HEIGHT2, HEIGHT2, HEIGHT2)
                childXs shouldBe listOf(
                        -5F,
                        -5F + 1 * LETTER_WIDTH1,
                        -5F + 1 * LETTER_WIDTH1 + 3 * LETTER_WIDTH2,
                        -5F + 1 * LETTER_WIDTH1 + 3 * LETTER_WIDTH2 + 1 * SPACE_WIDTH2
                )
                childYs shouldBe listOf(LEADING2 / 2 - ASCENT2 + ASCENT1, LEADING2 / 2, LEADING2 / 2, LEADING2 / 2)

                range shouldBe runs[0].charLocation(5)..runs[1].charLocation(9)
                childRanges shouldBe listOf(
                        runs[0].charRange(5, 6),
                        runs[1].charRange(0, 3),
                        runs[1].charRange(3, 4),
                        runs[1].charRange(4, 9)
                )
            }

            with (children[2].obj as LayoutLine) {
                childTexts shouldBe listOf(" ", " ", "qwert")
                childIsSpaces shouldBe listOf(true, true, false)
                childBaselines shouldBe listOf(-ASCENT2, -ASCENT1, -ASCENT1)
                childStyles shouldBe listOf(style2, style1, style1)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(1, SPACE_WIDTH2),
                        charOffsets(1, SPACE_WIDTH1),
                        charOffsets(5, LETTER_WIDTH1)
                )

                childWidths shouldBe listOf(1 * SPACE_WIDTH2, 1 * SPACE_WIDTH1, 5 * LETTER_WIDTH1)
                childHeights shouldBe listOf(HEIGHT2, HEIGHT1, HEIGHT1)
                childXs shouldBe listOf(0F, SPACE_WIDTH2, SPACE_WIDTH2 + SPACE_WIDTH1)
                childYs shouldBe listOf(LEADING2 / 2, LEADING2 / 2 - ASCENT2 + ASCENT1, LEADING2 / 2 - ASCENT2 + ASCENT1)

                range shouldBe runs[1].charLocation(9)..runs[2].charLocation(6)
                childRanges shouldBe listOf(
                        runs[1].charRange(9, 10),
                        runs[2].charRange(0, 1),
                        runs[2].charRange(1, 6)
                )
            }

            with (children[3].obj as LayoutLine) {
                childTexts shouldBe listOf("y")
                childIsSpaces shouldBe listOf(false)
                childBaselines shouldBe listOf(-ASCENT1)
                childStyles shouldBe listOf(style1)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(1, LETTER_WIDTH1)
                )

                childWidths shouldBe listOf(1 * LETTER_WIDTH1)
                childHeights shouldBe listOf(HEIGHT1)
                childXs shouldBe listOf(0F)
                childYs shouldBe listOf(LEADING1 / 2)

                range shouldBe runs[2].charLocation(6)..runs[2].charLocation(7)
                childRanges shouldBe listOf(
                        runs[2].charRange(6, 7)
                )
            }

            width shouldBe 200F
            height shouldBe 1 * LINE_HEIGHT1 + 2 * LINE_HEIGHT2 + 1 * LINE_HEIGHT1
            childWidths shouldBe listOf(200F, 200F, 200F, 200F)
            childHeights shouldBe listOf(LINE_HEIGHT1, LINE_HEIGHT2, LINE_HEIGHT2, LINE_HEIGHT1)
            childXs shouldBe listOf(0F, 0F, 0F, 0F)
            childYs shouldBe listOf(0F, LINE_HEIGHT1, LINE_HEIGHT1 + LINE_HEIGHT2, LINE_HEIGHT1 + LINE_HEIGHT2 + LINE_HEIGHT2)
            blankVerticalMargins shouldBe listOf(LEADING1 / 2, LEADING2 / 2, LEADING2 / 2, LEADING1 / 2)
            range shouldBe obj.range
        }

        layoutObj.children.map { it.obj }.forEach {
            (it as LayoutLine).children.map { it.obj }.forEach {
                it as LayoutText
                it.locale shouldBe Locale.US
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
                Run.Object(object1, 1F),
                Run.Text("text", style, 1F, runRange(1)),
                Run.Object(object2, 1F)
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
                children[0].obj shouldBe layoutObj1
                children[2].obj shouldBe layoutObj2

                with (children[1].obj as LayoutText) {
                    text.toString() shouldBe "text"
                    baseline shouldBe 20F
                    style shouldBe style
                }

                childWidths shouldBe listOf(50F, 4 * CHAR_WIDTH, 70F)
                childHeights shouldBe listOf(10F, 26F, 70F)
                childXs shouldBe listOf(0F, 50F, 50F + 4 * CHAR_WIDTH)
                childYs shouldBe listOf(70F - 10, 70F - 20, 70F - 70)
            }

            width shouldBe 200F
            height shouldBe 70F + 6F
            childWidths shouldBe listOf(200F)
            childHeights shouldBe listOf(70F + 6)
            childXs shouldBe listOf(0F)
            childYs shouldBe listOf(0F)
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
                Run.Text("text", style1, 1F, runRange(0)),
                Run.Object(object1, 1F),
                Run.Text("t", style1, 1F, runRange(2)),
                Run.Text("ext2", style2, 1F, runRange(3))
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
                childTexts shouldBe listOf("t", HYPHEN_STRING)
                childBaselines shouldBe listOf(-ASCENT1, -ASCENT1)
                childStyles shouldBe listOf(style1, style1)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(1, CHAR_WIDTH1),
                        charOffsets(1, HYPHEN_WIDTH1)
                )

                childWidths shouldBe listOf(1 * CHAR_WIDTH1, 1 * HYPHEN_WIDTH1)
                childHeights shouldBe listOf(HEIGHT1, HEIGHT1)
                childXs shouldBe listOf(0F, 1 * CHAR_WIDTH1)
                childYs shouldBe listOf(0F, 0F)

                range shouldBe run0.charLocation(0)..run0.charLocation(1)
                childRanges shouldBe listOf(
                        run0.charRange(0, 1),
                        run0.charRange(1, 1)
                )
            }

            with (children[1].obj as LayoutLine) {
                childTexts shouldBe listOf("ext", HYPHEN_STRING)
                childBaselines shouldBe listOf(-ASCENT1, -ASCENT1)
                childStyles shouldBe listOf(style1, style1)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(3, CHAR_WIDTH1),
                        charOffsets(1, HYPHEN_WIDTH1)
                )

                childWidths shouldBe listOf(3 * CHAR_WIDTH1, 1 * HYPHEN_WIDTH1)
                childHeights shouldBe listOf(HEIGHT1, HEIGHT1)
                childXs shouldBe listOf(0F, 3 * CHAR_WIDTH1)
                childYs shouldBe listOf(0F, 0F)

                range shouldBe run0.charLocation(1)..run0.charLocation(4)
                childRanges shouldBe listOf(
                        run0.charRange(1, 4),
                        run0.charRange(4, 4)
                )
            }

            with (children[2].obj as LayoutLine) {
                children[0].obj shouldBe layoutObj1

                childWidths shouldBe listOf(50F)
                childHeights shouldBe listOf(10F)
                childXs shouldBe listOf(0F)
                childYs shouldBe listOf(0F)
            }

            with (children[3].obj as LayoutLine) {
                childTexts shouldBe listOf("t", "ext2", HYPHEN_STRING)
                childBaselines shouldBe listOf(-ASCENT1, -ASCENT2, -ASCENT2)
                childStyles shouldBe listOf(style1, style2, style2)
                childCharOffsets shouldEqualCharOffsets listOf(
                        charOffsets(1, CHAR_WIDTH1),
                        charOffsets(4, CHAR_WIDTH2),
                        charOffsets(1, HYPHEN_WIDTH2)
                )

                childWidths shouldBe listOf(1 * CHAR_WIDTH1, 4 * CHAR_WIDTH2, 1 * HYPHEN_WIDTH2)
                childHeights shouldBe listOf(HEIGHT1, HEIGHT2, HEIGHT2)
                childXs shouldBe listOf(0F, 1 * CHAR_WIDTH1, 1 * CHAR_WIDTH1 + 4 * CHAR_WIDTH2)
                childYs shouldBe listOf(8F, 0F, 0F)

                range shouldBe run2.charLocation(0)..run3.charLocation(4)
                childRanges shouldBe listOf(
                        run2.charRange(0, 1),
                        run3.charRange(0, 4),
                        run3.charRange(4, 4)
                )
            }

            width shouldBe 200F
            height shouldBe HEIGHT1 + HEIGHT1 + 10 + HEIGHT2
            childWidths shouldBe listOf(200F, 200F, 200F, 200F)
            childHeights shouldBe listOf(HEIGHT1, HEIGHT1, 10F, HEIGHT2)
            childXs shouldBe listOf(0F, 0F, 0F, 0F)
            childYs shouldBe listOf(0F, HEIGHT1, HEIGHT1 + HEIGHT1, HEIGHT1 + HEIGHT1 + 10)
        }
    }

    @Test
    fun `align text right`() {
        // given
        val LETTER_WIDTH = 10F
        val SPACE_WIDTH = 5F
        val style = fontStyle()

        val runs = listOf(
                Run.Text(" text1   text2   text3 text4", style, 1F, runRange(0))
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
            childWidths shouldBe widths
            childXs shouldBe listOf(
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
            childWidths shouldBe widths
            childXs shouldBe listOf(
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
            childWidths shouldBe widths
            childXs shouldBe listOf(
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
                Run.Text(" text1   text2   text3 text4", style, 1F, runRange(0))
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
            childWidths shouldBe widths
            childXs shouldBe listOf(
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
            childWidths shouldBe widths
            childXs shouldBe listOf(
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
            childWidths shouldBe widths
            childXs shouldBe listOf(
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
                Run.Text(" text1   t ext2   text3 text4  text5 ", style, 1F, runRange(0))
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
            childWidths shouldBe widths
            childXs shouldBe listOf(
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
            childWidths shouldBe widths
            childXs shouldBe listOf(
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
            childWidths shouldBe widths
            childXs shouldBe listOf(
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
            childWidths shouldBe widths
            childXs shouldBe listOf(
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
            width shouldBe 200F
            height shouldBe 0F
            children.size shouldBe 0
        }
    }

    @Test
    fun `compute width when wraps content`() {
        // given
        val runs = listOf(
                Run.Text("texttexttext", fontStyle(), 1F, runRange(0))
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
        layoutObj.width shouldBe 170F
        layoutObj.childWidths shouldBe listOf(170F, 170F, 170F)

        // when
        layoutObj = layouter.layout(
                ConfiguredParagraph(Locale.US, runs, 20F, TextAlign.RIGHT, true, DefaultHangingConfig, rootRange()),
                layoutSpace
        )

        // then
        layoutObj.width shouldBe 170F
        layoutObj.childWidths shouldBe listOf(170F, 170F, 170F)
        layoutObj.children[0].obj.childXs shouldBe listOf(170F - 100)
        layoutObj.children[1].obj.childXs shouldBe listOf(170F - 180)
        layoutObj.children[2].obj.childXs shouldBe listOf(170F - 50)

        // when
        layoutObj = layouter.layout(
                ConfiguredParagraph(Locale.US, runs, 20F, TextAlign.CENTER, true, DefaultHangingConfig, rootRange()),
                layoutSpace
        )

        // then
        layoutObj.width shouldBe 170F
        layoutObj.childWidths shouldBe listOf(170F, 170F, 170F)
        layoutObj.children[0].obj.childXs shouldBe listOf(30F + (170 - 30 - 100) / 2)
        layoutObj.children[1].obj.childXs shouldBe listOf(-10F + (170 + 10 - 180) / 2)
        layoutObj.children[2].obj.childXs shouldBe listOf(0F + (170 + 0 - 50) / 2)
    }

    @Test
    fun `apply word spacing`() {
        // given
        val WORD_SPACING1 = 1.5F
        val LETTER_WIDTH1 = 10F
        val SPACE_WIDTH1 = 5F
        val style1 = fontStyle(wordSpacingMultiplier = WORD_SPACING1)

        val WORD_SPACING2 = 1.5F
        val LETTER_WIDTH2 = 15F
        val SPACE_WIDTH2 = 10F
        val style2 = fontStyle(wordSpacingMultiplier = WORD_SPACING2)

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
                        style1 to TestMetrics(LETTER_WIDTH1, SPACE_WIDTH1),
                        style2 to TestMetrics(LETTER_WIDTH2, SPACE_WIDTH2)
                )),
                liner
        )

        // when
        layouter.layout(
                ConfiguredParagraph(
                        Locale.US,
                        listOf(
                                Run.Text("some ", style1, 1F, runRange(0)),
                                Run.Text("text ", style2, 1F, runRange(1))
                        ),
                        20F, TextAlign.LEFT, true,
                        DefaultHangingConfig, rootRange()
                ),
                rootSpace(200F, 200F)
        )

        // then
        with (liner.measuredText) {
            advanceOf(4) shouldBe SPACE_WIDTH1 * WORD_SPACING1
            advanceOf(9) shouldBe SPACE_WIDTH2 * WORD_SPACING2
        }
    }

    @Test
    fun `apply line height multiplier`() {
        // given
        val LETTER_WIDTH1 = 10F
        val SPACE_WIDTH1 = 5F
        val HYPHEN_WIDTH1 = 5F
        val ASCENT1 = -8F
        val DESCENT1 = 5F
        val LEADING1 = 1F
        val LINE_HEIGHT_MULTIPLIER1 = 0.5F
        val style1 = fontStyle()

        val LETTER_WIDTH2 = 15F
        val SPACE_WIDTH2 = 10F
        val HYPHEN_WIDTH2 = 10F
        val ASCENT2 = -16F
        val DESCENT2 = 10F
        val LEADING2 = 4F
        val LINE_HEIGHT_MULTIPLIER2 = 1.5F
        val style2 = fontStyle()

        val runs = listOf(
                Run.Text("some t", style1, LINE_HEIGHT_MULTIPLIER1, runRange(0)),
                Run.Text("ext words ", style2, LINE_HEIGHT_MULTIPLIER2, runRange(1))
        )
        val obj = ConfiguredParagraph(Locale.US, runs, 20F, TextAlign.LEFT, true, DefaultHangingConfig, rootRange())

        val lines = listOf(
                TestLine(left = 20F, width = 100F, text = "some "),
                TestLine(left = -5F, width = 150F, text = "text words")
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
            val LINE_HEIGHT1 = (HEIGHT1 + LEADING1) * LINE_HEIGHT_MULTIPLIER1
            val LINE_HEIGHT2 = (HEIGHT2 + LEADING2) * LINE_HEIGHT_MULTIPLIER2
            val FACT_LEADING1 = LINE_HEIGHT1 - HEIGHT1
            val FACT_LEADING2 = LINE_HEIGHT2 - HEIGHT2

            with (children[0].obj as LayoutLine) {
                childBaselines shouldBe listOf(-ASCENT1, -ASCENT1)
                childHeights shouldBe listOf(HEIGHT1, HEIGHT1)
                childYs shouldBe listOf(FACT_LEADING1 / 2, FACT_LEADING1 / 2)
            }

            with (children[1].obj as LayoutLine) {
                childBaselines shouldBe listOf(-ASCENT1, -ASCENT2, -ASCENT2, -ASCENT2)
                childHeights shouldBe listOf(HEIGHT1, HEIGHT2, HEIGHT2, HEIGHT2)
                childYs shouldBe listOf(FACT_LEADING2 / 2 - ASCENT2 + ASCENT1, FACT_LEADING2 / 2, FACT_LEADING2 / 2, FACT_LEADING2 / 2)
            }

            height shouldBe LINE_HEIGHT1 + LINE_HEIGHT2
            childHeights shouldBe listOf(LINE_HEIGHT1, LINE_HEIGHT2)
            childYs shouldBe listOf(0F, LINE_HEIGHT1)
            blankVerticalMargins shouldBe listOf(0F, FACT_LEADING2 / 2)
        }

        layoutObj.children.map { it.obj }.forEach {
            (it as LayoutLine).children.map { it.obj }.forEach {
                it as LayoutText
                it.locale shouldBe Locale.US
            }
        }
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

    fun charOffsets(textLength: Int, charWidth: Float) = (0 until textLength).map { charWidth * it }.toFloatArray()

    fun rootRange() = Location(0.0)..Location(100.0)
    fun runRange(index: Int) = rootRange().subrange(
            index.toDouble() / 1000,
            (index.toDouble() + 1) / 1000
    )

    fun rootSpace(width: Float, height: Float) = LayoutSpace.root(SizeF(width, height))

    private infix fun List<FloatArray?>.shouldEqualCharOffsets(expected: List<FloatArray?>) {
        size shouldBe expected.size
        for (i in 0 until size) {
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